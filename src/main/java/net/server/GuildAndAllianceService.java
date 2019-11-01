package net.server;

import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;

import java.util.HashMap;
import java.util.Map;

public class GuildAndAllianceService {
    private static final GuildAndAllianceService instance = new GuildAndAllianceService();
    public static GuildAndAllianceService getInstance() {return instance;}
    public final Map<Integer, MapleGuild> guilds = new HashMap<>(100);
    public final Map<Integer, MapleAlliance> alliances = new HashMap<>(100);

    public MapleAlliance getAlliance(int id) {
        synchronized (alliances) {
            if (alliances.containsKey(id)) {
                return alliances.get(id);
            }
            return null;
        }
    }

    public void addAlliance(int id, MapleAlliance alliance) {
        synchronized (alliances) {
            if (!alliances.containsKey(id)) {
                alliances.put(id, alliance);
            }
        }
    }

    public void disbandAlliance(int id) {
        synchronized (alliances) {
            MapleAlliance alliance = alliances.get(id);
            if (alliance != null) {
                for (Integer gid : alliance.getGuilds()) {
                    guilds.get(gid).setAllianceId(0);
                }
                alliances.remove(id);
            }
        }
    }
}
