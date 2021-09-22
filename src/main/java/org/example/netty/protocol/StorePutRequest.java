package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class StorePutRequest {

  public static final TypeToken<StorePutRequest> TYPE_TOKEN = new TypeToken<>() {};

  private String name;
  private String key;
  private byte[] value;

  public StorePutRequest name(String name) {
    this.name = name;
    return this;
  }

  public StorePutRequest key(String key) {
    this.key = key;
    return this;
  }

  public StorePutRequest value(byte[] value) {
    this.value = value;
    return this;
  }

  public String name() {
    return name;
  }

  public String key() {
    return key;
  }

  public byte[] value() {
    return value;
  }
}
