package hr.fer.axon.users.user_list;

import java.util.TreeSet;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import hr.fer.axon.users.events.UserCreated;
import hr.fer.axon.users.events.UserDeleted;
import hr.fer.axon.users.events.UserOauth2subUpdated;

// TODO Axon after migration can be removed
@ProcessingGroup("validation")
@Component
public class UserListProjector {
  private UserListRepository repository;

  public UserListProjector(UserListRepository repository) {
    this.repository = repository;
  }

  @EventHandler
  void on(UserCreated event) {
    repository.saveAndFlush(new UserListEntity(
        event.login(),
        event.jmbag(),
        null,
        event.firstName(),
        event.lastName(),
        event.email(),
        event.durationFactor(),
        new TreeSet<>(event.roles())
        ));
  }

  @EventHandler
  void on(UserOauth2subUpdated event) {
    repository.findById(event.login())
      .ifPresent(u -> {
        u.setOauth2sub(event.oauth2sub());
        repository.saveAndFlush(u);
      });
  }

  @EventHandler
  void on(UserDeleted event) {
    repository.deleteById(event.login());
  }
}
