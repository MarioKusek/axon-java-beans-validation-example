package hr.fer.axon.users.user_list;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="user_list")
public class UserListEntity {
  @Id
  private String login;

  private String jmbag;

  private String oauth2sub;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "duration_factor")
  private double durationFactor = 1.0;

  @ElementCollection
  @CollectionTable(name = "user_roles_list", joinColumns = @JoinColumn(name = "login"))
  @Column(name = "role")
  private Set<String> roleNames;

  public UserListEntity() {
  }

  public UserListEntity(String login, String jmbag, String oauth2sub, String firstName, String lastName, String email,
      double durationFactor, Set<String> roleNames) {
    this.login = login;
    this.jmbag = jmbag;
    this.oauth2sub = oauth2sub;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.durationFactor = durationFactor;
    this.roleNames = roleNames;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getJmbag() {
    return jmbag;
  }

  public void setJmbag(String jmbag) {
    this.jmbag = jmbag;
  }

  public String getOauth2sub() {
    return oauth2sub;
  }

  public void setOauth2sub(String oauth2sub) {
    this.oauth2sub = oauth2sub;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public double getDurationFactor() {
    return durationFactor;
  }

  public void setDurationFactor(double durationFactor) {
    this.durationFactor = durationFactor;
  }

  public Set<String> getRoleNames() {
    return roleNames;
  }

  public void setRoleNames(Set<String> roleNames) {
    this.roleNames = roleNames;
  }

}
