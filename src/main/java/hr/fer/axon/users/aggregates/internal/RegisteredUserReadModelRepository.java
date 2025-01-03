package hr.fer.axon.users.aggregates.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredUserReadModelRepository extends JpaRepository<RegisteredUser, String> {
  Optional<RegisteredUser> findByLogin(String login);
  Optional<RegisteredUser> findByJmbag(String jmbag);
  Optional<RegisteredUser> findByOauth2sub(String oauth2sub);

  boolean existsByLogin(String login);
  boolean existsByJmbag(String jmbag);
  boolean existsByOauth2sub(String oauth2sub);
}
