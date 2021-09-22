package org.example.netty.client.handler.inbound;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.example.netty.protocol.CreateStoreRequest;
import org.example.netty.protocol.CreateStoreResponse;
import org.example.netty.protocol.PingRequest;
import org.example.netty.protocol.PingResponse;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.RequestType;
import org.example.netty.protocol.Response;

public class ClientHandler extends SimpleChannelInboundHandler<Response> {

  private static final Logger LOG = Logger.getLogger(ClientHandler.class);
  private static final AtomicInteger COUNT = new AtomicInteger(0);
  private static final Gson GSON = new Gson();

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    LOG.info("channelActive");

    var req = createRequest(RequestType.PING_REQUEST, new PingRequest().message("hello-initial`"));
    handleOption(ctx, RequestOption.reply(req));
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    LOG.info("channelRead");
    super.channelRead(ctx, msg);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Response in) {
    // this is a convenience method which discards the ByteBuf for us
    LOG.info("channelRead0");
    handleResponse(in).thenApply(opt -> handleOption(ctx, opt));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOG.info("exceptionCaught");
    super.exceptionCaught(ctx, cause);
  }

  private CompletableFuture<RequestOption> handleResponse(Response response) {
    LOG.info("got response type: " + response.type());
    return switch (response.type()) {
      case PING_RESPONSE -> handlePingResponse(parseJson(response, PingResponse.TYPE_TOKEN));
      case CREATE_STORE_RESPONSE -> handleCreateStoreResponse(
          parseJson(response, CreateStoreResponse.TYPE_TOKEN));
        case GET_STORE_RESPONSE -> null; // todo complete
        case PUT_STORE_RESPONSE -> null; // todo complete
    };
  }

  private CompletableFuture<RequestOption> handleCreateStoreResponse(CreateStoreResponse resp) {
    LOG.info("created store: " + resp.name());
    LOG.info("store values: " + resp.values());
    var responseOption = RequestOption.disconnect();
    return completedFuture(responseOption);
  }

  private CompletableFuture<RequestOption> handlePingResponse(PingResponse resp) {
    LOG.info("resp: " + resp.message());

    int i = COUNT.incrementAndGet();
    if (i > 1) {
      LOG.info("creating a store");

      // create a store
      var req = new CreateStoreRequest().name("test-store");
      return completedFuture(
          RequestOption.reply(createRequest(RequestType.CREATE_STORE_REQUEST, req)));
    }

    var req = new PingRequest().message("hello-" + i);
    return completedFuture(RequestOption.reply(createRequest(RequestType.PING_REQUEST, req)));
  }

  private Request createRequest(RequestType requestType, Object o) {
    var json = GSON.toJson(o);
    return new Request(requestType, json.getBytes(StandardCharsets.UTF_8));
  }

  private CompletableFuture<Void> handleOption(
      ChannelHandlerContext ctx, RequestOption requestOption) {
    switch (requestOption.option) {
      case REPLY -> ctx.writeAndFlush(requestOption.request);
      case DO_NOTHING -> {
        /* do nothing */
      }
      case DISCONNECT -> ctx.close().addListener(channelFuture -> LOG.info("connection closed"));
    }

    return completedFuture(null);
  }

  private <T> T parseJson(Response request, TypeToken<T> typeToken) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(request.payload()));
    return GSON.fromJson(reader, typeToken.getType());
  }

  record RequestOption(Request request, Option option) {

    static RequestOption reply(Request request) {
      return new RequestOption(request, Option.REPLY);
    }

    static RequestOption doNothing() {
      return new RequestOption(null, Option.DO_NOTHING);
    }

    static RequestOption disconnect() {
      return new RequestOption(null, Option.DISCONNECT);
    }

    enum Option {
      REPLY,
      DO_NOTHING,
      DISCONNECT
    }
  }
}
