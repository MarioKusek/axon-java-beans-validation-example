package hr.fer.axon.utils;

import org.axonframework.common.AxonNonTransientException;

public class ViolationException extends AxonNonTransientException {

  public ViolationException(String message) {
    super(message);
  }

  public ViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}
