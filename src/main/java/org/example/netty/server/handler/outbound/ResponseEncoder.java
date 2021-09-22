package org.example.netty.server.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Response;

public class ResponseEncoder extends MessageToByteEncoder<Response> {

  private static final Logger logger = Logger.getLogger(ResponseEncoder.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    logger.info("write");
    super.write(ctx, msg, promise);
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) {
    logger.info("encoding message");

    out.writeInt(4 + msg.payload().length);
    out.writeBytes(ByteBuffer.allocate(4).putInt(msg.type().getIdx()).array());
    out.writeBytes(msg.payload());
  }
}
