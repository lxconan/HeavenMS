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

import abstraction.dao.CharacterGateway;
import client.MapleFamily;
import client.SkillFactory;
import client.command.CommandsExecutor;
import client.inventory.manipulator.MapleCashidGenerator;
import client.newyear.NewYearCardRecord;
import config.YamlConfig;
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
import server.quest.MapleQuest;
import tools.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.TimeZone;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private static Server instance = new Server();

    public static Server getInstance() {
        return instance;
    }

    private IoAcceptor acceptor;
    private final CouponService couponService = CouponService.getInstance();
    private final WorldServer worldServer = WorldServer.getInstance();
    private final LoginStateService loginStateService = LoginStateService.getInstance();
    private final PlayerBuffStorage buffStorage = new PlayerBuffStorage();
    private final CharacterGateway characterGateway = CharacterGateway.getInstance();
    private final NameChangingService nameChangingService = NameChangingService.getInstance();
    private final WorldTransferService worldTransferService = WorldTransferService.getInstance();

    private boolean online = false;

    public boolean isOnline() {
        return online;
    }

    public void init() {
        logger.info("HeavenMS v" + ServerConstants.VERSION + " starting up.");
        initializeShutdownHook();
        initializeTimezone();

        try (Connection c = createConnection()) {
            loginStateService.clearAccountLoginState(c);
            characterGateway.clearMerchantState(c);
            couponService.cleanNxcodeCoupons(c);
            couponService.loadCouponRates(c);
            couponService.updateActiveCoupons();
        } catch (SQLException sqle) {
            final String message = "Fail to loading player and coupon state.";
            logger.error(message, sqle);
            throw new RuntimeException(message, sqle);
        }

        //name changes can be missed by INSTANT_NAME_CHANGE
        nameChangingService.applyAllNameChanges();
        worldTransferService.applyAllWorldTransfers();
        //MaplePet.clearMissingPetsFromDb();    // thanks Optimist for noticing this taking too long to run
        MapleCashidGenerator.loadExistentCashIdsFromDb();

        ThreadManager.getInstance().start();
        initializeTimelyTasks();
        SkillFactory.loadAllSkills();
        CashItemFactory.getSpecialCashItems();
        MapleQuest.loadAllQuest();
        NewYearCardRecord.startPendingNewYearCardRequests();
        if (YamlConfig.config.server.USE_THREAD_TRACKER) ThreadTracker.getInstance().registerThreadTrackerTask();
        initializeWorldAndChannels();
        loadFamilySystem();
        OpcodeConstants.generateOpcodeNames();
        createIoPipeline();
        logger.info("HeavenMS is now online.");
        online = true;
        MapleSkillbookInformationProvider.getInstance();
        CommandsExecutor.getInstance();
    }

    private void initializeWorldAndChannels() {
        try {
            worldServer.initialize();
        } catch (Exception e) {
            logger.error("Initialize worlds failed.", e);
            System.exit(0);
        }
    }

    private void loadFamilySystem() {
        long timeToTake;
        if (YamlConfig.config.server.USE_FAMILY_SYSTEM) {
            timeToTake = System.currentTimeMillis();
            MapleFamily.loadAllFamilies();
            logger.info("Families loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds\r\n");
        }
    }

    private void createIoPipeline() {
        IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", (IoFilter) new ProtocolCodecFilter(new MapleCodecFactory()));
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        acceptor.setHandler(new MapleServerHandler());
        try {
            acceptor.bind(new InetSocketAddress(8484));
        } catch (IOException ex) {
            logger.error("Cannot bind address.", ex);
            System.exit(-1);
        }

        logger.info("Listening on port 8484");
    }

    private void initializeTimelyTasks() {
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
    }

    private void initializeTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(YamlConfig.config.server.TIMEZONE));
    }

    private void initializeShutdownHook() {
        if (YamlConfig.config.server.SHUTDOWNHOOK)
            Runtime.getRuntime().addShutdownHook(new Thread(shutdown()));
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

    public final Runnable shutdown() {//no player should be online when trying to shutdown!
        return this::shutdownInternal;
    }

    private synchronized void shutdownInternal() {
        logger.info("Shutting down the server!");
        if (worldServer.getWorlds() == null) return;//already shutdown
        for (World w : worldServer.getWorlds()) {
            w.shutdown();
        }

        List<Channel> allChannels = worldServer.getAllChannels();
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

        worldServer.resetServerWorlds();
        logger.info("Worlds + Channels are offline.");

        if (YamlConfig.config.server.USE_THREAD_TRACKER) ThreadTracker.getInstance().cancelThreadTrackerTask();

        ThreadManager.getInstance().stop();
        TimerManager.getInstance().purge();
        TimerManager.getInstance().stop();

        acceptor.unbind();
        acceptor = null;
        // shutdown hook deadlocks if System.exit() method is used within its body chores, thanks MIKE for pointing that out
        new Thread(() -> System.exit(0)).start();
    }

    private Connection createConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}
