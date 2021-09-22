package org.example.netty.client.handler.inbound;

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

    var msgBytes =
        new Gson()
            .toJson(new PingRequest().message("hello-initial`"))
            .getBytes(StandardCharsets.UTF_8);
    handleOption(ctx, ResponseOption.reply(new Request(RequestType.PING_REQUEST, msgBytes)));
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

  private CompletableFuture<ResponseOption> handleResponse(Response response) {
    LOG.info("got response type: " + response.type());
    return switch (response.type()) {
      case PING_RESPONSE -> handlePingResponse(parseJson(response, PingResponse.TYPE_TOKEN));
      default -> throw new IllegalArgumentException("unsupported type: " + response.type());
    };
  }

  private CompletableFuture<ResponseOption> handlePingResponse(PingResponse response) {
    LOG.info("resp: " + response.message());

    int i = COUNT.incrementAndGet();
    if (i > 5) {
      LOG.warn("closing client connection");
      return CompletableFuture.completedFuture(ResponseOption.disconnect());
    }

    var pingRequest = new PingRequest().message("hello-" + i);
    var msgBytes = new Gson().toJson(pingRequest).getBytes(StandardCharsets.UTF_8);
    var responseOption = ResponseOption.reply(new Request(RequestType.PING_REQUEST, msgBytes));
    return CompletableFuture.completedFuture(responseOption);
  }

  private CompletableFuture<Void> handleOption(
      ChannelHandlerContext ctx, ResponseOption responseOption) {
    switch (responseOption.option) {
      case REPLY -> ctx.writeAndFlush(responseOption.request);
      case DO_NOTHING -> {
        /* do nothing */
      }
      case DISCONNECT -> ctx.close().addListener(channelFuture -> LOG.info("connection closed"));
    }

    return CompletableFuture.completedFuture(null);
  }

  record ResponseOption(Request request, Option option) {

    static ResponseOption reply(Request request) {
      return new ResponseOption(request, Option.REPLY);
    }

    static ResponseOption doNothing() {
      return new ResponseOption(null, Option.DO_NOTHING);
    }

    static ResponseOption disconnect() {
      return new ResponseOption(null, Option.DISCONNECT);
    }

    enum Option {
      REPLY,
      DO_NOTHING,
      DISCONNECT
    }
  }

  private <T> T parseJson(Response request, TypeToken<T> tTypeToken) {
    Reader reader = new InputStreamReader(new ByteArrayInputStream(request.payload()));
    return GSON.fromJson(reader, tTypeToken.getType());
  }
}
