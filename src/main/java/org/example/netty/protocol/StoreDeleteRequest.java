package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class StoreDeleteRequest {

  public static final TypeToken<StoreDeleteRequest> TYPE_TOKEN = new TypeToken<>() {};

  private String name;
  private String key;
  private boolean all;

  public StoreDeleteRequest name(String name) {
    this.name = name;
    return this;
  }

  public StoreDeleteRequest key(String key) {
    this.key = key;
    return this;
  }

  public StoreDeleteRequest name(boolean all) {
    this.all = all;
    return this;
  }

  public String name() {
    return name;
  }

  public String key() {
    return key;
  }

  public boolean all() {
    return all;
  }
}
