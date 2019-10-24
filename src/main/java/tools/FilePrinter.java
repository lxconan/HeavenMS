package tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilePrinter {
    private static final Logger logger = LoggerFactory.getLogger(FilePrinter.class);
    public static final String
            AUTOBAN_WARNING = "game/AutoBanWarning.txt",    // log naming version by Vcoc
            AUTOBAN_DC = "game/AutoBanDC.txt",
            ACCOUNT_STUCK = "players/AccountStuck.txt",
            COMMAND_GM = "reports/Gm.txt",
            COMMAND_BUG = "reports/Bug.txt",
            LOG_TRADE = "interactions/Trades.txt",
            LOG_EXPEDITION = "interactions/Expeditions.txt",
            LOG_LEAF = "interactions/MapleLeaves.txt",
            LOG_GACHAPON = "interactions/Gachapon.txt",
            LOG_CHAT = "interactions/ChatLog.txt",
            QUEST_RESTORE_ITEM = "game/QuestItemRestore.txt",
            EXCEPTION_CAUGHT = "game/ExceptionCaught.txt",
            CLIENT_START = "game/ClientStartError.txt",
            MAPLE_MAP = "game/MapleMap.txt",
            ERROR38 = "game/Error38.txt",
            PACKET_LOG = "game/Log.txt",
            CASHITEM_BOUGHT = "interactions/CashLog.txt",
            EXCEPTION = "game/Exceptions.txt",
            LOGIN_EXCEPTION = "game/LoginExceptions.txt",
            TRADE_EXCEPTION = "game/TradeExceptions.txt",
            SQL_EXCEPTION = "game/SqlExceptions.txt",
            PACKET_HANDLER = "game/packethandler/",
            PORTAL = "game/portals/",
            PORTAL_STUCK = "game/portalblocks/",
            NPC = "game/npcs/",
            INVOCABLE = "game/invocable/",
            REACTOR = "game/reactors/",
            QUEST = "game/quests/",
            ITEM = "game/items/",
            MOB_MOVEMENT = "game/MobMovement.txt",
            MAP_SCRIPT = "game/mapscript/",
            DIRECTION = "game/directions/",
            GUILD_CHAR_ERROR = "guilds/GuildCharError.txt",
            SAVE_CHAR = "players/SaveToDB.txt",
            INSERT_CHAR = "players/InsertCharacter.txt",
            LOAD_CHAR = "players/LoadCharFromDB.txt",
            CREATED_CHAR = "players/createdchars/",
            DELETED_CHAR = "players/deletedchars/",
            UNHANDLED_EVENT = "game/DoesNotExist.txt",
            SESSION = "players/Sessions.txt",
            DCS = "game/disconnections/",
            EXPLOITS = "game/exploits/",
            STORAGE = "game/storage/",
            PACKET_LOGS = "game/packetlogs/",
            PACKET_STREAM = "game/packetstream/",
            FREDRICK = "game/npcs/fredrick/",
            NPC_UNCODED = "game/npcs/UncodedNPCs.txt",
            QUEST_UNCODED = "game/quests/UncodedQuests.txt",
            AUTOSAVING_CHARACTER = "players/SaveCharAuto.txt",
            SAVING_CHARACTER = "players/SaveChar.txt",
            CHANGE_CHARACTER_NAME = "players/NameChange.txt",
            WORLD_TRANSFER = "players/WorldTransfer.txt",
            FAMILY_ERROR = "players/FamilyErrors.txt",
            USED_COMMANDS = "commands/UsedCommands.txt",
            DEADLOCK_ERROR = "deadlocks/Deadlocks.txt",
            DEADLOCK_STACK = "deadlocks/Path.txt",
            DEADLOCK_LOCKS = "deadlocks/Locks.txt",
            DEADLOCK_STATE = "deadlocks/State.txt",
            DISPOSED_LOCKS = "deadlocks/Disposed.txt";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //for file system purposes, it's nice to use yyyy-MM-dd
    private static final String FILE_PATH = "logs/" + sdf.format(Calendar.getInstance().getTime()) + "/"; // + sdf.format(Calendar.getInstance().getTime()) + "/"
    private static final String ERROR = "error/";

    public static void printError(final String name, final Throwable t) {
        final String target = FILE_PATH + ERROR + name;
        logger.error("Error thrown: " + target, t);
    }

    public static void printError(final String name, final Throwable t, final String info) {
        final String target = FILE_PATH + ERROR + name;
        logger.error("Error thrown: " + target, t);
        logger.error("Additional info: " + info);
    }

    public static void printError(final String name, final String s) {
        final String target = FILE_PATH + ERROR + name;
        logger.error("Error thrown: " + target);
        logger.error("Additional info: " + s);
    }

    public static void print(final String name, final String s) {
        print(name, s, true);
    }

    public static void print(final String name, final String s, boolean line) {
        logger.info(name + ": " + s);
    }
}
