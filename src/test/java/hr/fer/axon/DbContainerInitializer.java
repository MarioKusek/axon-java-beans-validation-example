package hr.fer.axon;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.PostgreSQLContainer;

public class DbContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private PostgreSQLContainer<?> postgreSQLContainer;

  private void startContainer() {
    postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.2")
        .withDatabaseName("integration-tests-db")
        .withUsername("sa")
        .withPassword("sa");
    postgreSQLContainer.start();
  }

  public DbContainerInitializer() {
    startContainer();
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    TestPropertyValues.of(
        "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
        "spring.datasource.username=" + postgreSQLContainer.getUsername(),
        "spring.datasource.password=" + postgreSQLContainer.getPassword()
      ).applyTo(applicationContext.getEnvironment());

    applicationContext.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
      @Override
      public void onApplicationEvent(ContextClosedEvent event) {
        DataSource dataSource = event.getApplicationContext().getBean(DataSource.class);
        if(dataSource instanceof Closeable c) {
          try {
            c.close();
          } catch (IOException ignored) {
          }
        }
        postgreSQLContainer.stop();
      }
    });
  }

}
