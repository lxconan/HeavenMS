package abstraction.dao;

import tools.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class PlayerNpcFieldGateway {
    private final static PlayerNpcFieldGateway instance = new PlayerNpcFieldGateway();
    public static PlayerNpcFieldGateway getInstance() {
        return instance;
    }

    public void forEach(Consumer<PlayerNpcField> consumer) throws SQLException {
        try (
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_field");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                int world = rs.getInt("world");
                int map = rs.getInt("map");
                int step = rs.getInt("step");
                int podium = rs.getInt("podium");
                consumer.accept(new PlayerNpcField(world, map, step, podium));
            }
        }
    }
}

