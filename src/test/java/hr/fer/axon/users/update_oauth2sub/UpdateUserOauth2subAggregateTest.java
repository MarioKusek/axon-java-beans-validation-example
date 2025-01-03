package hr.fer.axon.users.update_oauth2sub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.modelling.command.Repository;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hr.fer.axon.TestContainersFixture;
import hr.fer.axon.users.UserDto;
import hr.fer.axon.users.aggregates.UserAggregate;
import hr.fer.axon.users.commands.UpdateUserOauth2sub;
import hr.fer.axon.users.events.UserCreatedFaker;
import hr.fer.axon.users.events.UserOauth2subUpdated;
import hr.fer.axon.users.queries.FindUserByLogin;
import hr.fer.axon.utils.ProjectionFixtureConfiguration;

class UpdateUserOauth2subAggregateTest extends TestContainersFixture {

  @Autowired
  private Repository<UserAggregate> repository;

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private QueryGateway queryGateway;

  @Autowired
  private EventStore eventStore;

  @Test
  void updateOauth2sub() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(UserCreatedFaker.builder()
        .login("l")
        .build());
    fixture.apply();

    // WHEN
    final Object sendAndWait = commandGateway.sendAndWait(new UpdateUserOauth2sub("l", "some oauth2sub value"));

    // THEN
    final List<Object> events = eventStore.readEvents("l")
        .asStream()
        .map(e -> (Object) e.getPayload())
        .toList();

    assertThat(events).hasSize(2);

    assertThat(events.get(1)).isEqualTo(new UserOauth2subUpdated("l", "some oauth2sub value"));

  }

  @Test
  void updateWhenThereIsNoUserCreatedBefore() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    // WHEN
    assertThatThrownBy(() ->
      commandGateway.sendAndWait(new UpdateUserOauth2sub("l", "some oauth2sub value")))
    //THEN
    .isInstanceOf(AggregateNotFoundException.class);
  }

  @Test
  void oauth2subShouldNotBeNull() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(UserCreatedFaker.builder()
        .login("l")
        .build());
    fixture.apply();

    // WHEN
    assertThatThrownBy(() ->
      commandGateway.sendAndWait(new UpdateUserOauth2sub("l", null)))
    // THEN
    .isInstanceOf(JSR303ViolationException.class)
    .hasMessageFindingMatch("property oauth2sub in class (\\w|\\.)*\\.UpdateUserOauth2sub must not be null");
  }

  @Test
  void oauth2subShouldBeUnique() throws Exception {
    // GIVEN
    var fixture = ProjectionFixtureConfiguration.aggregateInstance(() -> repository.newInstance(() -> new UserAggregate()));

    fixture.given(
        UserCreatedFaker.builder().login("pperic").build(),
        new UserOauth2subUpdated("pperic", "sub1"));
    fixture.apply();
    fixture.reset();
    fixture.given(
        UserCreatedFaker.builder().login("aanic").build());
    fixture.apply();
    Awaitility.await().atMost(60, TimeUnit.SECONDS).pollInterval(500, TimeUnit.MILLISECONDS)
      .until(() -> {
        final UserDto user = queryGateway.query(new FindUserByLogin("pperic"), UserDto.class).join();
        return user != null && user.oauth2sub() != null;
      });
    Awaitility.await().atMost(60, TimeUnit.SECONDS).pollInterval(500, TimeUnit.MILLISECONDS)
      .until(() -> {
        final UserDto user = queryGateway.query(new FindUserByLogin("aanic"), UserDto.class).join();
        return user != null;
      });

    // WHEN
    assertThatThrownBy(() ->
      commandGateway.sendAndWait(new UpdateUserOauth2sub("aanic", "sub1")))
    // THEN
    .isInstanceOf(JSR303ViolationException.class)
    .hasMessageFindingMatch("property oauth2sub in class (\\w|\\.)*\\.UpdateUserOauth2sub user oauth2sub 'sub1' already exists!");

  }
}
