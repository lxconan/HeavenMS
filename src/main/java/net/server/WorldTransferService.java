package net.server;

import client.MapleCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DatabaseConnection;
import tools.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class WorldTransferService {
    private static Logger logger = LoggerFactory.getLogger(WorldTransferService.class);
    private static final WorldTransferService instance = new WorldTransferService();
    public static WorldTransferService getInstance() {return instance;}

    private WorldTransferService() { }

    private Connection createConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void applyAllWorldTransfers() {
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
                    final Date time = Calendar.getInstance().getTime();
                    logger.info("World transfer cancelled : Character ID " + characterId + " at " + time.toString() + ", Reason" + " : " + reason);
                    try (PreparedStatement delPs = con.prepareStatement("DELETE FROM worldtransfers WHERE id = ?")) {
                        delPs.setInt(1, nameChangeId);
                        delPs.executeUpdate();
                    } catch (SQLException e) {
                        logger.error("Failed to delete world transfer for character ID " + characterId, e);
                        throw new RuntimeException(e);
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
                else worldTransfers.add(new Pair<>(characterId, new Pair<>(oldWorld, newWorld)));
                con.setAutoCommit(true);
            }
            //log
            for (Pair<Integer, Pair<Integer, Integer>> worldTransferPair : worldTransfers) {
                int charId = worldTransferPair.getLeft();
                int oldWorld = worldTransferPair.getRight().getLeft();
                int newWorld = worldTransferPair.getRight().getRight();
                final Date time = Calendar.getInstance().getTime();
                logger.info("World transfer applied : Character ID " + charId + " from World " + oldWorld + " to " +
                    "World " + newWorld + " at " + time.toString());
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve list of pending world transfers.", e);
            throw new RuntimeException(e);
        }
    }
}
