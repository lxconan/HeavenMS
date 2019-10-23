package abstraction;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataConnectionFactory {
    Connection getConnection() throws SQLException;
}

