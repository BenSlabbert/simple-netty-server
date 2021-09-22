package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class PingResponse {

  public static final TypeToken<PingResponse> TYPE_TOKEN = new TypeToken<>() {};

  private String message;

  public String message() {
    return message;
  }

  public PingResponse message(String message) {
    this.message = message;
    return this;
  }
}
