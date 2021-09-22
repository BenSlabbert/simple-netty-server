package org.example.netty.protocol;

public class PingRequest {

  private String message;

  public PingRequest(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
