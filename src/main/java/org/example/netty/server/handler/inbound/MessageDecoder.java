package org.example.netty.server.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.RequestType;

public class MessageDecoder extends ByteToMessageDecoder {

  private static final Logger logger = Logger.getLogger(MessageDecoder.class);

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    logger.info(String.format("thread id: %d channel registered", Thread.currentThread().getId()));
    super.channelRegistered(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.info(String.format("thread id: %d channel active", Thread.currentThread().getId()));
    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    logger.info("channelRead");
    super.channelRead(ctx, msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    logger.info(
        String.format("thread id: %d channel read complete", Thread.currentThread().getId()));
    super.channelReadComplete(ctx);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    int readableBytes = in.readableBytes();
    if (readableBytes < 8) {
      return; // call me again with more data
    }

    // subtract 4, the first 4 are is the message length
    int payloadLength = in.readBytes(4).readInt() - 4;
    if (readableBytes < payloadLength) {
      in.resetReaderIndex();
      return; // call me again with more data
    }

    // we now have the whole message, deserialize it

    int msgType = in.readBytes(4).readInt();
    var requestType = RequestType.fromIdx(msgType);
    var msgBytes = new byte[payloadLength];

    in.readBytes(msgBytes);

    var req =
        switch (requestType) {
          case PING_REQUEST -> new Request(requestType, msgBytes);
          default -> throw new IllegalArgumentException("unsupported request type: " + requestType);
        };

    out.add(req);
  }
}
