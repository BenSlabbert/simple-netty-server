package org.example.netty.client.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Response;
import org.example.netty.protocol.ResponseType;

public class ResponseDecoder extends ByteToMessageDecoder {

  private static final Logger LOG = Logger.getLogger(ResponseDecoder.class);

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
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    LOG.info("channelRead");
    super.channelRead(ctx, msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    LOG.info("channel read complete");
    super.channelReadComplete(ctx);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    int readableBytes = in.readableBytes();
    if (readableBytes < 8) {
      return; // call me again with more data
    }

    // subtract 4, the first 4 are is the message length
    var buf = in.readBytes(4);
    int payloadLength = buf.readInt() - 4;
    buf.release();

    if (readableBytes < payloadLength) {
      in.resetReaderIndex();
      return; // call me again with more data
    }

    // we now have the whole message, deserialize it

    buf = in.readBytes(4);
    int msgType = buf.readInt();
    buf.release();

    var requestType = ResponseType.fromId(msgType);
    var msgBytes = new byte[payloadLength];

    in.readBytes(msgBytes);

    var req =
        switch (requestType) {
          case PING_RESPONSE -> new Response(requestType, msgBytes);
          default -> throw new IllegalArgumentException("unsupported request type: " + requestType);
        };

    out.add(req);
  }
}
