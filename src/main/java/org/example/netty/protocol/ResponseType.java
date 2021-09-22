package org.example.netty.protocol;

import java.util.Arrays;

public enum ResponseType {
  PING_RESPONSE(1);

  private final int id;

  ResponseType(int id) {
    this.id = id;
  }

  public static ResponseType fromId(int idx) {
    var rt = Arrays.stream(ResponseType.values()).filter(p -> p.getId() == idx).findFirst();

    if (rt.isEmpty()) {
      throw new IllegalArgumentException("no request found for idx: " + idx);
    }

    return rt.get();
  }

  public int getId() {
    return id;
  }
}
