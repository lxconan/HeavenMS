package net.server;

import net.server.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorldServer {
    private List<World> worlds = new ArrayList<>();
    private List<Map<Integer, String>> channels = new LinkedList<>();

    public List<World> getWorlds() {
        return worlds;
    }

    public List<Map<Integer, String>> getChannels() {
        return channels;
    }
}
