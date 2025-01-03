package hr.fer.axon.users;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record UserDto(
    String login,
    String oauth2sub,
    String jmbag,
    String firstName,
    String lastName,
    String email,
    double durationFactor,
    List<String> roleNames) {
  public UserDto {
    if(roleNames != null)
      roleNames = roleNames.stream()
        .filter(r -> !r.isBlank())
        .distinct()
        .sorted()
        .toList();
  }

  public UserDto addRoles(String... roles) {
    Set<String> newRoles = new TreeSet<>();
    newRoles.addAll(roleNames);
    for(String r: roles)
      newRoles.add(r);

    return new UserDto(login, oauth2sub, jmbag, firstName, lastName, email, durationFactor, newRoles.stream().toList());
  }

  public UserDto addRoles(Collection<String> roles) {
    Set<String> newRoles = new TreeSet<>();
    newRoles.addAll(roleNames);
    newRoles.addAll(roles);

    return new UserDto(login, oauth2sub, jmbag, firstName, lastName, email, durationFactor, newRoles.stream().toList());
  }

  public UserDto removeRoles(Collection<String> roles) {
    Set<String> newRoles = new TreeSet<>();
    newRoles.addAll(roleNames);
    newRoles.removeAll(roles);

    return new UserDto(login, oauth2sub, jmbag, firstName, lastName, email, durationFactor, newRoles.stream().toList());
  }

  public UserDto setLogin(String newLogin) {
    return new UserDto(newLogin, oauth2sub, jmbag, firstName, lastName, email, durationFactor, roleNames);
  }

  public UserDto setOauth2sub(String newOauth2sub) {
    return new UserDto(login, newOauth2sub, jmbag, firstName, lastName, email, durationFactor, roleNames);
  }

  public UserDto setJmbag(String newJmbag) {
    return new UserDto(login, oauth2sub, newJmbag, firstName, lastName, email, durationFactor, roleNames);
  }

  public UserDto setFirstName(String newFirstName) {
    return new UserDto(login, oauth2sub, jmbag, newFirstName, lastName, email, durationFactor, roleNames);
  }

  public UserDto setLastName(String newLastName) {
    return new UserDto(login, oauth2sub, jmbag, firstName, newLastName, email, durationFactor, roleNames);
  }

  public UserDto setEmail(String newEmail) {
    return new UserDto(login, oauth2sub, jmbag, firstName, lastName, newEmail, durationFactor, roleNames);
  }

  public UserDto setDurationFactor(double newDurationFactor) {
    return new UserDto(login, oauth2sub, jmbag, firstName, lastName, email, newDurationFactor, roleNames);
  }

  public UserDto setRoleNames(List<String> newRoleNames) {
    return new UserDto(login, oauth2sub, jmbag, firstName, lastName, email, durationFactor, newRoleNames);
  }

  public UserDto mergeNotNulls(UserDto newDto) {
    List<String> newRoleNames = new LinkedList<>();
    if(roleNames != null)
      newRoleNames.addAll(roleNames);
    if(newDto.roleNames != null)
      newRoleNames.addAll(newDto.roleNames);

    return new UserDto(
        login,
        newDto.oauth2sub == null ? oauth2sub : newDto.oauth2sub,
        newDto.jmbag == null ? jmbag : newDto.jmbag,
        newDto.firstName == null ? firstName : newDto.firstName,
        newDto.lastName == null ? lastName : newDto.lastName,
        newDto.email == null ? email : newDto.email,
        newDto.durationFactor <= 1.0 ? durationFactor : newDto.durationFactor,
        newRoleNames);
  }

  public String fullName() {
    return String.format("%s %s", firstName, lastName);
  }

  public String fullNameWithJMBAG() {
    return String.format("%s %s (%s)", firstName, lastName, jmbag);
  }

  public String getRoleNamesAsString() {
    return roleNames.stream().collect(Collectors.joining(", "));
  }

  public static UserDto createNull() {
    return new UserDto(null, null, null, null, null, null, 1.0, List.of());
  }



}
