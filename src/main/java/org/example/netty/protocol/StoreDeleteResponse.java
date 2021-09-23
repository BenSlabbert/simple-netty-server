package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class StoreDeleteResponse {

  public static final TypeToken<StoreDeleteResponse> TYPE_TOKEN = new TypeToken<>() {};

  private boolean ok;

  public StoreDeleteResponse ok(boolean ok) {
    this.ok = ok;
    return this;
  }

  public boolean ok() {
    return ok;
  }
}
