package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class CreateStoreRequest {

  public static final TypeToken<CreateStoreRequest> TYPE_TOKEN = new TypeToken<>() {};

  private String name;

  public CreateStoreRequest name(String name) {
    this.name = name;
    return this;
  }

  public String name() {
    return name;
  }
}
