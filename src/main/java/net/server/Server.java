/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server;

import abstraction.ApplicationContextFactory;
import abstraction.dao.PlayerNpcFieldGateway;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamily;
import client.SkillFactory;
import client.command.CommandsExecutor;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.manipulator.MapleCashidGenerator;
import client.newyear.NewYearCardRecord;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import constants.net.OpcodeConstants;
import constants.net.ServerConstants;
import net.MapleServerHandler;
import net.mina.MapleCodecFactory;
import net.server.audit.ThreadTracker;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.session.MapleSessionCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.task.*;
import net.server.world.World;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import server.CashShop.CashItemFactory;
import server.MapleSkillbookInformationProvider;
import server.ThreadManager;
import server.TimerManager;
import server.expeditions.MapleExpeditionBossLog;
import server.life.MaplePlayerNPCFactory;
import server.quest.MapleQuest;
import tools.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private static Server instance = null;

    public static Server getInstance() {
        if (instance == null) {
            final ApplicationContext context = ApplicationContextFactory.getInstance();
            Server.instance = new Server(
                context.getBean(PlayerNpcFieldGateway.class));
        }
        return instance;
    }

    private final Set<Integer> activeFly = new HashSet<>();
    private final Map<Integer, Integer> couponRates = new HashMap<>(30);
    private final List<Integer> activeCoupons = new LinkedList<>();

    private IoAcceptor acceptor;
    private final WorldServer worldServer = WorldServer.getInstance();
    private final Properties subnetInfo = new Properties();

    private final Map<Integer, MapleGuild> guilds = new HashMap<>(100);
    private final Map<MapleClient, Long> inLoginState = new HashMap<>(100);

    private final PlayerBuffStorage buffStorage = new PlayerBuffStorage();
    private final Map<Integer, MapleAlliance> alliances = new HashMap<>(100);
    private final Map<Integer, NewYearCardRecord> newyears = new HashMap<>();
    private final List<MapleClient> processDiseaseAnnouncePlayers = new LinkedList<>();
    private final List<MapleClient> registeredDiseaseAnnouncePlayers = new LinkedList<>();

    private final Lock srvLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER);
    private final Lock disLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.SERVER_DISEASES);

    private final LoginServer loginServer = new LoginServer();

    private boolean availableDeveloperRoom = false;
    private boolean online = false;
    private final PlayerNpcFieldGateway playerNpcFieldGateway;

    public Server(PlayerNpcFieldGateway playerNpcFieldGateway) {
        this.playerNpcFieldGateway = playerNpcFieldGateway;
    }

    public boolean isOnline() {
        return online;
    }

    public void setNewYearCard(NewYearCardRecord nyc) {
        newyears.put(nyc.getId(), nyc);
    }

    public NewYearCardRecord getNewYearCard(int cardid) {
        return newyears.get(cardid);
    }

    public void removeNewYearCard(int cardId) {
        newyears.remove(cardId);
    }

    public void setAvailableDeveloperRoom() {
        availableDeveloperRoom = true;
    }

    public boolean canEnterDeveloperRoom() {
        return availableDeveloperRoom;
    }

    private void loadPlayerNpcMapStepFromDb() {
        try {
            List<World> worldList = worldServer.getWorlds();
            playerNpcFieldGateway.forEach(playerNpcField -> {
                World w = worldList.get(playerNpcField.getWorld());
                if (w != null) {
                    w.setPlayerNpcMapData(playerNpcField.getMap(),
                        playerNpcField.getStep(),
                        playerNpcField.getPodium());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Integer> getCouponRates() {
        return couponRates;
    }

    private void cleanNxcodeCoupons(Connection con) throws SQLException {
        if (!YamlConfig.config.server.USE_CLEAR_OUTDATED_COUPONS) return;

        long timeClear = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000;

        PreparedStatement ps = con.prepareStatement("SELECT * FROM nxcode WHERE expiration <= ?");
        ps.setLong(1, timeClear);
        ResultSet rs = ps.executeQuery();

        if (!rs.isLast()) {
            PreparedStatement ps2 = con.prepareStatement("DELETE FROM nxcode_items WHERE codeid = ?");
            while (rs.next()) {
                ps2.setInt(1, rs.getInt("id"));
                ps2.addBatch();
            }
            ps2.executeBatch();
            ps2.close();

            ps2 = con.prepareStatement("DELETE FROM nxcode WHERE expiration <= ?");
            ps2.setLong(1, timeClear);
            ps2.executeUpdate();
            ps2.close();
        }

        rs.close();
        ps.close();
    }

    private void loadCouponRates(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT couponid, rate FROM nxcoupons");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int cid = rs.getInt("couponid");
            int rate = rs.getInt("rate");

            couponRates.put(cid, rate);
        }

        rs.close();
        ps.close();
    }

    public List<Integer> getActiveCoupons() {
        synchronized (activeCoupons) {
            return activeCoupons;
        }
    }

    public void commitActiveCoupons() {
        for (World world : worldServer.getWorlds()) {
            for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                if (!chr.isLoggedin()) continue;

                chr.updateCouponRates();
            }
        }
    }

    public void toggleCoupon(Integer couponId) {
        if (ItemConstants.isRateCoupon(couponId)) {
            synchronized (activeCoupons) {
                if (activeCoupons.contains(couponId)) {
                    activeCoupons.remove(couponId);
                } else {
                    activeCoupons.add(couponId);
                }

                commitActiveCoupons();
            }
        }
    }

    public void updateActiveCoupons() throws SQLException {
        synchronized (activeCoupons) {
            activeCoupons.clear();
            Calendar c = Calendar.getInstance();

            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            int hourDay = c.get(Calendar.HOUR_OF_DAY);

            Connection con = null;
            try {
                con = createConnection();

                int weekdayMask = (1 << weekDay);
                PreparedStatement ps = con.prepareStatement("SELECT couponid FROM nxcoupons WHERE (activeday & ?) = ? AND starthour <= ? AND " +
                    "endhour > ?");
                ps.setInt(1, weekdayMask);
                ps.setInt(2, weekdayMask);
                ps.setInt(3, hourDay);
                ps.setInt(4, hourDay);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    activeCoupons.add(rs.getInt("couponid"));
                }

                rs.close();
                ps.close();

                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();

                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }

    public void runAnnouncePlayerDiseasesSchedule() {
        List<MapleClient> processDiseaseAnnounceClients;
        disLock.lock();
        try {
            processDiseaseAnnounceClients = new LinkedList<>(processDiseaseAnnouncePlayers);
            processDiseaseAnnouncePlayers.clear();
        } finally {
            disLock.unlock();
        }

        while (!processDiseaseAnnounceClients.isEmpty()) {
            MapleClient c = processDiseaseAnnounceClients.remove(0);
            MapleCharacter player = c.getPlayer();
            if (player != null && player.isLoggedinWorld()) {
                player.announceDiseases();
                player.collectDiseases();
            }
        }

        disLock.lock();
        try {
            // this is to force the system to wait for at least one complete tick before releasing disease info for the registered clients
            while (!registeredDiseaseAnnouncePlayers.isEmpty()) {
                MapleClient c = registeredDiseaseAnnouncePlayers.remove(0);
                processDiseaseAnnouncePlayers.add(c);
            }
        } finally {
            disLock.unlock();
        }
    }

    public void registerAnnouncePlayerDiseases(MapleClient c) {
        disLock.lock();
        try {
            registeredDiseaseAnnouncePlayers.add(c);
        } finally {
            disLock.unlock();
        }
    }

    public void init() {
        logger.info("HeavenMS v" + ServerConstants.VERSION + " starting up.");

        if (YamlConfig.config.server.SHUTDOWNHOOK)
            Runtime.getRuntime().addShutdownHook(new Thread(shutdown(false)));

        TimeZone.setDefault(TimeZone.getTimeZone(YamlConfig.config.server.TIMEZONE));

        Connection c = null;
        try {
            c = createConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
            ps = c.prepareStatement("UPDATE characters SET HasMerchant = 0");
            ps.executeUpdate();
            ps.close();

            cleanNxcodeCoupons(c);
            loadCouponRates(c);
            updateActiveCoupons();

            c.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        applyAllNameChanges(); //name changes can be missed by INSTANT_NAME_CHANGE
        applyAllWorldTransfers();
        //MaplePet.clearMissingPetsFromDb();    // thanks Optimist for noticing this taking too long to run
        MapleCashidGenerator.loadExistentCashIdsFromDb();

        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", (IoFilter) new ProtocolCodecFilter(new MapleCodecFactory()));

        ThreadManager.getInstance().start();
        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        tMan.register(tMan.purge(), YamlConfig.config.server.PURGING_INTERVAL);//Purging ftw...
        disconnectIdlesOnLoginTask();

        long timeLeft = ServerTimer.getTimeLeftForNextHour();
        tMan.register(new CharacterDiseaseTask(), YamlConfig.config.server.UPDATE_INTERVAL, YamlConfig.config.server.UPDATE_INTERVAL);
        tMan.register(new ReleaseLockTask(), 2 * 60 * 1000, 2 * 60 * 1000);
        tMan.register(new CouponTask(), YamlConfig.config.server.COUPON_INTERVAL, timeLeft);
        tMan.register(new RankingCommandTask(), 5 * 60 * 1000, 5 * 60 * 1000);
        tMan.register(new RankingLoginTask(), YamlConfig.config.server.RANKING_INTERVAL, timeLeft);
        tMan.register(new LoginCoordinatorTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new EventRecallCoordinatorTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new LoginStorageTask(), 2 * 60 * 1000, 2 * 60 * 1000);
        tMan.register(new DueyFredrickTask(), 60 * 60 * 1000, timeLeft);
        tMan.register(new InvitationTask(), 30 * 1000, 30 * 1000);
        tMan.register(new RespawnTask(), YamlConfig.config.server.RESPAWN_INTERVAL, YamlConfig.config.server.RESPAWN_INTERVAL);

        timeLeft = ServerTimer.getTimeLeftForNextDay();
        MapleExpeditionBossLog.resetBossLogTable();
        tMan.register(new BossLogTask(), 24 * 60 * 60 * 1000, timeLeft);

        long timeToTake = System.currentTimeMillis();
        SkillFactory.loadAllSkills();

        logger.info("Skills loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        timeToTake = System.currentTimeMillis();

        CashItemFactory.getSpecialCashItems();

        logger.info("Items loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        timeToTake = System.currentTimeMillis();
        MapleQuest.loadAllQuest();

        logger.info("Quest loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds\r\n");

        NewYearCardRecord.startPendingNewYearCardRequests();

        if (YamlConfig.config.server.USE_THREAD_TRACKER) ThreadTracker.getInstance().registerThreadTrackerTask();

        try {
            Integer worldCount = Math.min(GameConstants.WORLD_NAMES.length, YamlConfig.config.server.WORLDS);

            for (int i = 0; i < worldCount; i++) {
                worldServer.initWorld();
            }
            worldServer.initWorldPlayerRanking();

            MaplePlayerNPCFactory.loadFactoryMetadata();
            loadPlayerNpcMapStepFromDb();
        } catch (Exception e) {
            logger.error("Syntax error in 'world.ini'", e);
            System.exit(0);
        }

        if (YamlConfig.config.server.USE_FAMILY_SYSTEM) {
            timeToTake = System.currentTimeMillis();
            MapleFamily.loadAllFamilies();
            logger.info("Families loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds\r\n");
        }

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        acceptor.setHandler(new MapleServerHandler());
        try {
            acceptor.bind(new InetSocketAddress(8484));
        } catch (IOException ex) {
            logger.error("Cannot bind address.", ex);
            System.exit(-1);
        }

        logger.info("Listening on port 8484");
        logger.info("HeavenMS is now online.");
        online = true;

        MapleSkillbookInformationProvider.getInstance();
        OpcodeConstants.generateOpcodeNames();
        CommandsExecutor.getInstance();

        for (Channel ch : worldServer.getAllChannels()) {
            ch.reloadEventScriptManager();
        }
    }

    public static void main(String args[]) {
        System.setProperty("wzpath", "wz");
        Security.setProperty("crypto.policy", "unlimited");
        AutoJCE.removeCryptographyRestrictions();
        DatabaseMigration.migrate();
        Server.getInstance().init();
    }

    public Properties getSubnetInfo() {
        return subnetInfo;
    }

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

    public void allianceMessage(int id, final byte[] packet, int exception, int guildex) {
        MapleAlliance alliance = alliances.get(id);
        if (alliance != null) {
            for (Integer gid : alliance.getGuilds()) {
                if (guildex == gid) {
                    continue;
                }
                MapleGuild guild = guilds.get(gid);
                if (guild != null) {
                    guild.broadcast(packet, exception);
                }
            }
        }
    }

    public boolean addGuildtoAlliance(int aId, int guildId) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.addGuild(guildId);
            guilds.get(guildId).setAllianceId(aId);
            return true;
        }
        return false;
    }

    public boolean removeGuildFromAlliance(int aId, int guildId) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.removeGuild(guildId);
            guilds.get(guildId).setAllianceId(0);
            return true;
        }
        return false;
    }

    public boolean setAllianceRanks(int aId, String[] ranks) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.setRankTitle(ranks);
            return true;
        }
        return false;
    }

    public boolean setAllianceNotice(int aId, String notice) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.setNotice(notice);
            return true;
        }
        return false;
    }

    public boolean increaseAllianceCapacity(int aId, int inc) {
        MapleAlliance alliance = alliances.get(aId);
        if (alliance != null) {
            alliance.increaseCapacity(inc);
            return true;
        }
        return false;
    }

    public int createGuild(int leaderId, String name) {
        return MapleGuild.createGuild(leaderId, name);
    }

    public MapleGuild getGuildByName(String name) {
        synchronized (guilds) {
            for (MapleGuild mg : guilds.values()) {
                if (mg.getName().equalsIgnoreCase(name)) {
                    return mg;
                }
            }

            return null;
        }
    }

    public MapleGuild getGuild(int id) {
        synchronized (guilds) {
            if (guilds.get(id) != null) {
                return guilds.get(id);
            }

            return null;
        }
    }

    public MapleGuild getGuild(int id, int world) {
        return getGuild(id, world, null);
    }

    public MapleGuild getGuild(int id, int world, MapleCharacter mc) {
        synchronized (guilds) {
            MapleGuild g = guilds.get(id);
            if (g != null) {
                return g;
            }

            g = new MapleGuild(id, world);
            if (g.getId() == -1) {
                return null;
            }

            if (mc != null) {
                MapleGuildCharacter mgc = g.getMGC(mc.getId());
                if (mgc != null) {
                    mc.setMGC(mgc);
                    mgc.setCharacter(mc);
                } else {
                    FilePrinter.printError(FilePrinter.GUILD_CHAR_ERROR, "Could not find " + mc.getName() + " when loading guild " + id + ".");
                }

                g.setOnline(mc.getId(), true, mc.getClient().getChannel());
            }

            guilds.put(id, g);
            return g;
        }
    }

    public void setGuildMemberOnline(MapleCharacter mc, boolean bOnline, int channel) {
        MapleGuild g = getGuild(mc.getGuildId(), mc.getWorld(), mc);
        g.setOnline(mc.getId(), bOnline, channel);
    }

    public int addGuildMember(MapleGuildCharacter mgc, MapleCharacter chr) {
        MapleGuild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            return g.addGuildMember(mgc, chr);
        }
        return 0;
    }

    public boolean setGuildAllianceId(int gId, int aId) {
        MapleGuild guild = guilds.get(gId);
        if (guild != null) {
            guild.setAllianceId(aId);
            return true;
        }
        return false;
    }

    public void resetAllianceGuildPlayersRank(int gId) {
        guilds.get(gId).resetAllianceGuildPlayersRank();
    }

    public void leaveGuild(MapleGuildCharacter mgc) {
        MapleGuild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            g.leaveGuild(mgc);
        }
    }

    public void guildChat(int gid, String name, int cid, String msg) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.guildChat(name, cid, msg);
        }
    }

    public void changeRank(int gid, int cid, int newRank) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.changeRank(cid, newRank);
        }
    }

    public void expelMember(MapleGuildCharacter initiator, String name, int cid) {
        MapleGuild g = guilds.get(initiator.getGuildId());
        if (g != null) {
            g.expelMember(initiator, name, cid);
        }
    }

    public void setGuildNotice(int gid, String notice) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.setGuildNotice(notice);
        }
    }

    public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
        MapleGuild g = guilds.get(mgc.getGuildId());
        if (g != null) {
            g.memberLevelJobUpdate(mgc);
        }
    }

    public void changeRankTitle(int gid, String[] ranks) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.changeRankTitle(ranks);
        }
    }

    public void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.setGuildEmblem(bg, bgcolor, logo, logocolor);
        }
    }

    public void disbandGuild(int gid) {
        synchronized (guilds) {
            MapleGuild g = guilds.get(gid);
            g.disbandGuild();
            guilds.remove(gid);
        }
    }

    public boolean increaseGuildCapacity(int gid) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            return g.increaseCapacity();
        }
        return false;
    }

    public void gainGP(int gid, int amount) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.gainGP(amount);
        }
    }

    public void guildMessage(int gid, byte[] packet) {
        guildMessage(gid, packet, -1);
    }

    public void guildMessage(int gid, byte[] packet, int exception) {
        MapleGuild g = guilds.get(gid);
        if (g != null) {
            g.broadcast(packet, exception);
        }
    }

    public PlayerBuffStorage getPlayerBuffStorage() {
        return buffStorage;
    }

    public void deleteGuildCharacter(MapleCharacter mc) {
        setGuildMemberOnline(mc, false, (byte) -1);
        if (mc.getMGC().getGuildRank() > 1) {
            leaveGuild(mc.getMGC());
        } else {
            disbandGuild(mc.getMGC().getGuildId());
        }
    }

    public void deleteGuildCharacter(MapleGuildCharacter mgc) {
        if (mgc.getCharacter() != null) setGuildMemberOnline(mgc.getCharacter(), false, (byte) -1);
        if (mgc.getGuildRank() > 1) {
            leaveGuild(mgc);
        } else {
            disbandGuild(mgc.getGuildId());
        }
    }

    public void reloadGuildCharacters(int world) {
        World worlda = worldServer.getWorld(world);
        for (MapleCharacter mc : worlda.getPlayerStorage().getAllCharacters()) {
            if (mc.getGuildId() > 0) {
                setGuildMemberOnline(mc, true, worlda.getId());
                memberLevelJobUpdate(mc.getMGC());
            }
        }
        worlda.reloadGuildSummary();
    }

    public void changeFly(Integer accountid, boolean canFly) {
        if (canFly) {
            activeFly.add(accountid);
        } else {
            activeFly.remove(accountid);
        }
    }

    public boolean canFly(Integer accountid) {
        return activeFly.contains(accountid);
    }

    public int getCharacterWorld(Integer characterId) {
        return loginServer.getCharacterWorld(characterId);
    }

    public boolean haveCharacterEntry(Integer accountId, Integer characterId) {
        return loginServer.haveCharacterEntry(accountId, characterId);
    }

    public short getAccountCharacterCount(Integer accountId) {
        return loginServer.getAccountCharacterCount(accountId);
    }

    public short getAccountWorldCharacterCount(Integer accountId, Integer worldId) {
        return loginServer.getAccountWorldCharacterCount(accountId, worldId);
    }

    public void updateCharacterEntry(MapleCharacter chr) {
        MapleCharacter chrView = chr.generateCharacterEntry();
        World world = worldServer.getWorld(chrView.getWorld());
        if (world == null) return;
        updateCharacterEntry(chrView, world);
    }

    private void updateCharacterEntry(MapleCharacter chrView, World world) {
        loginServer.lgnWLock.lock();
        try {
            final int characterId = chrView.getId();
            if (!loginServer.worldChars.containsKey(characterId)) {
                logger.warn("Attempt to update character entry but the character is out of sync with LoginServer. Character Id: " + characterId);
            }

            final int accountID = chrView.getAccountID();
            final Set<Integer> characters = loginServer.accountChars.get(accountID);
            if (characters == null) {
                logger.warn("Attempt to update character entry but the account is out of sync with LoginServer. Account Id: " + accountID);
            }

            if (characters != null && !characters.contains(characterId)) {
                logger.warn("Attempt to update character entry but the account character is out of sync with LoginServer. Character Id: " + characterId);
            }

            world.registerAccountCharacterView(accountID, chrView);
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public void createCharacterEntry(MapleCharacter chr) {
        Integer accountId = chr.getAccountID();
        Integer characterId = chr.getId();
        int worldId = chr.getWorld();
        World world = worldServer.getWorld(worldId);

        loginServer.lgnWLock.lock();
        try {
            loginServer.accountCharacterCount.put(accountId, (short) (loginServer.accountCharacterCount.get(accountId) + 1));

            Set<Integer> accChars = loginServer.accountChars.get(accountId);
            accChars.add(characterId);

            loginServer.worldChars.put(characterId, worldId);

            MapleCharacter chrView = chr.generateCharacterEntry();
            if (world != null) world.registerAccountCharacterView(chrView.getAccountID(), chrView);
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public void deleteCharacterEntry(Integer accountid, Integer chrid) {
        loginServer.lgnWLock.lock();
        try {
            loginServer.accountCharacterCount.put(accountid, (short) (loginServer.accountCharacterCount.get(accountid) - 1));

            Set<Integer> accChars = loginServer.accountChars.get(accountid);
            accChars.remove(chrid);

            Integer world = loginServer.worldChars.remove(chrid);
            if (world != null) {
                World wserv = worldServer.getWorld(world);
                if (wserv != null) wserv.unregisterAccountCharacterView(accountid, chrid);
            }
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public void transferWorldCharacterEntry(MapleCharacter chr, Integer toWorld) { // used before setting the new worldid on the character object
        loginServer.lgnWLock.lock();
        try {
            Integer chrid = chr.getId(), accountid = chr.getAccountID(), world = loginServer.worldChars.get(chr.getId());
            if (world != null) {
                World wserv = worldServer.getWorld(world);
                if (wserv != null) wserv.unregisterAccountCharacterView(accountid, chrid);
            }

            loginServer.worldChars.put(chrid, toWorld);

            MapleCharacter chrView = chr.generateCharacterEntry();

            World wserv = worldServer.getWorld(toWorld);
            if (wserv != null) wserv.registerAccountCharacterView(chrView.getAccountID(), chrView);
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public Pair<Pair<Integer, List<MapleCharacter>>, List<Pair<Integer, List<MapleCharacter>>>> loadAccountCharlist(
        Integer accountId, int visibleWorlds) {
        List<World> wlist = worldServer.getWorlds();
        if (wlist.size() > visibleWorlds) wlist = wlist.subList(0, visibleWorlds);
        return loginServer.loadAccountCharacterList(accountId, wlist);
    }

    private Pair<Short, List<List<MapleCharacter>>> loadAccountCharactersViewFromDb(int accId, int wlen) {
        short characterCount = 0;
        List<List<MapleCharacter>> wchars = new ArrayList<>(wlen);
        for (int i = 0; i < wlen; i++) wchars.add(i, new LinkedList<MapleCharacter>());

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

            Connection con = createConnection();
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
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return new Pair<>(characterCount, wchars);
    }

    public void loadAllAccountsCharactersView() {
        try {
            Connection con = createConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM accounts");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int accountId = rs.getInt("id");
                if (loginServer.isFirstAccountLogin(accountId)) {
                    loadAccountCharactersView(accountId, 0, 0);
                }
            }

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void applyAllNameChanges() {
        try (Connection con = createConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM namechanges WHERE completionTime IS NULL")) {
            ResultSet rs = ps.executeQuery();
            List<Pair<String, String>> changedNames = new LinkedList<Pair<String, String>>(); //logging only
            while (rs.next()) {
                con.setAutoCommit(false);
                int nameChangeId = rs.getInt("id");
                int characterId = rs.getInt("characterId");
                String oldName = rs.getString("old");
                String newName = rs.getString("new");
                boolean success = MapleCharacter.doNameChange(con, characterId, oldName, newName, nameChangeId);
                if (!success) con.rollback(); //discard changes
                else changedNames.add(new Pair<String, String>(oldName, newName));
                con.setAutoCommit(true);
            }
            //log
            for (Pair<String, String> namePair : changedNames) {
                FilePrinter.print(FilePrinter.CHANGE_CHARACTER_NAME,
                    "Name change applied : from \"" + namePair.getLeft() + "\" to \"" + namePair.getRight() + "\" at " + Calendar.getInstance().getTime().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.CHANGE_CHARACTER_NAME, e, "Failed to retrieve list of pending name changes.");
        }
    }

    private void applyAllWorldTransfers() {
        try (Connection con = createConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM worldtransfers WHERE completionTime IS NULL")) {
            ResultSet rs = ps.executeQuery();
            List<Integer> removedTransfers = new LinkedList<Integer>();
            while (rs.next()) {
                int nameChangeId = rs.getInt("id");
                int characterId = rs.getInt("characterId");
                int oldWorld = rs.getInt("from");
                int newWorld = rs.getInt("to");
                String reason = MapleCharacter.checkWorldTransferEligibility(con, characterId, oldWorld, newWorld); //check if character is still
                // eligible
                if (reason != null) {
                    removedTransfers.add(nameChangeId);
                    FilePrinter.print(FilePrinter.WORLD_TRANSFER,
                        "World transfer cancelled : Character ID " + characterId + " at " + Calendar.getInstance().getTime().toString() + ", Reason" +
                            " : " + reason);
                    try (PreparedStatement delPs = con.prepareStatement("DELETE FROM worldtransfers WHERE id = ?")) {
                        delPs.setInt(1, nameChangeId);
                        delPs.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        FilePrinter.printError(FilePrinter.WORLD_TRANSFER, e, "Failed to delete world transfer for character ID " + characterId);
                    }
                }
            }
            rs.beforeFirst();
            List<Pair<Integer, Pair<Integer, Integer>>> worldTransfers = new LinkedList<Pair<Integer, Pair<Integer, Integer>>>(); //logging only
            // <charid, <oldWorld, newWorld>>
            while (rs.next()) {
                con.setAutoCommit(false);
                int nameChangeId = rs.getInt("id");
                if (removedTransfers.contains(nameChangeId)) continue;
                int characterId = rs.getInt("characterId");
                int oldWorld = rs.getInt("from");
                int newWorld = rs.getInt("to");
                boolean success = MapleCharacter.doWorldTransfer(con, characterId, oldWorld, newWorld, nameChangeId);
                if (!success) con.rollback();
                else worldTransfers.add(new Pair<Integer, Pair<Integer, Integer>>(characterId, new Pair<Integer, Integer>(oldWorld, newWorld)));
                con.setAutoCommit(true);
            }
            //log
            for (Pair<Integer, Pair<Integer, Integer>> worldTransferPair : worldTransfers) {
                int charId = worldTransferPair.getLeft();
                int oldWorld = worldTransferPair.getRight().getLeft();
                int newWorld = worldTransferPair.getRight().getRight();
                FilePrinter.print(FilePrinter.WORLD_TRANSFER, "World transfer applied : Character ID " + charId + " from World " + oldWorld + " to " +
                    "World " + newWorld + " at " + Calendar.getInstance().getTime().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FilePrinter.printError(FilePrinter.WORLD_TRANSFER, e, "Failed to retrieve list of pending world transfers.");
        }
    }

    public void loadAccountCharacters(MapleClient c) {
        Integer accId = c.getAccID();
        if (!loginServer.isFirstAccountLogin(accId)) {
            Set<Integer> accWorlds = loginServer.getWorldsForAccount(accId);

            int gmLevel = 0;
            for (Integer aw : accWorlds) {
                World wserv = worldServer.getWorld(aw);

                if (wserv != null) {
                    for (MapleCharacter chr : wserv.getAllCharactersView()) {
                        if (gmLevel < chr.gmLevel()) gmLevel = chr.gmLevel();
                    }
                }
            }

            c.setGMLevel(gmLevel);
            return;
        }

        int gmLevel = loadAccountCharactersView(c.getAccID(), 0, 0);
        c.setGMLevel(gmLevel);
    }

    private int loadAccountCharactersView(Integer accId, int gmLevel, int fromWorldid) {    // returns the maximum gmLevel found
        List<World> wlist = worldServer.getWorlds();
        Pair<Short, List<List<MapleCharacter>>> accCharacters = loadAccountCharactersViewFromDb(accId, wlist.size());

        loginServer.lgnWLock.lock();
        try {
            List<List<MapleCharacter>> accChars = accCharacters.getRight();
            loginServer.accountCharacterCount.put(accId, accCharacters.getLeft());

            Set<Integer> chars = loginServer.accountChars.get(accId);
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
                    loginServer.worldChars.put(cid, wid);
                }
            }

            loginServer.accountChars.put(accId, chars);
        } finally {
            loginServer.lgnWLock.unlock();
        }

        return gmLevel;
    }

    public void loadAccountStorages(MapleClient c) {
        int accountId = c.getAccID();
        Set<Integer> accWorlds = new HashSet<>();
        loginServer.lgnWLock.lock();
        try {
            Set<Integer> chars = loginServer.accountChars.get(accountId);

            for (Integer cid : chars) {
                Integer worldid = loginServer.worldChars.get(cid);
                if (worldid != null) {
                    accWorlds.add(worldid);
                }
            }
        } finally {
            loginServer.lgnWLock.unlock();
        }

        List<World> worldList = worldServer.getWorlds();
        for (Integer worldid : accWorlds) {
            if (worldid < worldList.size()) {
                World wserv = worldList.get(worldid);
                wserv.registerAccountStorage(accountId);
            }
        }
    }

    private String getRemoteIp(IoSession session) {
        return MapleSessionCoordinator.getSessionRemoteAddress(session);
    }

    public void setCharacteridInTransition(IoSession session, int charId) {
        String remoteIp = getRemoteIp(session);

        loginServer.lgnWLock.lock();
        try {
            loginServer.transitioningChars.put(remoteIp, charId);
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public boolean validateCharacteridInTransition(IoSession session, int charId) {
        if (!YamlConfig.config.server.USE_IP_VALIDATION) {
            return true;
        }

        String remoteIp = getRemoteIp(session);

        loginServer.lgnWLock.lock();
        try {
            Integer cid = loginServer.transitioningChars.remove(remoteIp);
            return cid != null && cid.equals(charId);
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public Integer freeCharacteridInTransition(IoSession session) {
        if (!YamlConfig.config.server.USE_IP_VALIDATION) {
            return null;
        }

        String remoteIp = getRemoteIp(session);

        loginServer.lgnWLock.lock();
        try {
            return loginServer.transitioningChars.remove(remoteIp);
        } finally {
            loginServer.lgnWLock.unlock();
        }
    }

    public boolean hasCharacterInTransition(IoSession session) {
        return loginServer.hasCharacterInTransition(session);
    }

    public void registerLoginState(MapleClient c) {
        srvLock.lock();
        try {
            inLoginState.put(c, System.currentTimeMillis() + 600000);
        } finally {
            srvLock.unlock();
        }
    }

    public void unregisterLoginState(MapleClient c) {
        srvLock.lock();
        try {
            inLoginState.remove(c);
        } finally {
            srvLock.unlock();
        }
    }

    private void disconnectIdlesOnLoginState() {
        List<MapleClient> toDisconnect = new LinkedList<>();

        srvLock.lock();
        try {
            long timeNow = System.currentTimeMillis();

            for (Entry<MapleClient, Long> mc : inLoginState.entrySet()) {
                if (timeNow > mc.getValue()) {
                    toDisconnect.add(mc.getKey());
                }
            }

            for (MapleClient c : toDisconnect) {
                inLoginState.remove(c);
            }
        } finally {
            srvLock.unlock();
        }

        for (MapleClient c : toDisconnect) {    // thanks Lei for pointing a deadlock issue with srvLock
            if (c.isLoggedIn()) {
                c.disconnect(false, false);
            } else {
                MapleSessionCoordinator.getInstance().closeSession(c.getSession(), true);
            }
        }
    }

    private void disconnectIdlesOnLoginTask() {
        TimerManager.getInstance().register(new Runnable() {
            @Override
            public void run() {
                disconnectIdlesOnLoginState();
            }
        }, 300000);
    }

    public final Runnable shutdown(final boolean restart) {//no player should be online when trying to shutdown!
        return new Runnable() {
            @Override
            public void run() {
                shutdownInternal(restart);
            }
        };
    }

    private synchronized void shutdownInternal(boolean restart) {
        logger.info((restart ? "Restarting" : "Shutting down") + " the server!\r\n");
        if (worldServer.getWorlds() == null) return;//already shutdown
        for (World w : worldServer.getWorlds()) {
            w.shutdown();
        }

        List<Channel> allChannels = worldServer.getAllChannels();

        if (YamlConfig.config.server.USE_THREAD_TRACKER) ThreadTracker.getInstance().cancelThreadTrackerTask();

        for (Channel ch : allChannels) {
            while (!ch.finishedShutdown()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    System.err.println("FUCK MY LIFE");
                }
            }
        }

        ThreadManager.getInstance().stop();
        TimerManager.getInstance().purge();
        TimerManager.getInstance().stop();

        worldServer.resetServerWorlds();

        logger.info("Worlds + Channels are offline.");
        acceptor.unbind();
        acceptor = null;
        if (!restart) {  // shutdown hook deadlocks if System.exit() method is used within its body chores, thanks MIKE for pointing that out
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }).start();
        } else {
            logger.info("Restarting the server....");
            try {
                instance.finalize();//FUU I CAN AND IT'S FREE
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            instance = null;
            System.gc();
            getInstance().init();//DID I DO EVERYTHING?! D:
        }
    }

    private Connection createConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}
