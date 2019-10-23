package abstraction.dao;

public class PlayerNpcField {
    private int world;
    private int map;
    private int step;
    private int podium;

    public PlayerNpcField(int world, int map, int step, int podium) {
        this.world = world;
        this.map = map;
        this.step = step;
        this.podium = podium;
    }

    public int getWorld() {
        return world;
    }

    public int getMap() {
        return map;
    }

    public int getStep() {
        return step;
    }

    public int getPodium() {
        return podium;
    }
}
