package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class StoreGetRequest {

  public static final TypeToken<StoreGetRequest> TYPE_TOKEN = new TypeToken<>() {};

  private String name;

  public StoreGetRequest name(String name) {
    this.name = name;
    return this;
  }

  public String name() {
    return name;
  }
}
