package hr.fer.axon.users.aggregates.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueUserLoginValidator implements ConstraintValidator<UniqueUserLogin, String> {
  @Autowired
  private RegisteredUserReadModelRepository registeredUserRepository;

  @Override
  public boolean isValid(String login, ConstraintValidatorContext context) {
    if (login == null) {
      return true;
    }
    return !registeredUserRepository.existsByLogin(login);
  }
}