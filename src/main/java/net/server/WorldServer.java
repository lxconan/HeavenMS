package net.server;

import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.channel.Channel;
import net.server.world.World;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class WorldServer {
    private List<World> worlds = new ArrayList<>();
    private List<Map<Integer, String>> channels = new LinkedList<>();
    private final ReentrantReadWriteLock wldLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_WORLDS, true);
    private final ReentrantReadWriteLock.ReadLock wldRLock = wldLock.readLock();
    private final ReentrantReadWriteLock.WriteLock wldWLock = wldLock.writeLock();

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
}
