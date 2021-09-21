package org.example.netty.server.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.ReferenceCountUtil;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;

public class BusinessLogicHandler extends ChannelInboundHandlerAdapter {

  private static final Logger logger = Logger.getLogger(BusinessLogicHandler.class);

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    logger.info("client disconnected");
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    logger.info(String.format("thread id: %d channel registered", Thread.currentThread().getId()));
    super.channelRegistered(ctx);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    logger.info(
        String.format("thread id: %d channel read complete", Thread.currentThread().getId()));
    super.channelReadComplete(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // todo not sure if we should use this or channelReadComplete :/

    logger.info(String.format("thread id: %d channel read start", Thread.currentThread().getId()));
    ByteBuf in = (ByteBuf) msg;
    StringBuilder clientMessage = new StringBuilder();
    try {
      // telnet is sending \r\n to end the message
      // these are 0x0D and 0x0A in hex
      logger.info("readable bytes: " + in.readableBytes());

      while (in.isReadable()) {
        byte readByte = in.readByte();
        logger.info(String.format("read byte : 0x%02X ", readByte));
        clientMessage.append((char) readByte);
      }
    } finally {
      ReferenceCountUtil.release(msg);
    }

    logger.info("client message: " + clientMessage);

    var chunkedStream =
        new ChunkedStream(
            new ByteArrayInputStream(
                "this is a message from an input stream\n".getBytes(StandardCharsets.UTF_8)));

    ctx.write("server says hello\n");
    ctx.write(chunkedStream);
    ChannelFuture future = ctx.writeAndFlush("\n");
    future.addListener(
        new ChannelFutureListener() {
          @Override
          public void operationComplete(ChannelFuture channelFuture) throws Exception {
            logger.info("finished write to client, write stream");
          }
        });

    logger.info(String.format("thread id: %d channel read end", Thread.currentThread().getId()));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
