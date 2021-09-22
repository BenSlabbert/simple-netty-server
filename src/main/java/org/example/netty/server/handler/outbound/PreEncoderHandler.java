package org.example.netty.server.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Response;

public class PreEncoderHandler extends ChannelOutboundHandlerAdapter {

  // see: https://netty.io/4.1/api/io/netty/channel/ChannelPipeline.html
  // Forwarding an event to the next handler:
  // Outbound event propagation methods:
  //
  //    ChannelOutboundInvoker.bind(SocketAddress, ChannelPromise)
  //    ChannelOutboundInvoker.connect(SocketAddress, SocketAddress, ChannelPromise)
  //    ChannelOutboundInvoker.write(Object, ChannelPromise)
  //    ChannelHandlerContext.flush()
  //    ChannelHandlerContext.read()
  //    ChannelOutboundInvoker.disconnect(ChannelPromise)
  //    ChannelOutboundInvoker.close(ChannelPromise)
  //    ChannelOutboundInvoker.deregister(ChannelPromise)

  private static final Logger LOG = Logger.getLogger(PreEncoderHandler.class);

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    LOG.info("write");

    if (msg instanceof Response req) {
      LOG.info("pre processing Response: " + req);
    } else {
      LOG.info("pre processing response");
    }

    super.write(ctx, msg, promise);
  }

  @Override
  public void flush(ChannelHandlerContext ctx) throws Exception {
    LOG.info("flush");
    super.flush(ctx);
  }

  @Override
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
    LOG.info("close");
    ctx.close(promise);
  }
}
