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
    var resp = handleRequest((Request) msg);

    // this call to write will call all the ChannelOutboundHandlerAdapter(s)
    ctx.writeAndFlush(resp)
        .addListener(
            new ChannelFutureListener() {
              @Override
              public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LOG.info("finished write to client");
              }
            });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

  private Response handleRequest(Request request) {
    LOG.info("got request type: " + request.type());
    return switch (request.type()) {
      case PING_REQUEST -> handlePing(request);
      default -> throw new IllegalArgumentException("unsupported type: " + request.type());
    };
  }

  private Response handlePing(Request request) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(request.payload()));
    PingRequest pingRequest = GSON.fromJson(reader, new TypeToken<PingRequest>() {}.getType());
    LOG.info("ping request message: " + pingRequest.getMessage());
    return new Response(ResponseType.PING_RESPONSE, "pong!".getBytes(StandardCharsets.UTF_8));
  }
}
