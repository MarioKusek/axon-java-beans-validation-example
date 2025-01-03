package hr.fer.axon.users;

import java.util.List;

import net.datafaker.providers.base.BaseFaker;
import net.datafaker.providers.base.Name;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.Schema;

public class UserDtoFaker {
  public static UserDto create() {
    BaseFaker faker = new BaseFaker();
    final Name name = faker.name();
    final String firstName = name.firstName();
    final String lastName = name.lastName();
    final String username = firstName.substring(0, 1).toLowerCase() + lastName.toLowerCase();

    return BaseFaker.populate(UserDto.class, Schema.of(
        Field.field("login", () -> username),
        Field.field("oauth2sub", () -> null),
        Field.field("jmbag", () -> faker.number().digits(12).toString()),
        Field.field("firstName", () -> firstName),
        Field.field("lastName", () -> lastName),
        Field.field("email", () -> faker.internet().emailAddress(username)),
        Field.field("durationFactor", () -> 1.0),
        Field.field("roleNames", () -> List.of())
        ));
  }

  public static UserDtoBuilder builder() {
    return UserDtoBuilder.builder(create());
  }

}