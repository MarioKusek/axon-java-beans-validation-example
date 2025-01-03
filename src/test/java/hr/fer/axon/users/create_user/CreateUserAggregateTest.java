package hr.fer.axon.users.create_user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.modelling.command.Repository;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hr.fer.axon.TestContainersFixture;
import hr.fer.axon.users.aggregates.UserAggregate;
import hr.fer.axon.users.commands.CreateUser;
import hr.fer.axon.users.commands.CreateUserFaker;
import hr.fer.axon.users.events.UserCreated;
import hr.fer.axon.users.events.UserCreatedFaker;
import hr.fer.axon.utils.ProjectionFixtureConfiguration;

class CreateUserAggregateTest extends TestContainersFixture {

  @Autowired
  private Repository<UserAggregate> repository;

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private EventStore eventStore;

  @Test
  void createUser() throws Exception {
    FixtureConfiguration<UserAggregate> fixture = new AggregateTestFixture<>(UserAggregate.class);

    fixture
      .given(List.of())
      .when(new CreateUser(
          "l",
          "j",
          "fn",
          "ln",
          "em",
          1.1,
          List.of("r1", "r2")))
      .expectEvents(new UserCreated(
          "l",
          "j",
          "fn",
          "ln",
          "em",
          1.1,
          List.of("r1", "r2")));
  }

    @Test
    void jmbagInCreateUser_canNotBeNull() throws Exception {
      // GIVEN

      // WHEN
      assertThatThrownBy(() ->
        commandGateway.sendAndWait(new CreateUser(
          "l",
          null,
          "fn",
          "ln",
          "em",
          1.1,
          List.of("r1", "r2"))
          )
      )
      // THEN
      .isInstanceOf(JSR303ViolationException.class)
      .hasMessageContaining("property jmbag")
      .hasMessageContaining("must not be null");

      final List<Object> events = eventStore.readEvents("l")
          .asStream()
          .map(e -> (Object) e.getPayload())
          .toList();

      assertThat(events).hasSize(0);
    }

  @Test
  void userAlereadyCreated() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(UserCreatedFaker.builder()
        .login("l")
        .build());
    fixture.apply();

    // WHEN
    assertThatThrownBy(() ->
      commandGateway.sendAndWait(CreateUserFaker.builder()
          .login("l")
          .build())
    )
    // THEN
    .isInstanceOf(JSR303ViolationException.class)
    .hasMessageFindingMatch("property login in class (\\w|\\.)*\\.CreateUser user login 'l' already exists!");
  }


  @Test
  void differentUsersCanNotHaveTheSameJMBAG() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(UserCreatedFaker.builder()
        .jmbag("jmbag1")
        .build());
    fixture.apply();

    // WHEN
    assertThatThrownBy(() ->
      commandGateway.sendAndWait(CreateUserFaker.builder()
          .jmbag("jmbag1")
          .build())
    )
    // THEN
    .isInstanceOf(JSR303ViolationException.class)
    .hasMessageFindingMatch("property jmbag in class (\\w|\\.)*\\.CreateUser user jmbag 'jmbag1' already exists!");
  }

}
