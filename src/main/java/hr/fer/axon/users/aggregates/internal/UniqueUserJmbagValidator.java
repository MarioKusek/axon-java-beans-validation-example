package hr.fer.axon.users.aggregates.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueUserJmbagValidator implements ConstraintValidator<UniqueUserJmbag, String> {
  @Autowired
  private RegisteredUserReadModelRepository registeredUserRepository;

  @Override
  public boolean isValid(String jmbag, ConstraintValidatorContext context) {
    if (jmbag == null) {
      return true;
    }
    return !registeredUserRepository.existsByJmbag(jmbag);
  }
}