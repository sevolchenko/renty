package vsu.csf.rentyserver;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

@Testcontainers
public abstract class IntegrationEnvironment {

    public static final JdbcDatabaseContainer<?> DB_CONTAINER;

    static {
        DB_CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("renty")
                .withUsername("user")
                .withPassword("password");
        DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DB_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DB_CONTAINER::getPassword);

        Startables.deepStart(DB_CONTAINER);
                //.thenAccept(unused -> runMigrations(DB_CONTAINER));
    }
//
//
//    private static void runMigrations(JdbcDatabaseContainer<?> c) {
//        var changelogPath = new File(".").toPath().toAbsolutePath().getParent().getParent()
//                .resolve("migrations");
//
//        try (var conn = DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword())) {
//            var changelogDir = new DirectoryResourceAccessor(changelogPath);
//
//            var db = new PostgresDatabase();
//            db.setConnection(new JdbcConnection(conn));
//
//            var liquibase = new Liquibase("master.xml", changelogDir, db);
//            liquibase.update(new Contexts(), new LabelExpression());
//        } catch (FileNotFoundException | LiquibaseException | SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
