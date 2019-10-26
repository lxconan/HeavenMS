package net.server;

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
            List<Channel> channelz = new ArrayList<>();
            for (World world : getWorldsSync()) {
                channelz.addAll(world.getChannels());
            }
            return channelz;
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
}
