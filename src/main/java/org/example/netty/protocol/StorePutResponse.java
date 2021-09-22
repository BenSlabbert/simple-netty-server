package org.example.netty.protocol;

import com.google.gson.reflect.TypeToken;

public class StorePutResponse {

  public static final TypeToken<StorePutResponse> TYPE_TOKEN = new TypeToken<>() {};

  private boolean ok;

  public StorePutResponse ok(boolean ok) {
    this.ok = ok;
    return this;
  }
}
