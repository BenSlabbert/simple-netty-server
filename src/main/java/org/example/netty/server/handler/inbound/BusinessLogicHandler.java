package org.example.netty.server.handler.inbound;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.apache.log4j.Logger;
import org.example.netty.protocol.PingRequest;
import org.example.netty.protocol.PingResponse;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.Response;
import org.example.netty.protocol.ResponseType;

public class BusinessLogicHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = Logger.getLogger(BusinessLogicHandler.class);
  private static final Gson GSON = new Gson();

  private final RedisStringAsyncCommands<String, byte[]> async;

  public BusinessLogicHandler(StatefulRedisConnection<String, byte[]> connection) {
    this.async = connection.async();
    //        RedisFuture<String> set = async.set("key", "value".getBytes(StandardCharsets.UTF_8));
    //        RedisFuture<byte[]> get = async.get("key");
    //
    //        boolean allCompleted = LettuceFutures.awaitAll(Duration.ofMillis(500L), set, get);
    //        LOG.info("allCompleted: " + allCompleted);
    //        String s = set.get();
    //        byte[] s1 = get.get();
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    LOG.info("client disconnected");
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel registered");
    super.channelRegistered(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel active");
    super.channelActive(ctx);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel read complete");
    // come here when the decoder returns without adding to List<Object> out
    // i.e. channel has completed its read, there may be more data coming
    super.channelReadComplete(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    LOG.info("channel read start");
    handleRequest((Request) msg)
        .thenApply(
            resp ->
                ctx.writeAndFlush(resp)
                    .addListener(channelFuture -> LOG.info("finished write to client")));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  private CompletableFuture<Response> handleRequest(Request request) {
    LOG.info("got request type: " + request.type());
    return switch (request.type()) {
      case PING_REQUEST -> handlePing(request);
      default -> throw new IllegalArgumentException("unsupported type: " + request.type());
    };
  }

  private CompletableFuture<Response> handlePing(Request request) {
    return CompletableFuture.supplyAsync(
        () -> {
          LOG.info("handle ping");
          var pingRequest = parseJson(request, PingRequest.TYPE_TOKEN);
          LOG.info("ping request message: " + pingRequest.getMessage());

          var pingResponse = new PingResponse().message("pong!");
          var json = GSON.toJson(pingResponse);
          return new Response(ResponseType.PING_RESPONSE, json.getBytes(StandardCharsets.UTF_8));
        });
  }

  private <T> T parseJson(Request request, TypeToken<T> tTypeToken) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(request.payload()));
    return GSON.fromJson(reader, tTypeToken.getType());
  }
}
