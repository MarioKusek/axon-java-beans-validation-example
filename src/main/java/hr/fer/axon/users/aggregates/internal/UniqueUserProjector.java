package hr.fer.axon.users.aggregates.internal;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import hr.fer.axon.users.events.UserCreated;
import hr.fer.axon.users.events.UserDeleted;
import hr.fer.axon.users.events.UserOauth2subUpdated;

@ProcessingGroup("validation")
@Component
class UniqueUserProjector {
  private RegisteredUserReadModelRepository repository;

  public UniqueUserProjector(RegisteredUserReadModelRepository repository) {
    this.repository = repository;
  }

  @EventHandler
  void on(UserCreated event) {
    var entity = this.repository.findByLogin(event.login())
      .orElse(new RegisteredUser(
          event.login(),
          event.jmbag(),
          null));
    repository.saveAndFlush(entity);
  }

  @EventHandler
  void on(UserOauth2subUpdated event) {
    var entity = this.repository.findByLogin(event.login())
        .orElseThrow();
    entity.setOauth2sub(event.oauth2sub());
    repository.saveAndFlush(entity);
  }

  @EventHandler
  void on(UserDeleted event) {
    if(this.repository.existsById(event.login())) {
      repository.deleteById(event.login());
    }
  }
}
