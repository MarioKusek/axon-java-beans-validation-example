package hr.fer.axon.users.aggregates;

import java.util.List;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import hr.fer.axon.users.commands.CreateUser;
import hr.fer.axon.users.commands.DeleteUser;
import hr.fer.axon.users.commands.UpdateUserOauth2sub;
import hr.fer.axon.users.events.UserCreated;
import hr.fer.axon.users.events.UserDeleted;
import hr.fer.axon.users.events.UserOauth2subUpdated;
import hr.fer.axon.utils.ViolationException;

@Aggregate
public class UserAggregate {

  @AggregateIdentifier
  private String login;
  private String jmbag;
  private String oauth2sub;
  private String firstName;
  private String lastName;
  private String email;
  private double durationFactor;
  private List<String> roles;
  private boolean deleted;

  public UserAggregate() {
    deleted = true;
  }

  @CommandHandler
  @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
  public void handle(CreateUser createUserCmd) {
    if(deleted) {
      AggregateLifecycle.apply(new UserCreated(
          createUserCmd.login(),
          createUserCmd.jmbag(),
          createUserCmd.firstName(),
          createUserCmd.lastName(),
          createUserCmd.email(),
          createUserCmd.durationFactor(),
          createUserCmd.roles()));
    } else {
      //throw new ValidationException("User with login '" + login + "' already exists!");
    }
  }

  @CommandHandler
  public void handle(UpdateUserOauth2sub updateOauth2cubCmd) {
    if(!deleted) {
      AggregateLifecycle.apply(new UserOauth2subUpdated(
          updateOauth2cubCmd.login(),
          updateOauth2cubCmd.oauth2sub()));
    }
  }

  @CommandHandler
  public void handle(DeleteUser cmd) {
    if(deleted)
      throw new ViolationException("User with login '" + cmd.login() + "' already deleted!");
    AggregateLifecycle.apply(new UserDeleted(cmd.login()));
  }

  @EventSourcingHandler
  public void on(UserCreated event) {
    login = event.login();
    jmbag = event.jmbag();
    firstName = event.firstName();
    lastName = event.lastName();
    email = event.email();
    durationFactor = event.durationFactor();
    roles = event.roles();
    deleted = false;
  }

  @EventSourcingHandler
  public void on(UserOauth2subUpdated event) {
    oauth2sub = event.oauth2sub();
  }

  @EventSourcingHandler
  public void on(UserDeleted event) {
    deleted = true;
    jmbag = null;
    oauth2sub = null;
    firstName = null;
    lastName = null;
    email = null;
    durationFactor = 1.0;
    roles = List.of();
  }
}
