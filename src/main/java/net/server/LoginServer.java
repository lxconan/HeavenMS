package net.server;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.world.World;
import tools.Pair;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoginServer {
    private final ReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
    public final ReentrantReadWriteLock.ReadLock lgnRLock = lgnLock.readLock();
    public final ReentrantReadWriteLock.WriteLock lgnWLock = lgnLock.writeLock();
    public final Map<Integer, Integer> worldChars = new HashMap<>();
    public final Map<Integer, Set<Integer>> accountChars = new HashMap<>();
    public final Map<Integer, Short> accountCharacterCount = new HashMap<>();
    public final Map<String, Integer> transitioningChars = new HashMap<>();

    public boolean isFirstAccountLogin(Integer accId) {
        lgnRLock.lock();
        try {
            return !accountChars.containsKey(accId);
        } finally {
            lgnRLock.unlock();
        }
    }

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

    public Set<Integer> getAccountCharacterEntries(Integer accountid) {
        lgnRLock.lock();
        try {
            return new HashSet<>(accountChars.get(accountid));
        } finally {
            lgnRLock.unlock();
        }
    }

    public Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharacterList(Integer accountId, List<World> wlist) {
        List<Pair<Integer, List<MapleCharacter>>> accChars = new ArrayList<>(wlist.size() + 1);
        int chrTotal = 0;
        List<MapleCharacter> lastwchars = null;

        lgnRLock.lock();
        try {
            for (World w : wlist) {
                List<MapleCharacter> wchars = w.getAccountCharactersView(accountId);
                if (wchars == null) {
                    if (!accountChars.containsKey(accountId)) {
                        accountCharacterCount.put(accountId, (short) 0);
                        accountChars.put(accountId, new HashSet<Integer>());    // not advisable at all to write on the map on a read-protected
                                                                                // environment
                    }                                                           // yet it's known there's no problem since no other point in the
                                                                                // source does
                } else if (!wchars.isEmpty()) {                                 // this action.
                    lastwchars = wchars;

                    accChars.add(new Pair<>(w.getId(), wchars));
                    chrTotal += wchars.size();
                }
            }
        } finally {
            lgnRLock.unlock();
        }

        return new Pair<>(new Pair<>(chrTotal, lastwchars), accChars);
    }

    public Set<Integer> getWorldsForAccount(Integer accountId) {
        Set<Integer> accWorlds = new HashSet<>();

        lgnRLock.lock();
        try {
            for (Integer chrid : getAccountCharacterEntries(accountId)) {
                accWorlds.add(worldChars.get(chrid));
            }
        } finally {
            lgnRLock.unlock();
        }

        return accWorlds;
    }
}
