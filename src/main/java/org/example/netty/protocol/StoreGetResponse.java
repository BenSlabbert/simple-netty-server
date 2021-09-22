package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class StoreGetResponse {

  public static final TypeToken<StoreGetResponse> TYPE_TOKEN = new TypeToken<>() {};

  private Map<String, byte[]> values;

  public StoreGetResponse values(Map<String, byte[]> values) {
    this.values = values;
    return this;
  }

  public Map<String, byte[]> values() {
    return values;
  }
}
