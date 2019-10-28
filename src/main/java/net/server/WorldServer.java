package net.server;

import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.channel.Channel;
import net.server.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Pair;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class WorldServer {
    private static final Logger logger = LoggerFactory.getLogger(WorldServer.class);

    private List<World> worlds = new ArrayList<>();
    private List<Map<Integer, String>> channels = new LinkedList<>();
    private final ReentrantReadWriteLock wldLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_WORLDS, true);
    private final ReentrantReadWriteLock.ReadLock wldRLock = wldLock.readLock();
    private final ReentrantReadWriteLock.WriteLock wldWLock = wldLock.writeLock();
    public List<Pair<Integer, String>> worldRecommendedList = new LinkedList<>();

    List<World> getWorlds() {
        return worlds;
    }

    List<Map<Integer, String>> getChannels() {
        return channels;
    }

    ReentrantReadWriteLock.ReadLock getWldRLock() {
        return wldRLock;
    }

    ReentrantReadWriteLock.WriteLock getWldWLock() {
        return wldWLock;
    }

    public World getWorld(int id) {
        wldRLock.lock();
        try {
            return id >= 0 && id < worlds.size() ? worlds.get(id) : null;
        } finally {
            wldRLock.unlock();
        }
    }

    int initWorld() {
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

    public int addChannel(int worldid) {
        wldWLock.lock();
        try {
            if (worldid >= worlds.size()) return -3;

            Map<Integer, String> worldChannels = channels.get(worldid);
            if (worldChannels == null) return -3;

            int channelid = worldChannels.size();
            if (channelid >= YamlConfig.config.server.CHANNEL_SIZE) return -2;

            channelid++;
            World world = getWorld(worldid);
            Channel channel = new Channel(worldid, channelid, ServerTimer.getInstance().getCurrentTime());

            channel.setServerMessage(YamlConfig.config.worlds.get(worldid).why_am_i_recommended);

            world.addChannel(channel);
            worldChannels.put(channelid, channel.getIP());

            return channelid;
        } finally {
            wldWLock.unlock();
        }
    }

    public boolean removeChannel(int worldid) {   //lol don't!
        wldWLock.lock();
        try {
            if (worldid >= worlds.size()) return false;

            World world = worlds.get(worldid);
            if (world != null) {
                int channel = world.removeChannel();

                Map<Integer, String> m = channels.get(worldid);
                if (m != null) m.remove(channel);

                return channel > -1;
            }
        } finally {
            wldWLock.unlock();
        }

        return false;
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
            for (World world : getWorldsSync()) {
                allChannels.addAll(world.getChannels());
            }
            return allChannels;
        } catch (NullPointerException npe) {
            return new ArrayList<>(0);
        }
    }

    public List<World> getWorldsSync() {
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

    void resetServerWorlds() {  // thanks maple006 for noticing proprietary lists assigned to null
        wldWLock.lock();
        try {
            worlds.clear();
            channels.clear();
            worldRecommendedList.clear();
        } finally {
            wldWLock.unlock();
        }
    }
}
