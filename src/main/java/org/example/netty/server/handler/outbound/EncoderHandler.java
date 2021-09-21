package org.example.netty.server.handler.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;

public class EncoderHandler extends ChannelOutboundHandlerAdapter {

  private static final Logger logger = Logger.getLogger(EncoderHandler.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    logger.info("write");

    if (msg instanceof String outboundMsg) {
      ByteBuf buffer = ctx.alloc().buffer();
      buffer.writeCharSequence("encoder modified message ->" + outboundMsg, StandardCharsets.UTF_8);
      ChannelFuture future = ctx.write(buffer, promise);
      new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
          if (channelFuture == future) {
            logger.info("write completed");
          }
        }
      };
      return;
    }

    logger.info("writing with type: " + msg.getClass().getName());
    super.write(ctx, msg, promise);
  }

  @Override
  public void flush(ChannelHandlerContext ctx) throws Exception {
    logger.info("flush");
    super.flush(ctx);
  }

  @Override
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
    logger.info("close");
    ctx.close(promise);
  }
}
