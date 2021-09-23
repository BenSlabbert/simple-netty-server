package org.example.netty.protocol;

import java.util.Arrays;

public enum RequestType {
  PING_REQUEST(1),
  CREATE_STORE_REQUEST(2),
  GET_STORE_REQUEST(3),
  PUT_STORE_REQUEST(4),
  DELETE_STORE_REQUEST(5);

  private final int id;

  RequestType(int id) {
    this.id = id;
  }

  public static RequestType fromId(int id) {
    var rt = Arrays.stream(RequestType.values()).filter(p -> p.getId() == id).findFirst();

    if (rt.isEmpty()) {
      throw new IllegalArgumentException("no request found for idx: " + id);
    }

    return rt.get();
  }

  public int getId() {
    return id;
  }
}
