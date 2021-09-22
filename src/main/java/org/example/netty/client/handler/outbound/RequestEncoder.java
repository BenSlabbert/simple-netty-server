package org.example.netty.client.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Request;

public class RequestEncoder extends MessageToByteEncoder<Request> {

  private static final Logger logger = Logger.getLogger(RequestEncoder.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    logger.info("write");
    super.write(ctx, msg, promise);
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) {
    logger.info("encoding message");

    out.writeInt(4 + msg.payload().length);
    out.writeInt(msg.type().getId());
    out.writeBytes(msg.payload());
  }
}
