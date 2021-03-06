package org.example.netty.server.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.apache.log4j.Logger;
import org.example.netty.protocol.Request;
import org.example.netty.protocol.RequestType;

public class RequestDecoder extends ByteToMessageDecoder {

  private static final Logger LOG = Logger.getLogger(RequestDecoder.class);

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

    var requestType = RequestType.fromId(msgType);
    var msgBytes = new byte[payloadLength];

    in.readBytes(msgBytes);

    out.add(new Request(requestType, msgBytes));
  }
}
