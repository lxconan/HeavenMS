package net.server;

import client.MapleClient;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class LoginStateService {
    private static final LoginStateService instance = new LoginStateService();
    public static LoginStateService getInstance() {return instance;}

    public final Map<MapleClient, Long> inLoginState = new HashMap<>(100);
    public final Lock srvLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER);
}
