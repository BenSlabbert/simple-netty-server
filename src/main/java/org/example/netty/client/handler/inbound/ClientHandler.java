package org.example.netty.client.handler.inbound;

import com.google.gson.Gson;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.example.netty.protocol.PingRequest;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.RequestType;
import org.example.netty.protocol.Response;

public class ClientHandler extends SimpleChannelInboundHandler<Response> {

  private static final Logger logger = Logger.getLogger(ClientHandler.class);
  private static final AtomicInteger COUNT = new AtomicInteger(0);

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    logger.info("channelActive");
    sendPing(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    logger.info("channelRead");
    super.channelRead(ctx, msg);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Response in) {
    // this is a convenience method which discards the ByteBuf for us
    logger.info("channelRead0");

    logger.info(
        "respType: "
            + in.type()
            + " respBody: "
            + new String(in.payload(), StandardCharsets.UTF_8));

    logger.info("send another ping");
    sendPing(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.info("exceptionCaught");
    super.exceptionCaught(ctx, cause);
  }

  private void sendPing(ChannelHandlerContext ctx) {
    int i = COUNT.incrementAndGet();
    if (i > 5) {
      logger.info("closing client connection");
      ctx.close()
          .addListener(
              new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                  logger.info("connection closed");
                }
              });
      return;
    }

    var msgBytes =
        new Gson().toJson(new PingRequest("hello-" + i)).getBytes(StandardCharsets.UTF_8);
    ctx.writeAndFlush(new Request(RequestType.PING_REQUEST, msgBytes));
  }
}
