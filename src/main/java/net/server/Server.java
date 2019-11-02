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

import client.MapleCharacter;
import client.MapleFamily;
import client.SkillFactory;
import client.command.CommandsExecutor;
import client.inventory.manipulator.MapleCashidGenerator;
import client.newyear.NewYearCardRecord;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.net.OpcodeConstants;
import constants.net.ServerConstants;
import net.MapleServerHandler;
import net.mina.MapleCodecFactory;
import net.server.audit.ThreadTracker;
import net.server.channel.Channel;
import net.server.task.*;
import net.server.world.World;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private static Server instance = new Server();
    public static Server getInstance() { return instance; }

    private IoAcceptor acceptor;
    private final CouponService couponService = CouponService.getInstance();
    private final WorldServer worldServer = WorldServer.getInstance();


    private final LoginStateService loginStateService = LoginStateService.getInstance();

    private final PlayerBuffStorage buffStorage = new PlayerBuffStorage();

    private boolean online = false;

    public boolean isOnline() {
        return online;
    }

    public void init() {
        logger.info("HeavenMS v" + ServerConstants.VERSION + " starting up.");

        if (YamlConfig.config.server.SHUTDOWNHOOK)
            Runtime.getRuntime().addShutdownHook(new Thread(shutdown(false)));

        TimeZone.setDefault(TimeZone.getTimeZone(YamlConfig.config.server.TIMEZONE));

        Connection c = null;
        try {
            c = createConnection();
            loginStateService.clearAccountLoginState(c);

            PreparedStatement ps = c.prepareStatement("UPDATE characters SET HasMerchant = 0");
            ps.executeUpdate();
            ps.close();

            couponService.cleanNxcodeCoupons(c);
            couponService.loadCouponRates(c);
            couponService.updateActiveCoupons();

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
        loginStateService.disconnectIdlesOnLoginTask();

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
            worldServer.loadPlayerNpcMapStepFromDb();
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

    public PlayerBuffStorage getPlayerBuffStorage() {
        return buffStorage;
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

    public final Runnable shutdown(final boolean restart) {//no player should be online when trying to shutdown!
        return () -> shutdownInternal(restart);
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
