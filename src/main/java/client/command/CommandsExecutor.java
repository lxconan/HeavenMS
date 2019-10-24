/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2018 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command;

import client.command.commands.gm0.*;
import client.command.commands.gm1.*;
import client.command.commands.gm2.*;
import client.command.commands.gm3.*;
import client.command.commands.gm4.*;
import client.command.commands.gm5.*;
import client.command.commands.gm6.*;

import client.MapleClient;

import config.YamlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.FilePrinter;
import tools.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class CommandsExecutor {
    private static CommandsExecutor instance = new CommandsExecutor(YamlConfig.config.server.ENABLE_ALL_COMMANDS);
    private static final Logger logger = LoggerFactory.getLogger(CommandsExecutor.class);

    public static CommandsExecutor getInstance() {
        return instance;
    }

    private static final char USER_HEADING = '@';
    private static final char GM_HEADING = '!';


    public static boolean isCommand(MapleClient client, String content) {
        char heading = content.charAt(0);
        if (client.getPlayer().isGM()) {
            return heading == USER_HEADING || heading == GM_HEADING;
        }
        return heading == USER_HEADING;
    }
    private HashMap<String, Command> registeredCommands = new HashMap<>();

    private Pair<List<String>, List<String>> levelCommandsCursor;
    private List<Pair<List<String>, List<String>>> commandsNameDesc = new ArrayList<>();

    private CommandsExecutor(boolean enableAllCommands) {
        registerLv0Commands();
        registerLv1Commands(enableAllCommands);
        registerLv2Commands(enableAllCommands);
        registerLv3Commands(enableAllCommands);
        registerLv4Commands(enableAllCommands);
        registerLv5Commands(enableAllCommands);
        registerLv6Commands(enableAllCommands);
    }

    public List<Pair<List<String>, List<String>>> getGmCommands() {
        return commandsNameDesc;
    }

    public void handle(MapleClient client, String message) {
        if (client.tryacquireClient()) {
            try {
                handleInternal(client, message);
            } finally {
                client.releaseClient();
            }
        } else {
            client.getPlayer().dropMessage(5, "Try again in a while... Latest commands are currently being processed.");
        }
    }

    private void handleInternal(MapleClient client, String message) {
        if (client.getPlayer().getMapId() == 300000012) {
            client.getPlayer().yellowMessage("You do not have permission to use commands while in jail.");
            return;
        }
        final String splitRegex = "[ ]";
        String[] splitedMessage = message.substring(1).split(splitRegex, 2);
        if (splitedMessage.length < 2) {
            splitedMessage = new String[]{splitedMessage[0], ""};
        }

        client.getPlayer().setLastCommandMessage(splitedMessage[1]);    // thanks Tochi & Nulliphite for noticing string messages being marshalled lowercase
        final String commandName = splitedMessage[0].toLowerCase();
        final String[] lowercaseParams = splitedMessage[1].toLowerCase().split(splitRegex);

        final Command command = registeredCommands.get(commandName);
        if (command == null) {
            client.getPlayer().yellowMessage("Command '" + commandName + "' is not available. See @commands for a list of available commands.");
            return;
        }
        if (client.getPlayer().gmLevel() < command.getRank()) {
            client.getPlayer().yellowMessage("You do not have permission to use this command.");
            return;
        }
        String[] params;
        if (lowercaseParams.length > 0 && !lowercaseParams[0].isEmpty()) {
            params = Arrays.copyOfRange(lowercaseParams, 0, lowercaseParams.length);
        } else {
            params = new String[]{};
        }

        command.execute(client, params);
        writeLog(client, message);
    }

    private void writeLog(MapleClient client, String command) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        FilePrinter.print(FilePrinter.USED_COMMANDS, client.getPlayer().getName() + " used: " + command + " on "
                + sdf.format(Calendar.getInstance().getTime()));
    }

    private void addCommandInfo(String name, Class<? extends Command> commandClass) {
        try {
            levelCommandsCursor.getRight().add(commandClass.newInstance().getDescription());
            levelCommandsCursor.getLeft().add(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void addCommand(String[] commandNames, Class<? extends Command> commandClass) {
        for (String commandName : commandNames) {
            addCommand(commandName, 0, commandClass);
        }
    }

    private void addCommand(String commandName, Class<? extends Command> commandClass) {
        addCommand(commandName, 0, commandClass);
    }

    @SuppressWarnings("SameParameterValue")
    private void addCommand(String[] commandNames, int rank, Class<? extends Command> commandClass) {
        for (String commandName : commandNames) {
            addCommand(commandName, rank, commandClass);
        }
    }

    private void addCommand(String commandName, int rank, Class<? extends Command> commandClass) {
        commandName = commandName.toLowerCase();
        if (registeredCommands.containsKey(commandName)) {
            logger.warn("Error on register command with name: " + commandName + ". Already exists.");
            return;
        }

        addCommandInfo(commandName, commandClass);

        try {
            Command commandInstance = commandClass.newInstance();     // thanks Halcyon for noticing commands getting reinstanced every call
            commandInstance.setRank(rank);

            registeredCommands.put(commandName, commandInstance);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerLv0Commands() {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addCommand(new String[]{"help", "commands"}, HelpCommand.class);
        addCommand("droplimit", DropLimitCommand.class);
        addCommand("time", TimeCommand.class);
        addCommand("credits", StaffCommand.class);
        addCommand("buyback", BuyBackCommand.class);
        addCommand("uptime", UptimeCommand.class);
        addCommand("gacha", GachaCommand.class);
        addCommand("dispose", DisposeCommand.class);
        addCommand("changel", ChangeLanguageCommand.class);
        addCommand("equiplv", EquipLvCommand.class);
        addCommand("showrates", ShowRatesCommand.class);
        addCommand("rates", RatesCommand.class);
        addCommand("online", OnlineCommand.class);
        addCommand("gm", GmCommand.class);
        addCommand("reportbug", ReportBugCommand.class);
        addCommand("points", ReadPointsCommand.class);
        addCommand("joinevent", JoinEventCommand.class);
        addCommand("leaveevent", LeaveEventCommand.class);
        addCommand("ranks", RanksCommand.class);
        addCommand("str", StatStrCommand.class);
        addCommand("dex", StatDexCommand.class);
        addCommand("int", StatIntCommand.class);
        addCommand("luk", StatLukCommand.class);
        addCommand("enableauth", EnableAuthCommand.class);
        addCommand("toggleexp", ToggleExpCommand.class);
        addCommand("mylawn", MapOwnerClaimCommand.class);
        addCommand("bosshp", BossHpCommand.class);
        addCommand("mobhp", MobHpCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }


    private void registerLv1Commands(boolean enableAllCommands) {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        final int rank = enableAllCommands ? 0 : 1;

        addCommand("whatdropsfrom", rank, WhatDropsFromCommand.class);
        addCommand("whodrops", rank, WhoDropsCommand.class);
        addCommand("buffme", rank, BuffMeCommand.class);
        addCommand("goto", rank, GotoCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }


    private void registerLv2Commands(boolean enableAllCommands) {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        final int rank = enableAllCommands ? 0 : 2;
        addCommand("recharge", rank, RechargeCommand.class);
        addCommand("whereami", rank, WhereaMiCommand.class);
        addCommand("hide", rank, HideCommand.class);
        addCommand("unhide", rank, UnHideCommand.class);
        addCommand("sp", rank, SpCommand.class);
        addCommand("ap", rank, ApCommand.class);
        addCommand("empowerme", rank, EmpowerMeCommand.class);
        addCommand("buffmap", rank, BuffMapCommand.class);
        addCommand("buff", rank, BuffCommand.class);
        addCommand("bomb", rank, BombCommand.class);
        addCommand("dc", rank, DcCommand.class);
        addCommand("cleardrops", rank, ClearDropsCommand.class);
        addCommand("clearslot", rank, ClearSlotCommand.class);
        addCommand("clearsavelocs", rank, ClearSavedLocationsCommand.class);
        addCommand("warp", rank, WarpCommand.class);
        addCommand(new String[]{"warphere", "summon"}, rank, SummonCommand.class);
        addCommand(new String[]{"warpto", "reach", "follow"}, rank, ReachCommand.class);
        addCommand("gmshop", rank, GmShopCommand.class);
        addCommand("heal", rank, HealCommand.class);
        addCommand("item", rank, ItemCommand.class);
        addCommand("drop", rank, ItemDropCommand.class);
        addCommand("level", rank, LevelCommand.class);
        addCommand("levelpro", rank, LevelProCommand.class);
        addCommand("setslot", rank, SetSlotCommand.class);
        addCommand("setstat", rank, SetStatCommand.class);
        addCommand("maxstat", rank, MaxStatCommand.class);
        addCommand("maxskill", rank, MaxSkillCommand.class);
        addCommand("resetskill", rank, ResetSkillCommand.class);
        addCommand("search", rank, SearchCommand.class);
        addCommand("jail", rank, JailCommand.class);
        addCommand("unjail", rank, UnJailCommand.class);
        addCommand("job", rank, JobCommand.class);
        addCommand("unbug", rank, UnBugCommand.class);
        addCommand("id", rank, IdCommand.class);
        addCommand("gachalist", GachaListCommand.class);
        addCommand("loot", LootCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv3Commands(boolean enableAllCommands) {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        final int rank = enableAllCommands ? 0 : 3;
        addCommand("debuff", rank, DebuffCommand.class);
        addCommand("fly", rank, FlyCommand.class);
        addCommand("spawn", rank, SpawnCommand.class);
        addCommand("mutemap", rank, MuteMapCommand.class);
        addCommand("checkdmg", rank, CheckDmgCommand.class);
        addCommand("inmap", rank, InMapCommand.class);
        addCommand("reloadevents", rank, ReloadEventsCommand.class);
        addCommand("reloaddrops", rank, ReloadDropsCommand.class);
        addCommand("reloadportals", rank, ReloadPortalsCommand.class);
        addCommand("reloadmap", rank, ReloadMapCommand.class);
        addCommand("reloadshops", rank, ReloadShopsCommand.class);
        addCommand("hpmp", rank, HpMpCommand.class);
        addCommand("maxhpmp", rank, MaxHpMpCommand.class);
        addCommand("music", rank, MusicCommand.class);
        addCommand("monitor", rank, MonitorCommand.class);
        addCommand("monitors", rank, MonitorsCommand.class);
        addCommand("ignore", rank, IgnoreCommand.class);
        addCommand("ignored", rank, IgnoredCommand.class);
        addCommand("pos", rank, PosCommand.class);
        addCommand("togglecoupon", rank, ToggleCouponCommand.class);
        addCommand("togglewhitechat", rank, ChatCommand.class);
        addCommand("fame", rank, FameCommand.class);
        addCommand("givenx", rank, GiveNxCommand.class);
        addCommand("givevp", rank, GiveVpCommand.class);
        addCommand("givems", rank, GiveMesosCommand.class);
        addCommand("giverp", rank, GiveRpCommand.class);
        addCommand("expeds", rank, ExpedsCommand.class);
        addCommand("kill", rank, KillCommand.class);
        addCommand("seed", rank, SeedCommand.class);
        addCommand("maxenergy", rank, MaxEnergyCommand.class);
        addCommand("killall", rank, KillAllCommand.class);
        addCommand("notice", rank, NoticeCommand.class);
        addCommand("rip", rank, RipCommand.class);
        addCommand("openportal", rank, OpenPortalCommand.class);
        addCommand("closeportal", rank, ClosePortalCommand.class);
        addCommand("pe", rank, PeCommand.class);
        addCommand("startevent", rank, StartEventCommand.class);
        addCommand("endevent", rank, EndEventCommand.class);
        addCommand("startmapevent", rank, StartMapEventCommand.class);
        addCommand("stopmapevent", rank, StopMapEventCommand.class);
        addCommand("online2", rank, OnlineTwoCommand.class);
        addCommand("ban", rank, BanCommand.class);
        addCommand("unban", rank, UnBanCommand.class);
        addCommand("healmap", rank, HealMapCommand.class);
        addCommand("healperson", rank, HealPersonCommand.class);
        addCommand("hurt", rank, HurtCommand.class);
        addCommand("killmap", rank, KillMapCommand.class);
        addCommand("night", rank, NightCommand.class);
        addCommand("npc", rank, NpcCommand.class);
        addCommand("face", rank, FaceCommand.class);
        addCommand("hair", rank, HairCommand.class);
        addCommand("startquest", rank, QuestStartCommand.class);
        addCommand("completequest", rank, QuestCompleteCommand.class);
        addCommand("resetquest", rank, QuestResetCommand.class);
        addCommand("timer", rank, TimerCommand.class);
        addCommand("timermap", rank, TimerMapCommand.class);
        addCommand("timerall", rank, TimerAllCommand.class);
        addCommand("warpmap", rank, WarpMapCommand.class);
        addCommand("warparea", rank, WarpAreaCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv4Commands(boolean enableAllCommands) {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        final int rank = enableAllCommands ? 0 : 4;
        addCommand("servermessage", rank, ServerMessageCommand.class);
        addCommand("proitem", rank, ProItemCommand.class);
        addCommand("seteqstat", rank, SetEqStatCommand.class);
        addCommand("exprate", rank, ExpRateCommand.class);
        addCommand("mesorate", rank, MesoRateCommand.class);
        addCommand("droprate", rank, DropRateCommand.class);
        addCommand("bossdroprate", rank, BossDropRateCommand.class);
        addCommand("questrate", rank, QuestRateCommand.class);
        addCommand("travelrate", rank, TravelRateCommand.class);
        addCommand("fishrate", rank, FishingRateCommand.class);
        addCommand("itemvac", rank, ItemVacCommand.class);
        addCommand("forcevac", rank, ForceVacCommand.class);
        addCommand("zakum", rank, ZakumCommand.class);
        addCommand("horntail", rank, HorntailCommand.class);
        addCommand("pinkbean", rank, PinkbeanCommand.class);
        addCommand("pap", rank, PapCommand.class);
        addCommand("pianus", rank, PianusCommand.class);
        addCommand("cake", rank, CakeCommand.class);
        addCommand("playernpc", rank, PlayerNpcCommand.class);
        addCommand("playernpcremove", rank, PlayerNpcRemoveCommand.class);
        addCommand("pnpc", rank, PnpcCommand.class);
        addCommand("pnpcremove", rank, PnpcRemoveCommand.class);
        addCommand("pmob", rank, PmobCommand.class);
        addCommand("pmobremove", rank, PmobRemoveCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv5Commands(boolean enableAllCommands) {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        final int rank = enableAllCommands ? 0 : 5;
        addCommand("debug", rank, DebugCommand.class);
        addCommand("set", rank, SetCommand.class);
        addCommand("showpackets", rank, ShowPacketsCommand.class);
        addCommand("showmovelife", rank, ShowMoveLifeCommand.class);
        addCommand("showsessions", rank, ShowSessionsCommand.class);
        addCommand("iplist", rank, IpListCommand.class);

        commandsNameDesc.add(levelCommandsCursor);
    }

    private void registerLv6Commands(boolean enableAllCommands) {
        levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        final int rank = enableAllCommands ? 0 : 6;
        addCommand("setgmlevel", rank, SetGmLevelCommand.class);
        addCommand("warpworld", rank, WarpWorldCommand.class);
        addCommand("saveall", rank, SaveAllCommand.class);
        addCommand("dcall", rank, DCAllCommand.class);
        addCommand("mapplayers", rank, MapPlayersCommand.class);
        addCommand("getacc", rank, GetAccCommand.class);
        addCommand("shutdown", rank, ShutdownCommand.class);
        addCommand("clearquestcache", rank, ClearQuestCacheCommand.class);
        addCommand("clearquest", rank, ClearQuestCommand.class);
        addCommand("supplyratecoupon", rank, SupplyRateCouponCommand.class);
        addCommand("spawnallpnpcs", rank, SpawnAllPNpcsCommand.class);
        addCommand("eraseallpnpcs", rank, EraseAllPNpcsCommand.class);
        addCommand("addchannel", rank, ServerAddChannelCommand.class);
        addCommand("addworld", rank, ServerAddWorldCommand.class);
        addCommand("removechannel", rank, ServerRemoveChannelCommand.class);
        addCommand("removeworld", rank, ServerRemoveWorldCommand.class);
        commandsNameDesc.add(levelCommandsCursor);
    }
}
