package abstraction;

import tools.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DataConnectionFactoryImpl implements DataConnectionFactory {
    @Override
    public Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}
