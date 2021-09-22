package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class PingRequest {

  public static final TypeToken<PingRequest> TYPE_TOKEN = new TypeToken<>() {};

  private String message;

  public String getMessage() {
    return message;
  }

  public PingRequest message(String message) {
    this.message = message;
    return this;
  }
}
