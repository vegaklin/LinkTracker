package backend.academy.scrapper;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

// isolated from the "bot" module's containers!
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    //    @Bean
    //    @RestartScope
    //    @ServiceConnection(name = "redis")
    //    GenericContainer<?> redisContainer() {
    //        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
    //    }

    @Bean
    @RestartScope
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17-alpine")
                .withExposedPorts(5432)
                .withDatabaseName("local")
                .withUsername("postgres")
                .withPassword("test");

        container.start();
        migration(container);
        return container;
    }

    //    @Bean
    //    @RestartScope
    //    @ServiceConnection
    //    KafkaContainer kafkaContainer() {
    //        return new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);
    //    }

    @SneakyThrows
    void migration(final PostgreSQLContainer<?> container) {
        Connection conn =
                DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
        Liquibase liquibase =
                new Liquibase("master.xml", new DirectoryResourceAccessor(Path.of("..", "migrations")), database);

        liquibase.update(new Contexts());
    }
}
