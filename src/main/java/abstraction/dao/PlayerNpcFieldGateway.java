package abstraction.dao;

import abstraction.ApplicationContext;
import abstraction.DataConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerNpcFieldGateway {
    private final DataConnectionFactory dataConnectionFactory;

    public PlayerNpcFieldGateway(ApplicationContext applicationContext) {
        this.dataConnectionFactory = applicationContext.getBean(DataConnectionFactory.class);
    }

    public List<PlayerNpcField> findAll() throws SQLException {
        try (
            Connection con = dataConnectionFactory.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_field");
            ResultSet rs = ps.executeQuery();
        ) {
            List<PlayerNpcField> result = new ArrayList<>();
            while (rs.next()) {
                int world = rs.getInt("world");
                int map = rs.getInt("map");
                int step = rs.getInt("step");
                int podium = rs.getInt("podium");
                result.add(new PlayerNpcField(world, map, step, podium));
            }
            return result;
        }
    }
}

