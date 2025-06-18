package hr.fer.axon;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@Tag("integration")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
  "logging.level.org.springframework.security=DEBUG"
})
@ActiveProfiles({"test"})
public abstract class TestContainersFixture {
  private static final Logger log = LoggerFactory.getLogger(TestContainersFixture.class);

  @ServiceConnection
  private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.2")
    .withDatabaseName("integration-tests-db")
    .withUsername("sa")
    .withPassword("sa");

  static {
    postgreSQLContainer.start();
  }

  @Autowired
  private DataSource dataSource;

  @BeforeEach
  protected void clearAllTables() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();
      ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, new String[] {"TABLE"});
      List<String> tablesToClear = new LinkedList<>();
      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        if (!tableName.startsWith("databasechangelog")) {
          tablesToClear.add(tableName);
        }
      }

      clearTables(tablesToClear);

      recreateTestData();
    }
  }

  protected void clearTables(List<String> tablesToClear) throws SQLException {
    try (
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
    ) {
      for (var tableName : tablesToClear) {
        statement.addBatch("ALTER TABLE " + tableName + " DISABLE TRIGGER ALL");
      }
      for (var tableName : tablesToClear) {
        statement.addBatch("DELETE FROM " + tableName);
      }
      for (var tableName : tablesToClear) {
        statement.addBatch("ALTER TABLE " + tableName + " ENABLE TRIGGER ALL");
      }
      statement.executeBatch();
    }
  }

  // This is mend to be overridden in subclass if the have to recreate data
  protected void recreateTestData() throws Exception {
  }

  // injecting port of rest server
  @LocalServerPort
  protected int port;

  // url of rest server
  private String baseRestServerURL;

  // set url for rest server
  @PostConstruct
  public void init() {
    baseRestServerURL = "http://localhost:" + port;
  }

  protected String getBaseRestServerURL() {
    return baseRestServerURL;
  }

}
