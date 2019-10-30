package net.server;

import client.MapleCharacter;
import client.MapleClient;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class PlayerDiseasesService {
    private static final PlayerDiseasesService instance = new PlayerDiseasesService();
    public static PlayerDiseasesService getInstance() {return instance;}

    private final List<MapleClient> processDiseaseAnnouncePlayers = new LinkedList<>();
    private final List<MapleClient> registeredDiseaseAnnouncePlayers = new LinkedList<>();
    private final Lock disLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_DISEASES);

    public void runAnnouncePlayerDiseasesSchedule() {
        List<MapleClient> processDiseaseAnnounceClients;
        disLock.lock();
        try {
            processDiseaseAnnounceClients = new LinkedList<>(processDiseaseAnnouncePlayers);
            processDiseaseAnnouncePlayers.clear();
        } finally {
            disLock.unlock();
        }

        while (!processDiseaseAnnounceClients.isEmpty()) {
            MapleClient c = processDiseaseAnnounceClients.remove(0);
            MapleCharacter player = c.getPlayer();
            if (player != null && player.isLoggedinWorld()) {
                player.announceDiseases();
                player.collectDiseases();
            }
        }

        disLock.lock();
        try {
            // this is to force the system to wait for at least one complete tick before releasing disease info for the registered clients
            while (!registeredDiseaseAnnouncePlayers.isEmpty()) {
                MapleClient c = registeredDiseaseAnnouncePlayers.remove(0);
                processDiseaseAnnouncePlayers.add(c);
            }
        } finally {
            disLock.unlock();
        }
    }

    public void registerAnnouncePlayerDiseases(MapleClient c) {
        disLock.lock();
        try {
            registeredDiseaseAnnouncePlayers.add(c);
        } finally {
            disLock.unlock();
        }
    }
}
