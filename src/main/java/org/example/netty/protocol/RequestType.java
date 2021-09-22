package org.example.netty.protocol;

import java.util.Arrays;

public enum RequestType {
  PING_REQUEST(1);

  private final int idx;

  RequestType(int idx) {
    this.idx = idx;
  }

  public static RequestType fromIdx(int idx) {
    var rt = Arrays.stream(RequestType.values()).filter(p -> p.getIdx() == idx).findFirst();

    if (rt.isEmpty()) {
      throw new IllegalArgumentException("no request found for idx: " + idx);
    }

    return rt.get();
  }

  public int getIdx() {
    return idx;
  }
}
