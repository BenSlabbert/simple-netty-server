package org.example.netty.protocol;

public record Response(ResponseType type, byte[] payload) {
  // msg structure:
  // first 4 bytes is the msg length
  // second 4 bytes is the msg type
}
