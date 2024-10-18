package gr.georpavl.jwtAuth.api.security.services;

enum LogoutMessages {
  SUCCESS("Logout successful"),
  INVALID_TOKEN("Invalid token or user");

  private final String message;

  LogoutMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
