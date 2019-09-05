package Exceptions;

public class NoninvertibleException extends Exception {
  public NoninvertibleException() {
    super();
  }

  public NoninvertibleException(String message) {
    super(message);
  }

  public NoninvertibleException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoninvertibleException(Throwable cause) {
    super(cause);
  }
}
