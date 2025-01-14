package net.server;

import abstraction.dao.PlayerNpcFieldGateway;
import client.MapleCharacter;
import config.YamlConfig;
import constants.game.GameConstants;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.channel.Channel;
import net.server.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.life.MaplePlayerNPCFactory;
import tools.DatabaseConnection;
import tools.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WorldServer {
    private static final Logger logger = LoggerFactory.getLogger(WorldServer.class);
    private static final WorldServer instance = new WorldServer();
    public static WorldServer getInstance() {
        return instance;
    }

    private final PlayerNpcFieldGateway playerNpcFieldGateway = PlayerNpcFieldGateway.getInstance();
    private List<World> worlds = new ArrayList<>();
    private List<Map<Integer, String>> channels = new LinkedList<>();
    private final ReentrantReadWriteLock wldLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_WORLDS, true);
    private final ReentrantReadWriteLock.ReadLock wldRLock = wldLock.readLock();
    private final ReentrantReadWriteLock.WriteLock wldWLock = wldLock.writeLock();
    private List<Pair<Integer, String>> worldRecommendedList = new LinkedList<>();
    private final List<List<Pair<String, Integer>>> playerRanking = new LinkedList<>();

    public List<Pair<Integer, String>> getWorldRecommendedList() {
        return worldRecommendedList;
    }

    public World getWorld(int id) {
        wldRLock.lock();
        try {
            return id >= 0 && id < worlds.size() ? worlds.get(id) : null;
        } finally {
            wldRLock.unlock();
        }
    }

    public List<Pair<String, Integer>> getWorldPlayerRanking(int worldId) {
        wldRLock.lock();
        try {
            return new ArrayList<>(playerRanking.get(!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING ? worldId : 0));
        } finally {
            wldRLock.unlock();
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private int initWorld() {
        wldWLock.lock();
        try {
            int i = worlds.size();

            if (i >= YamlConfig.config.server.WLDLIST_SIZE) {
                return -1;
            }

            logger.info("Starting world " + i);

            int exprate = YamlConfig.config.worlds.get(i).exp_rate;
            int mesorate = YamlConfig.config.worlds.get(i).meso_rate;
            int droprate = YamlConfig.config.worlds.get(i).drop_rate;
            int bossdroprate = YamlConfig.config.worlds.get(i).boss_drop_rate;
            int questrate = YamlConfig.config.worlds.get(i).quest_rate;
            int travelrate = YamlConfig.config.worlds.get(i).travel_rate;
            int fishingrate = YamlConfig.config.worlds.get(i).fishing_rate;

            int flag = YamlConfig.config.worlds.get(i).flag;
            String event_message = YamlConfig.config.worlds.get(i).event_message;
            String why_am_i_recommended = YamlConfig.config.worlds.get(i).why_am_i_recommended;

            World world = new World(i,
                flag,
                event_message,
                exprate, droprate, bossdroprate, mesorate, questrate, travelrate, fishingrate);

            worldRecommendedList.add(new Pair<>(i, why_am_i_recommended));
            worlds.add(world);

            Map<Integer, String> channelInfo = new HashMap<>();
            long bootTime = ServerTimer.getInstance().getCurrentTime();
            for (int channelId = 1; channelId <= YamlConfig.config.worlds.get(i).channels; channelId++) {
                Channel channel = new Channel(i, channelId, bootTime);

                world.addChannel(channel);
                channelInfo.put(channelId, channel.getIP());
            }

            channels.add(i, channelInfo);

            world.setServerMessage(YamlConfig.config.worlds.get(i).server_message);

            logger.info("Finished loading world " + i);
            return i;
        } finally {
            wldWLock.unlock();
        }
    }

    public Channel getChannel(int world, int channel) {
        // TODO: This method is unsafe. Because the channel is not synchronized.
        try {
            return getWorld(world).getChannel(channel);
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public List<Channel> getChannelsFromWorld(int world) {
        try {
            return getWorld(world).getChannels();
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    public List<Channel> getAllChannels() {
        try {
            List<Channel> allChannels = new ArrayList<>();
            for (World world : getWorlds()) {
                allChannels.addAll(world.getChannels());
            }
            return allChannels;
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    public List<World> getWorlds() {
        wldRLock.lock();
        try {
            return Collections.unmodifiableList(worlds);
        } finally {
            wldRLock.unlock();
        }
    }

    public int getWorldsSize() {
        wldRLock.lock();
        try {
            return worlds.size();
        } finally {
            wldRLock.unlock();
        }
    }

    public Set<Integer> getOpenChannels(int world) {
        wldRLock.lock();
        try {
            return new HashSet<>(channels.get(world).keySet());
        } finally {
            wldRLock.unlock();
        }
    }

    private String getIP(int world, int channel) {
        wldRLock.lock();
        try {
            return channels.get(world).get(channel);
        } finally {
            wldRLock.unlock();
        }
    }

    public String[] getInetSocket(int world, int channel) {
        try {
            return getIP(world, channel).split(":");
        } catch (Exception e) {
            return null;
        }
    }

    private void resetServerWorlds() {  // thanks maple006 for noticing proprietary lists assigned to null
        wldWLock.lock();
        try {
            worlds.clear();
            channels.clear();
            worldRecommendedList.clear();
        } finally {
            wldWLock.unlock();
        }
    }

    private List<Pair<Integer, List<Pair<String, Integer>>>> updatePlayerRankingFromDB(int worldid) {
        List<Pair<Integer, List<Pair<String, Integer>>>> rankSystem = new ArrayList<>();
        List<Pair<String, Integer>> rankUpdate = new ArrayList<>(0);

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();

            String worldQuery;
            if (!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
                if (worldid >= 0) {
                    worldQuery = (" AND `characters`.`world` = " + worldid);
                } else {
                    worldQuery = (" AND `characters`.`world` >= 0 AND `characters`.`world` <= " + -worldid);
                }
            } else {
                worldQuery = (" AND `characters`.`world` >= 0 AND `characters`.`world` <= " + Math.abs(worldid));
            }

            ps = con.prepareStatement("SELECT `characters`.`name`, `characters`.`level`, `characters`.`world` FROM `characters` LEFT JOIN accounts " +
                "ON accounts.id = characters.accountid WHERE `characters`.`gm` < 2 AND `accounts`.`banned` = '0'" + worldQuery + " ORDER BY " + (!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING ? "world, " : "") + "level DESC, exp DESC, lastExpGainTime ASC LIMIT 50");
            rs = ps.executeQuery();

            if (!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
                int currentWorld = -1;
                while (rs.next()) {
                    int rsWorld = rs.getInt("world");
                    if (currentWorld < rsWorld) {
                        currentWorld = rsWorld;
                        rankUpdate = new ArrayList<>(50);
                        rankSystem.add(new Pair<>(rsWorld, rankUpdate));
                    }

                    rankUpdate.add(new Pair<>(rs.getString("name"), rs.getInt("level")));
                }
            } else {
                rankUpdate = new ArrayList<>(50);
                rankSystem.add(new Pair<>(0, rankUpdate));

                while (rs.next()) {
                    rankUpdate.add(new Pair<>(rs.getString("name"), rs.getInt("level")));
                }
            }

            ps.close();
            rs.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rankSystem;
    }

    public void updateWorldPlayerRanking() {
        List<Pair<Integer, List<Pair<String, Integer>>>> rankUpdates = updatePlayerRankingFromDB(-1 * (getWorldsSize() - 1));
        if (!rankUpdates.isEmpty()) {
            wldWLock.lock();
            try {
                if (!YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
                    for (int i = playerRanking.size(); i <= rankUpdates.get(rankUpdates.size() - 1).getLeft(); i++) {
                        playerRanking.add(new ArrayList<>(0));
                    }

                    for (Pair<Integer, List<Pair<String, Integer>>> wranks : rankUpdates) {
                        playerRanking.set(wranks.getLeft(), wranks.getRight());
                    }
                } else {
                    playerRanking.set(0, rankUpdates.get(0).getRight());
                }
            } finally {
                wldWLock.unlock();
            }
        }
    }

    private void initWorldPlayerRanking() {
        if (YamlConfig.config.server.USE_WHOLE_SERVER_RANKING) {
            playerRanking.add(new ArrayList<>(0));
        }
        updateWorldPlayerRanking();
    }

    public void broadcastMessage(int world, final byte[] packet) {
        for (Channel ch : getChannelsFromWorld(world)) {
            ch.broadcastPacket(packet);
        }
    }

    public void broadcastGMMessage(int world, final byte[] packet) {
        for (Channel ch : getChannelsFromWorld(world)) {
            ch.broadcastGMPacket(packet);
        }
    }

    public boolean isGmOnline(int world) {
        for (Channel ch : getChannelsFromWorld(world)) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadPlayerNpcMapStepFromDb() {
        try {
            List<World> worldList = getWorlds();
            playerNpcFieldGateway.forEach(playerNpcField -> {
                World w = worldList.get(playerNpcField.getWorld());
                if (w != null) {
                    w.setPlayerNpcMapData(playerNpcField.getMap(),
                        playerNpcField.getStep(),
                        playerNpcField.getPodium());
                }
            });
        } catch (SQLException e) {
            logger.error("Error occurred while loading player npc map step from db.", e);
        }
    }

    public void initialize() {
        int worldCount = Math.min(GameConstants.WORLD_NAMES.length, YamlConfig.config.server.WORLDS);
        for (int i = 0; i < worldCount; i++) {
            initWorld();
        }
        initWorldPlayerRanking();
        MaplePlayerNPCFactory.loadFactoryMetadata();
        loadPlayerNpcMapStepFromDb();

        for (Channel ch : getAllChannels()) {
            ch.reloadEventScriptManager();
        }
    }

    public void shutdown() {
        if (getWorlds() == null) return; // already shutdown
        for (World w : getWorlds()) {
            w.shutdown();
        }

        List<Channel> allChannels = getAllChannels();
        for (Channel ch : allChannels) {
            while (!ch.finishedShutdown()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    logger.error("Thread interrupted while shutting down channel.", ie);
                }
            }
        }

        resetServerWorlds();
    }
}
