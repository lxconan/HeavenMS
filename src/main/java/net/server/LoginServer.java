package net.server;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoginServer {
    private final ReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
    public final ReentrantReadWriteLock.ReadLock lgnRLock = lgnLock.readLock();
    public final ReentrantReadWriteLock.WriteLock lgnWLock = lgnLock.writeLock();
    public final Map<Integer, Integer> worldChars = new HashMap<>();
    public final Map<Integer, Set<Integer>> accountChars = new HashMap<>();
    public final Map<Integer, Short> accountCharacterCount = new HashMap<>();
    public final Map<String, Integer> transitioningChars = new HashMap<>();

    public int getCharacterWorld(Integer characterId) {
        lgnRLock.lock();
        try {
            Integer worldId = worldChars.get(characterId);
            return worldId != null ? worldId : -1;
        } finally {
            lgnRLock.unlock();
        }
    }

    public boolean haveCharacterEntry(Integer accountId, Integer characterId) {
        lgnRLock.lock();
        try {
            Set<Integer> accChars = accountChars.get(accountId);
            return accChars.contains(characterId);
        } finally {
            lgnRLock.unlock();
        }
    }

    public short getAccountCharacterCount(Integer accountId) {
        lgnRLock.lock();
        try {
            return accountCharacterCount.get(accountId);
        } finally {
            lgnRLock.unlock();
        }
    }

    public short getAccountWorldCharacterCount(Integer accountId, Integer worldId) {
        lgnRLock.lock();
        try {
            short count = 0;

            for (Integer chr : accountChars.get(accountId)) {
                if (worldChars.get(chr).equals(worldId)) {
                    count++;
                }
            }

            return count;
        } finally {
            lgnRLock.unlock();
        }
    }
}
