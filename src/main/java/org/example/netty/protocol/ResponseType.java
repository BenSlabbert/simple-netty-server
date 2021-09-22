package org.example.netty.protocol;

import java.util.Arrays;

public enum ResponseType {
  PING_RESPONSE(1);

  private final int idx;

  ResponseType(int idx) {
    this.idx = idx;
  }

  public static ResponseType fromIdx(int idx) {
    var rt = Arrays.stream(ResponseType.values()).filter(p -> p.getIdx() == idx).findFirst();

    if (rt.isEmpty()) {
      throw new IllegalArgumentException("no request found for idx: " + idx);
    }

    return rt.get();
  }

  public int getIdx() {
    return idx;
  }
}
