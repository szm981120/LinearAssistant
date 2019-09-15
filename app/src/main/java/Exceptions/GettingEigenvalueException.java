package Exceptions;

public class GettingEigenvalueException extends Exception {
  public GettingEigenvalueException() {
    super();
  }

  public GettingEigenvalueException(String message) {
    super(message);
  }

  public GettingEigenvalueException(String message, Throwable cause) {
    super(message, cause);
  }

  public GettingEigenvalueException(Throwable cause) {
    super(cause);
  }
}
