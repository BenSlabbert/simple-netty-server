package org.example.netty.client.handler.inbound;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.example.netty.protocol.PingRequest;
import org.example.netty.protocol.RequestType;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

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
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
    // this is a convenience method which discards the ByteBuf for us
    logger.info("channelRead0");

    String s = in.toString(StandardCharsets.UTF_8);
    logger.info("resp: " + s);

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

    var msgBytes = new Gson().toJson(new PingRequest("hello")).getBytes(StandardCharsets.UTF_8);

    ByteBuf buffer = Unpooled.buffer();
    buffer.writeInt(4 + msgBytes.length);
    buffer.writeInt(RequestType.PING_REQUEST.getIdx());
    buffer.writeBytes(msgBytes);

    logger.info("msg length: " + (4 + msgBytes.length));
    logger.info("msg type: " + RequestType.PING_REQUEST.getIdx());
    logger.info("payload length: " + msgBytes.length);

    ctx.write(buffer);
    ctx.flush();
  }
}
