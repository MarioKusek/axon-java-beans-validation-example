package hr.fer.axon.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.List;

import org.junit.jupiter.api.Test;

class UserDtoTest {

  @Test
  void addRolesFromCollection() {
    List<String> roles = List.of("b", "c");
    UserDto dto = new UserDto(
    null,
    null,
    null,
    null,
    null,
    null,
    0,
    roles);

    UserDto newDto = dto.addRoles(List.of("a", "x"));

    assertThat(newDto.roleNames())
      .containsExactly("a", "b", "c", "x");
  }

  @Test
  void removeRolesFromCollection() {
    List<String> roles = List.of("b", "c", "d");
    UserDto dto = new UserDto(
        null,
        null,
        null,
        null,
        null,
        null,
        0,
        roles);

    UserDto newDto = dto.removeRoles(List.of("b", "x"));

    assertThat(newDto.roleNames())
      .containsExactly("c", "d");
  }

  @Test
  void changeLogin() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setLogin("ll");

    assertThat(newDto.login()).isEqualTo("ll");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeOAuth2sub() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setOauth2sub("uid");

    assertThat(newDto.oauth2sub()).isEqualTo("uid");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeJmbag() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setJmbag("jmbag");

    assertThat(newDto.jmbag()).isEqualTo("jmbag");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeFirstName() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setFirstName("fn");

    assertThat(newDto.firstName()).isEqualTo("fn");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeLastName() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setLastName("ln");

    assertThat(newDto.lastName()).isEqualTo("ln");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeEmail() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setEmail("eml");

    assertThat(newDto.email()).isEqualTo("eml");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeDurationFactor() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setDurationFactor(1.5);

    assertThat(newDto.durationFactor()).isCloseTo(1.5, offset(1E-4));
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void changeRoleNames() throws Exception {
    UserDto dto = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.setRoleNames(List.of("a", "d"));

    assertThat(newDto.roleNames())
      .containsExactly("a", "d");
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void mergeNotNulls_whenAllFieldsAreNull() throws Exception {
    UserDto dto = new UserDto("l", "uid", "j", "fn", "ln", "e", 0.5, List.of("r"));
    UserDto dto2 = new UserDto(null, null, null, null, null, null, 0, null);

    UserDto newDto = dto.mergeNotNulls(dto2);

    assertThat(newDto).usingRecursiveComparison()
      .isEqualTo(dto);
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void mergeNotNulls_whenLoginIsDifferent_itIsNotChanged() throws Exception {
    UserDto dto = new UserDto("l", "uid", "j", "fn", "ln", "e", 1.5, List.of("r"));
    UserDto dto2 = new UserDto("l2", null, null, null, null, null, -1, null);

    UserDto newDto = dto.mergeNotNulls(dto2);

    assertThat(newDto).usingRecursiveComparison()
      .isEqualTo(dto);
    assertThat(newDto).isNotSameAs(dto2);
  }

  @Test
  void mergeNotNulls_whenRoleNamesAreSet_TheyAreAppended() throws Exception {
    UserDto dto = new UserDto("l", "uid", "j", "fn", "ln", "e", 1.5, List.of("r", "rr"));
    UserDto dto2 = new UserDto("l", null, null, null, null, null, -1.0, List.of("a", "rr"));

    UserDto newDto = dto.mergeNotNulls(dto2);

    assertThat(newDto).usingRecursiveComparison()
      .isEqualTo(new UserDto("l", "uid", "j", "fn", "ln", "e", 1.5, List.of("a", "r", "rr")));
    assertThat(newDto).isNotSameAs(dto2);
  }

  @Test
  void mergeNotNulls_whenAllFieldsAreNotNull() throws Exception {
    UserDto dto = new UserDto("l", "uid", "j", "fn", "ln", "e", 1.5, List.of("r"));
    UserDto dto2 = new UserDto("l", "uid2", "j2", "fn2", "ln2", "e2", 1.7, List.of("rr"));

    UserDto newDto = dto.mergeNotNulls(dto2);

    assertThat(newDto).usingRecursiveComparison()
      .isEqualTo(new UserDto("l", "uid2", "j2", "fn2", "ln2", "e2", 1.7, List.of("r", "rr")));
    assertThat(newDto).isNotSameAs(dto2);
  }

  @Test
  void mergeNotNulls_whenSomeFieldsAreSet() throws Exception {
    UserDto dto = new UserDto("l", "uid", "j", "fn", "ln", "e", 0.5, List.of("r"));
    UserDto dto2 = new UserDto(null, "uid2", null, "fn2", null, null, 0, List.of("x", "xx"));

    UserDto newDto = dto.mergeNotNulls(dto2);

    assertThat(newDto).usingRecursiveComparison()
        .isEqualTo(new UserDto("l", "uid2", "j", "fn2", "ln", "e", 0.5, List.of("r", "x", "xx")));
    assertThat(newDto).isNotSameAs(dto);
  }

  @Test
  void builder() throws Exception {
    UserDto dto = UserDtoBuilder.builder()
        .login("l1")
        .build();

    assertThat(dto.login()).isEqualTo("l1");
  }

  @Test
  void builderWith() throws Exception {
    UserDto dto = new UserDto("l1", "sub", null, null, null, null, 0, null);

    UserDto newDto = UserDtoBuilder.from(dto)
        .withLogin("l2");

    assertThat(newDto.login()).isEqualTo("l2");
    assertThat(newDto.oauth2sub()).isEqualTo("sub");
  }

  @Test
  void faker() throws Exception {
    final UserDto dto = UserDtoFaker.builder()
      .oauth2sub("some oauth2sub")
      .login("l1")
      .build();

    assertThat(dto.oauth2sub()).isEqualTo("some oauth2sub");
    assertThat(dto.login()).isEqualTo("l1");
    assertThat(dto.jmbag()).isNotBlank();
    assertThat(dto.firstName()).isNotNull();
    assertThat(dto.lastName()).isNotNull();
    assertThat(dto.email()).isNotNull();
    assertThat(dto.durationFactor()).isCloseTo(1.0, offset(1E-3));
    assertThat(dto.roleNames()).isEmpty();
  }

}
