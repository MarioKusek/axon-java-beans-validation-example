package hr.fer.axon.users.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import hr.fer.axon.TestContainersFixture;
import hr.fer.axon.users.user_list.UserListEntity;
import hr.fer.axon.users.user_list.UserListRepository;

@TestPropertySource(properties = {
    "admin.user=myadmin",
    "logging.level.org.springframework.transaction.interceptor=TRACE",
    "logging.level.org.springframework.transaction=TRACE",
    "logging.level.org.springframework.jdbc.support.JdbcTransactionManager=TRACE"
})
class AdminUserInitializerTest extends TestContainersFixture {
  @Autowired
  UserListRepository repo;

  @Autowired
  AdminUserInitializer initializer;

  @Override
  protected void clearAllTables() throws Exception {
  }

  @Test
  void adminUserConfigured() {

    // TODO Axon change to use Axon query for getting user
    Awaitility.await().atMost(60, TimeUnit.SECONDS)
      .pollInterval(500, TimeUnit.MILLISECONDS)
      .until(() -> {
        return repo.existsById("myadmin");
      });

    Optional<UserListEntity> optional = repo.findById("myadmin");

    assertThat(optional)
      .isNotEmpty();

    UserListEntity admin = optional.get();
    assertThat(admin.getRoleNames())
      .contains("admin");
  }

  @Test
  void adminUserWasAlreadyInDb_shouldAddAdminRole() {
    Optional<UserListEntity> optional = repo.findById("myadmin");
    UserListEntity admin = optional.get();

    // remove admin role
    admin.setRoleNames(Set.of("bla"));
    repo.saveAndFlush(admin);

    // init admin user
    initializer.initAdminUser();

    // check that init has added admin role
    UserListEntity loadedAdmin = repo.findById("myadmin").get();
    assertThat(loadedAdmin.getRoleNames()).containsExactlyInAnyOrder("admin","bla");
  }

  @Test
  void adminUserWasAlredyInDbAndHasAdnimRole_shouldNotAddAdminRoleAgain() {
    // TODO Axon add Axon command for adding user role admin
    Optional<UserListEntity> optional = repo.findById("myadmin");
    UserListEntity admin = optional.get();
    admin.setRoleNames(Set.of("admin", "bla"));
    repo.saveAndFlush(admin);

    initializer.initAdminUser();

    UserListEntity loadedAdmin = repo.findById("myadmin").get();
    assertThat(loadedAdmin.getRoleNames()).containsExactlyInAnyOrder("admin", "bla");
  }

}
