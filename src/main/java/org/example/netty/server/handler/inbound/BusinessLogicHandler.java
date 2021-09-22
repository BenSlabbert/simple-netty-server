package org.example.netty.server.handler.inbound;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.apache.log4j.Logger;
import org.example.netty.protocol.CreateStoreRequest;
import org.example.netty.protocol.CreateStoreResponse;
import org.example.netty.protocol.PingRequest;
import org.example.netty.protocol.PingResponse;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.Response;
import org.example.netty.protocol.ResponseType;

public class BusinessLogicHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = Logger.getLogger(BusinessLogicHandler.class);
  private static final Gson GSON = new Gson();

  private final RedisStringAsyncCommands<String, byte[]> async;
  private final RedisCommands<String, byte[]> sync;

  public BusinessLogicHandler(StatefulRedisConnection<String, byte[]> connection) {
    this.async = connection.async();
    this.sync = connection.sync();
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
      case PING_REQUEST -> handlePing(parseJson(request, PingRequest.TYPE_TOKEN));
      case CREAT_STORE_REQUEST -> handleCreateStore(
          parseJson(request, CreateStoreRequest.TYPE_TOKEN));
    };
  }

  private CompletableFuture<Response> handleCreateStore(CreateStoreRequest request) {
    LOG.info("create store: " + request.name());

    return supplyAsync(request::name)
        .thenCompose(
            storeName -> {
              if (sync.hget(storeName, "dummy") == null) {
                LOG.info("creating store");
                var ok = sync.hset(storeName, "dummy", "value".getBytes(StandardCharsets.UTF_8));
                LOG.info("creating store..." + ok);
              }

              return completedFuture(sync.hgetall(storeName));
            })
        .thenCompose(
            storeValues -> {
              var resp = new CreateStoreResponse().name(request.name()).values(storeValues);
              return completedFuture(createResponse(ResponseType.CREATE_STORE_RESPONSE, resp));
            });
  }

  private CompletableFuture<Response> handlePing(PingRequest request) {
    LOG.info("ping request message: " + request.getMessage());

    var resp = new PingResponse().message("pong!");
    return completedFuture(createResponse(ResponseType.PING_RESPONSE, resp));
  }

  private <T> T parseJson(Request request, TypeToken<T> typeToken) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(request.payload()));
    return GSON.fromJson(reader, typeToken.getType());
  }

  private Response createResponse(ResponseType responseType, Object o) {
    var json = GSON.toJson(o);
    return new Response(responseType, json.getBytes(StandardCharsets.UTF_8));
  }
}
