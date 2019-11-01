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
}
