package org.example.netty.protocol;

import java.util.Arrays;

public enum ResponseType {
  PING_RESPONSE(1),
  CREATE_STORE_RESPONSE(2),
  GET_STORE_RESPONSE(3),
  PUT_STORE_RESPONSE(4),
  DELETE_STORE_RESPONSE(5);

  private final int id;

  ResponseType(int id) {
    this.id = id;
  }

  public static ResponseType fromId(int id) {
    var rt = Arrays.stream(ResponseType.values()).filter(p -> p.getId() == id).findFirst();

    if (rt.isEmpty()) {
      throw new IllegalArgumentException("no request found for idx: " + id);
    }

    return rt.get();
  }

  public int getId() {
    return id;
  }
}
