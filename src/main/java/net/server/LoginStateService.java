package net.server;

import client.MapleClient;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.coordinator.session.MapleSessionCoordinator;
import server.TimerManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class LoginStateService {
    private static final LoginStateService instance = new LoginStateService();
    public static LoginStateService getInstance() {return instance;}

    private final Map<MapleClient, Long> inLoginState = new HashMap<>(100);
    private final Lock srvLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER);

    public void registerLoginState(MapleClient c) {
        srvLock.lock();
        try {
            inLoginState.put(c, System.currentTimeMillis() + 600000);
        } finally {
            srvLock.unlock();
        }
    }

    public void unregisterLoginState(MapleClient c) {
        srvLock.lock();
        try {
            inLoginState.remove(c);
        } finally {
            srvLock.unlock();
        }
    }

    void disconnectIdlesOnLoginTask() {
        TimerManager.getInstance().register(this::disconnectIdlesOnLoginState, 300000);
    }

    private void disconnectIdlesOnLoginState() {
        List<MapleClient> toDisconnect = new LinkedList<>();

        srvLock.lock();
        try {
            long timeNow = System.currentTimeMillis();

            for (Map.Entry<MapleClient, Long> mc : inLoginState.entrySet()) {
                if (timeNow > mc.getValue()) {
                    toDisconnect.add(mc.getKey());
                }
            }

            for (MapleClient c : toDisconnect) {
                inLoginState.remove(c);
            }
        } finally {
            srvLock.unlock();
        }

        for (MapleClient c : toDisconnect) {    // thanks Lei for pointing a deadlock issue with srvLock
            if (c.isLoggedIn()) {
                c.disconnect(false, false);
            } else {
                MapleSessionCoordinator.getInstance().closeSession(c.getSession(), true);
            }
        }
    }
}
