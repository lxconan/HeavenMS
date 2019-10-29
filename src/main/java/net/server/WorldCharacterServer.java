package net.server;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFactory;
import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.world.World;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DatabaseConnection;
import tools.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WorldCharacterServer {
    private static final Logger logger = LoggerFactory.getLogger(WorldCharacterServer.class);

    private final WorldServer worldServer = WorldServer.getInstance();
    private final ReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
    private final ReentrantReadWriteLock.ReadLock lgnRLock = lgnLock.readLock();
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

    public Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharlist(
        Integer accountId, int visibleWorlds) {
        List<World> wlist = worldServer.getWorlds();
        if (wlist.size() > visibleWorlds) wlist = wlist.subList(0, visibleWorlds);
        return loadAccountCharacterList(accountId, wlist);
    }

    private Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharacterList(
        Integer accountId, List<World> wlist) {
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

    public boolean hasCharacterInTransition(IoSession session) {
        if (!YamlConfig.config.server.USE_IP_VALIDATION) {
            return true;
        }

        String remoteIp = MapleSessionCoordinator.getSessionRemoteAddress(session);

        lgnRLock.lock();
        try {
            return transitioningChars.containsKey(remoteIp);
        } finally {
            lgnRLock.unlock();
        }
    }

    public void updateCharacterEntry(MapleCharacter chr) {
        MapleCharacter chrView = chr.generateCharacterEntry();
        World world = worldServer.getWorld(chrView.getWorld());
        updateCharacterEntry(chrView, world);
    }

    private void updateCharacterEntry(MapleCharacter chrView, World world) {
        lgnWLock.lock();
        try {
            final int characterId = chrView.getId();
            if (!worldChars.containsKey(characterId)) {
                logger.warn("Attempt to update character entry but the character is out of sync with LoginServer. Character Id: " + characterId);
            }

            final int accountID = chrView.getAccountID();
            final Set<Integer> characters = accountChars.get(accountID);
            if (characters == null) {
                logger.warn("Attempt to update character entry but the account is out of sync with LoginServer. Account Id: " + accountID);
            }

            if (characters != null && !characters.contains(characterId)) {
                logger.warn("Attempt to update character entry but the account character is out of sync with LoginServer. Character Id: " + characterId);
            }

            if (world != null) { world.registerAccountCharacterView(accountID, chrView); }
        } finally {
            lgnWLock.unlock();
        }
    }

    public void createCharacterEntry(MapleCharacter chr) {
        World world = worldServer.getWorld(chr.getWorld());
        createCharacterEntry(chr, world);
    }

    private void createCharacterEntry(MapleCharacter chr, World world) {
        Integer accountId = chr.getAccountID();
        Integer characterId = chr.getId();
        int worldId = chr.getWorld();

        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountId, (short) (accountCharacterCount.get(accountId) + 1));

            Set<Integer> accChars = accountChars.get(accountId);
            accChars.add(characterId);

            worldChars.put(characterId, worldId);

            MapleCharacter chrView = chr.generateCharacterEntry();
            if (world != null) world.registerAccountCharacterView(chrView.getAccountID(), chrView);
        } finally {
            lgnWLock.unlock();
        }
    }

    public void deleteCharacterEntry(Integer accountId, Integer characterId) {
        lgnWLock.lock();
        try {
            accountCharacterCount.put(accountId, (short) (accountCharacterCount.get(accountId) - 1));

            Set<Integer> accChars = accountChars.get(accountId);
            accChars.remove(characterId);

            Integer world = worldChars.remove(characterId);
            if (world != null) {
                World wserv = worldServer.getWorld(world);
                if (wserv != null) wserv.unregisterAccountCharacterView(accountId, characterId);
            }
        } finally {
            lgnWLock.unlock();
        }
    }

    public int loadAccountCharactersView(Integer accId, int gmLevel, int fromWorldid) {    // returns the maximum gmLevel found
        List<World> wlist = worldServer.getWorlds();
        Pair<Short, List<List<MapleCharacter>>> accCharacters = loadAccountCharactersViewFromDb(accId, wlist.size());

        lgnWLock.lock();
        try {
            List<List<MapleCharacter>> accChars = accCharacters.getRight();
            accountCharacterCount.put(accId, accCharacters.getLeft());

            Set<Integer> chars = accountChars.get(accId);
            if (chars == null) {
                chars = new HashSet<>(5);
            }

            for (int wid = fromWorldid; wid < wlist.size(); wid++) {
                World w = wlist.get(wid);
                List<MapleCharacter> wchars = accChars.get(wid);
                w.loadAccountCharactersView(accId, wchars);

                for (MapleCharacter chr : wchars) {
                    int cid = chr.getId();
                    if (gmLevel < chr.gmLevel()) gmLevel = chr.gmLevel();

                    chars.add(cid);
                    worldChars.put(cid, wid);
                }
            }

            accountChars.put(accId, chars);
        } finally {
            lgnWLock.unlock();
        }

        return gmLevel;
    }

    private Pair<Short, List<List<MapleCharacter>>> loadAccountCharactersViewFromDb(int accId, int wlen) {
        short characterCount = 0;
        List<List<MapleCharacter>> wchars = new ArrayList<>(wlen);
        for (int i = 0; i < wlen; i++) wchars.add(i, new LinkedList<>());

        List<MapleCharacter> chars = new LinkedList<>();
        int curWorld = 0;
        try {
            List<Pair<Item, Integer>> accEquips = ItemFactory.loadEquippedItems(accId, true, true);
            Map<Integer, List<Item>> accPlayerEquips = new HashMap<>();

            for (Pair<Item, Integer> ae : accEquips) {
                List<Item> playerEquips = accPlayerEquips.get(ae.getRight());
                if (playerEquips == null) {
                    playerEquips = new LinkedList<>();
                    accPlayerEquips.put(ae.getRight(), playerEquips);
                }

                playerEquips.add(ae.getLeft());
            }

            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY world, id")) {
                ps.setInt(1, accId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        characterCount++;

                        int cworld = rs.getByte("world");
                        if (cworld >= wlen) continue;

                        if (cworld > curWorld) {
                            wchars.add(curWorld, chars);

                            curWorld = cworld;
                            chars = new LinkedList<>();
                        }

                        Integer cid = rs.getInt("id");
                        chars.add(MapleCharacter.loadCharacterEntryFromDB(rs, accPlayerEquips.get(cid)));
                    }
                }
            }
            con.close();

            wchars.add(curWorld, chars);
        } catch (SQLException error) {
            logger.error("Unhandled SQL exception while load account character from DB.", error);
        }

        return new Pair<>(characterCount, wchars);
    }

    public void loadAccountStorages(MapleClient c) {
        int accountId = c.getAccID();
        Set<Integer> accWorlds = new HashSet<>();
        lgnWLock.lock();
        try {
            Set<Integer> chars = accountChars.get(accountId);

            for (Integer cid : chars) {
                Integer worldid = worldChars.get(cid);
                if (worldid != null) {
                    accWorlds.add(worldid);
                }
            }
        } finally {
            lgnWLock.unlock();
        }

        List<World> worldList = worldServer.getWorlds();
        for (Integer worldid : accWorlds) {
            if (worldid < worldList.size()) {
                World wserv = worldList.get(worldid);
                wserv.registerAccountStorage(accountId);
            }
        }
    }
}
