package hr.fer.axon.users.aggregates.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueUserOauth2subValidator implements ConstraintValidator<UniqueUserOauth2sub, String> {
  @Autowired
  private RegisteredUserReadModelRepository registeredUserRepository;

  @Override
  public boolean isValid(String oauth2sub, ConstraintValidatorContext context) {
    if (oauth2sub == null) {
      return true;
    }
    return !registeredUserRepository.existsByOauth2sub(oauth2sub);
  }
}