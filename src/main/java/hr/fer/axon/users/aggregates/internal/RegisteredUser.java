package hr.fer.axon.users.aggregates.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_uniqueness")
public class RegisteredUser {
  @Id
  @Column(name = "login")
  private String login;

  @Column(name = "jmbag")
  private String jmbag;

  @Column(name = "oauth2sub")
  private String oauth2sub;

  public RegisteredUser() {
  }

  public RegisteredUser(String login, String jmbag, String oauth2sub) {
    this.login = login;
    this.jmbag = jmbag;
    this.oauth2sub = oauth2sub;
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
}
