package hr.fer.axon.users.user_list;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import hr.fer.axon.DbContainerInitializer;
import hr.fer.axon.users.UserDto;
import hr.fer.axon.users.UsersPage;
import hr.fer.axon.users.events.UserCreated;
import hr.fer.axon.users.events.UserCreatedFaker;
import hr.fer.axon.users.events.UserDeleted;
import hr.fer.axon.users.events.UserOauth2subUpdated;
import hr.fer.axon.users.queries.FindAllUsers;
import hr.fer.axon.users.queries.FindUserByJmbag;
import hr.fer.axon.users.queries.FindUserByLogin;
import hr.fer.axon.users.queries.FindUserByOauth2sub;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {DbContainerInitializer.class})
@Import({UserListProjector.class, UserListQueryHandler.class})
class UserListQueryHandlerTest {

  @Autowired
  private UserListProjector projector;

  @Autowired
  private UserListQueryHandler handler;

  @Test
  void readModel() throws Exception {
    // GIVEN
    final UserCreated user1 = UserCreatedFaker.builder()
        .jmbag("jmbag1")
        .build();
    final UserCreated user2 = UserCreatedFaker.builder()
        .jmbag("jmbag2")
        .build();

    projector.on(user1);
    projector.on(user2);

    // THEN
    final UsersPage allUsers = handler.allUsers(FindAllUsers.max_10_000());

    assertThat(allUsers.getNumberOfElements()).isEqualTo(2);

    assertThat(allUsers.getContent().stream().map(u -> u.jmbag()).toList())
      .contains("jmbag1", "jmbag2");
  }

  @Test
  void findByLogin() throws Exception {
    // GIVEN
    final UserCreated user = UserCreatedFaker.builder()
        .login("l1")
        .roles(List.of("r1", "r2"))
        .durationFactor(1.5)
        .build();

    projector.on(user);

    // THEN
    final UserDto found = handler.findByLogin(new FindUserByLogin("l1"));

    assertThat(found.login()).isEqualTo("l1");
    assertThat(found.jmbag()).isEqualTo(user.jmbag());
    assertThat(found.oauth2sub()).isNull();
    assertThat(found.firstName()).isEqualTo(user.firstName());
    assertThat(found.lastName()).isEqualTo(user.lastName());
    assertThat(found.email()).isEqualTo(user.email());
    assertThat(found.durationFactor()).isEqualTo(1.5);
    assertThat(found.roleNames()).isEqualTo(List.of("r1", "r2"));
  }

  @Test
  void noLogin() throws Exception {
    assertThat(handler.findByLogin(new FindUserByLogin("noLogin"))).isNull();
  }

  @Test
  void findByJmbag() throws Exception {
    // GIVEN
    final UserCreated user = UserCreatedFaker.builder()
        .jmbag("jmbagX")
        .build();

    projector.on(user);

    // THEN
    final UserDto found = handler.findByJmbag(new FindUserByJmbag("jmbagX"));

    assertThat(found.login()).isEqualTo(user.login());
  }

  @Test
  void noJmbag() throws Exception {
    assertThat(handler.findByJmbag(new FindUserByJmbag("noJmbag"))).isNull();
  }

  @Test
  void findByOauth2sub() throws Exception {
    // GIVEN
    final UserCreated user = UserCreatedFaker.builder()
        .login("l1")
        .build();

    projector.on(user);
    projector.on(new UserOauth2subUpdated("l1", "my sub"));

    // THEN
    final UserDto found = handler.findByOauth2sub(new FindUserByOauth2sub("my sub"));

    assertThat(found.login()).isEqualTo("l1");
  }

  @Test
  void noOauth2sub() throws Exception {
    assertThat(handler.findByOauth2sub(new FindUserByOauth2sub("noOauth2sub"))).isNull();
  }

  @Test
  void userDeleted() throws Exception {
    // GIVEN
    final UserCreated user1 = UserCreatedFaker.builder()
        .login("l1")
        .build();
    projector.on(user1);

    final UserDeleted deleted = new UserDeleted("l1");
    projector.on(deleted);

    // THEN
    final UsersPage allUsers = handler.allUsers(FindAllUsers.max_10_000());

    assertThat(allUsers.getNumberOfElements()).isEqualTo(0);
  }

  @Test
  void userRecreated() throws Exception {
    // GIVEN
    final UserCreated user1 = UserCreatedFaker.builder()
        .login("l1")
        .build();
    projector.on(user1);

    final UserDeleted deleted = new UserDeleted("l1");
    projector.on(deleted);

    final UserCreated recreated = UserCreatedFaker.builder()
        .login("l1")
        .build();
    projector.on(recreated);

    // THEN
    final UsersPage allUsers = handler.allUsers(FindAllUsers.max_10_000());

    assertThat(allUsers.getNumberOfElements()).isEqualTo(1);
    assertThat(allUsers.getContent().get(0).login()).isEqualTo("l1");
  }
}
