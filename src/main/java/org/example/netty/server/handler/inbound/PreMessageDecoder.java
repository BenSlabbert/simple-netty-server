package org.example.netty.server.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

// see: https://netty.io/4.1/api/io/netty/channel/ChannelPipeline.html
// Forwarding an event to the next handler:
// Inbound event propagation methods:
//
//    ChannelHandlerContext.fireChannelRegistered()
//    ChannelHandlerContext.fireChannelActive()
//    ChannelHandlerContext.fireChannelRead(Object)
//    ChannelHandlerContext.fireChannelReadComplete()
//    ChannelHandlerContext.fireExceptionCaught(Throwable)
//    ChannelHandlerContext.fireUserEventTriggered(Object)
//    ChannelHandlerContext.fireChannelWritabilityChanged()
//    ChannelHandlerContext.fireChannelInactive()
//    ChannelHandlerContext.fireChannelUnregistered()
public class PreMessageDecoder extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = Logger.getLogger(PreMessageDecoder.class);

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    LOG.info("client disconnected");
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    LOG.info("channelRead");
    LOG.info("pre processing request");
    super.channelRead(ctx, msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel read complete");
    super.channelReadComplete(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel registered");

    if (false) {
      LOG.warn("do not call the next handler");
    } else {
      LOG.warn("call the next handler");
      super.channelRegistered(ctx);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel active");
    super.channelActive(ctx);
  }
}
