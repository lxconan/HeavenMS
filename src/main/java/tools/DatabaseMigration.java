package tools;

import config.YamlConfig;
import org.flywaydb.core.Flyway;

public class DatabaseMigration {
    public static void migrate() {
        if (!YamlConfig.config.server.DB_ENABLE_MIGRATION) { return; }

        final Flyway migration = Flyway.configure()
            .dataSource(
                YamlConfig.config.server.DB_MIGRATION_URL,
                YamlConfig.config.server.DB_MIGRATION_USER,
                YamlConfig.config.server.DB_MIGRATION_PASS)
            .initSql("SET SQL_MODE = \"NO_AUTO_VALUE_ON_ZERO\";\nSET time_zone = \"+00:00\";")
            .locations("classpath:sql/migration")
            .schemas(YamlConfig.config.server.DB_MIGRATION_SCHEMA)
            .load();

        migration.migrate();
    }
}
