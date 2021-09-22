package org.example.netty.protocol;

public class PingResponse {

  private String message;

  public PingResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
