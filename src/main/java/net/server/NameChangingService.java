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
import java.util.LinkedList;
import java.util.List;

public class NameChangingService {
    private static final NameChangingService instance = new NameChangingService();
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    public static NameChangingService getInstance() {return instance;}

    private NameChangingService() {}

    private Connection createConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void applyAllNameChanges() {
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
                else changedNames.add(new Pair<>(oldName, newName));
                con.setAutoCommit(true);
            }
            //log
            for (Pair<String, String> namePair : changedNames) {
                logger.info("Name change applied : from \"" + namePair.getLeft() + "\" to \"" + namePair.getRight() + "\" at " + Calendar.getInstance().getTime().toString());
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve list of pending name changes.");
            throw new RuntimeException(e);
        }
    }
}
