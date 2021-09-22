package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class CreateStoreResponse {

  public static final TypeToken<CreateStoreResponse> TYPE_TOKEN = new TypeToken<>() {};

  private String name;
  private Map<String, byte[]> values;

  public CreateStoreResponse name(String name) {
    this.name = name;
    return this;
  }

  public CreateStoreResponse values(Map<String, byte[]> values) {
    this.values = values;
    return this;
  }

  public String name() {
    return name;
  }

  public Map<String, byte[]> values() {
    return values;
  }
}
