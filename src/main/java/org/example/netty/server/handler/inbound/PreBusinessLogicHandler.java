package org.example.netty.server.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

public class PreBusinessLogicHandler extends ChannelInboundHandlerAdapter {

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

  private static final Logger logger = Logger.getLogger(PreBusinessLogicHandler.class);

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    logger.info("client disconnected");
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    logger.info(String.format("thread id: %d channel registered", Thread.currentThread().getId()));

    if (false) {
      logger.warn("do not call the next handler");
    } else {
      logger.warn("call the next handler");
      super.channelRegistered(ctx);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.info(String.format("thread id: %d channel active", Thread.currentThread().getId()));
    super.channelActive(ctx);
  }
}
