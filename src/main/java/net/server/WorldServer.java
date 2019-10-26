package net.server;

import net.server.world.World;

import java.util.ArrayList;
import java.util.List;

public class WorldServer {
    private List<World> worlds = new ArrayList<>();

    public List<World> getWorlds() {
        return worlds;
    }
}
