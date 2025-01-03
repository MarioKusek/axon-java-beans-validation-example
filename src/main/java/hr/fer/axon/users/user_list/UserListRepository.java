package hr.fer.axon.users.user_list;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserListRepository extends JpaRepository<UserListEntity, String>{
  Optional<UserListEntity> findByLogin(String login);
  Optional<UserListEntity> findByJmbag(String jmbag);
  Optional<UserListEntity> findByOauth2sub(String oauth2sub);
  boolean existsByOauth2sub(String oauth2sub);
  boolean existsByLogin(String login);
  boolean existsByJmbag(String jmbag);

  @Query("SELECT u FROM UserListEntity u WHERE LOWER(u.firstName) LIKE :search or LOWER(u.lastName) LIKE :search or LOWER(u.jmbag) LIKE :search or CAST(FUNCTION('to_char', u.durationFactor,'999D9') as text) LIKE :search or :plainSearch MEMBER OF u.roleNames")
  Page<UserListEntity> findBySearchText(Pageable pageable, @Param("search") String searchText, @Param("plainSearch") String plainSearch);

  @Query("SELECT u FROM UserListEntity u WHERE NOT (LOWER(u.firstName) LIKE :search or LOWER(u.lastName) LIKE :search or LOWER(u.roleNames) LIKE :search or LOWER(u.jmbag) LIKE :search or CAST(FUNCTION('to_char', u.durationFactor,'999D9') as text) LIKE :search or :plainSearch MEMBER OF u.roleNames)")
  Page<UserListEntity> findByNotSearchText(Pageable pageable, @Param("search") String searchText, @Param("plainSearch") String plainSearch);

}
