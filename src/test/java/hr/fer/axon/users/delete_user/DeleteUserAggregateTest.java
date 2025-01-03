package hr.fer.axon.users.delete_user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.modelling.command.Repository;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import hr.fer.axon.TestContainersFixture;
import hr.fer.axon.users.aggregates.UserAggregate;
import hr.fer.axon.users.commands.CreateUserFaker;
import hr.fer.axon.users.commands.DeleteUser;
import hr.fer.axon.users.events.UserCreated;
import hr.fer.axon.users.events.UserCreatedFaker;
import hr.fer.axon.users.events.UserDeleted;
import hr.fer.axon.utils.ProjectionFixtureConfiguration;
import hr.fer.axon.utils.ViolationException;

@DirtiesContext
class DeleteUserAggregateTest extends TestContainersFixture {

  @Autowired
  private Repository<UserAggregate> repository;

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private QueryGateway queryGateway;

  @Autowired
  private EventStore eventStore;

  @Test
  void deleteUser() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(UserCreatedFaker.builder()
        .login("l")
        .build());
    fixture.apply();

    // WHEN
    final Object sendAndWait = commandGateway.sendAndWait(new DeleteUser("l"));

    // THEN
    final List<Object> events = eventStore.readEvents("l")
        .asStream()
        .map(e -> (Object) e.getPayload())
        .toList();

    assertThat(events).hasSize(2);
    assertThat(events.get(1)).isEqualTo(new UserDeleted("l"));
  }

  @Test
  void deleteUserThatWasNotCreated() throws Exception {
    // GIVEN

    // WHEN
    assertThatThrownBy(() -> commandGateway.sendAndWait(new DeleteUser("l")))

    // THEN
    .isInstanceOf(AggregateNotFoundException.class)
    .hasMessageContaining("The aggregate was not found in the event store");

    final List<Object> events = eventStore.readEvents("l")
        .asStream()
        .map(e -> (Object) e.getPayload())
        .toList();
    assertThat(events).hasSize(0);
  }

  @Test
  void deletedUser() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(
      UserCreatedFaker.builder().login("l").build(),
      new UserDeleted("l")
    );
    fixture.apply();

    // WHEN
    assertThatThrownBy(() -> commandGateway.sendAndWait(new DeleteUser("l")))

    // THEN
    .isInstanceOf(ViolationException.class)
    .hasMessageContaining("User with login 'l' already deleted");

    final List<Object> events = eventStore.readEvents("l")
        .asStream()
        .map(e -> (Object) e.getPayload())
        .toList();
    assertThat(events).hasSize(2);
  }

  @Test
  void createDeletedUser() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(
        UserCreatedFaker.builder().login("l").build(),
        new UserDeleted("l")
        );
    fixture.apply();

    // WHEN
    commandGateway.sendAndWait(CreateUserFaker.builder().login("l").build());

    // THEN
    final List<Object> events = eventStore.readEvents("l")
        .asStream()
        .map(e -> (Object) e.getPayload())
        .toList();
    assertThat(events).hasSize(3);
    assertThat(events.get(2)).isInstanceOf(UserCreated.class);
  }
}
