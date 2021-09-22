package org.example.netty.server.handler.inbound;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;
import org.example.netty.protocol.PingRequest;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.Response;
import org.example.netty.protocol.ResponseType;

public class BusinessLogicHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = Logger.getLogger(BusinessLogicHandler.class);
  private static final Gson GSON = new Gson();

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    LOG.info("client disconnected");
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    LOG.info(String.format("thread id: %d channel registered", Thread.currentThread().getId()));
    super.channelRegistered(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    LOG.info(String.format("thread id: %d channel active", Thread.currentThread().getId()));
    super.channelActive(ctx);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    LOG.info(String.format("thread id: %d channel read complete", Thread.currentThread().getId()));
    // come here when the decoder returns without adding to List<Object> out
    // i.e. channel has completed its read, there may be more data coming
    super.channelReadComplete(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    LOG.info(String.format("thread id: %d channel read start", Thread.currentThread().getId()));

    Request request = (Request) msg;
    LOG.info("got request type: " + request.type());

    switch (request.type()) {
      case PING_REQUEST -> {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(request.payload()));
        PingRequest pingRequest = GSON.fromJson(reader, new TypeToken<PingRequest>() {}.getType());
        LOG.info("ping request message: " + pingRequest.getMessage());
      }
      default -> throw new IllegalArgumentException("unsupported type: " + request.type());
    }

    // this call to write will call all the ChannelOutboundHandlerAdapter(s)
    ctx.writeAndFlush(
            new Response(ResponseType.PING_RESPONSE, "pong!".getBytes(StandardCharsets.UTF_8)))
        .addListener(
            new ChannelFutureListener() {
              @Override
              public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOG.info("finished write to client");
              }
            });
    //    add back ChunkedWriteHandler to handle the input stream
    //    var chunkedStream =
    //        new ChunkedStream(
    //            new ByteArrayInputStream(
    //                "this is a message from an input stream\n".getBytes(StandardCharsets.UTF_8)));
    //
    //    ctx.write("server says hello\n");
    //    ctx.write(chunkedStream);
    //    ChannelFuture future = ctx.writeAndFlush("\n");
    //    future.addListener(
    //        new ChannelFutureListener() {
    //          @Override
    //          public void operationComplete(ChannelFuture channelFuture) throws Exception {
    //            logger.info("finished write to client, write stream");
    //          }
    //        });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
