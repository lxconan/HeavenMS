package abstraction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CharacterGateway {
    private static final CharacterGateway instance = new CharacterGateway();
    public static CharacterGateway getInstance() {return instance;}

    public void clearMerchantState(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE characters SET HasMerchant = 0")) {
            ps.executeUpdate();
        }
    }
}
