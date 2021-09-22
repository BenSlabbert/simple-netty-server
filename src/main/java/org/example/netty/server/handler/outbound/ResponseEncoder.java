package org.example.netty.server.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Response;

public class ResponseEncoder extends MessageToByteEncoder<Response> {

  private static final Logger LOG = Logger.getLogger(ResponseEncoder.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    LOG.info("write");
    super.write(ctx, msg, promise);
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) {
    LOG.info("encoding message");

    out.writeInt(4 + msg.payload().length);
    out.writeInt(msg.type().getId());
    out.writeBytes(msg.payload());
  }
}
