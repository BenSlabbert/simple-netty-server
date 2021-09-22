package org.example.netty.server;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.example.netty.common.MyThreadFactory;
import org.example.netty.server.handler.inbound.BusinessLogicHandler;
import org.example.netty.server.handler.inbound.PreMessageDecoder;
import org.example.netty.server.handler.inbound.RequestDecoder;
import org.example.netty.server.handler.outbound.PreEncoderHandler;
import org.example.netty.server.handler.outbound.ResponseEncoder;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class);

  public static void main(String[] args) throws Exception {

    RedisClient client = RedisClient.create("redis://localhost");
    StatefulRedisConnection<String, byte[]> connection =
        client.connect(RedisCodec.of(new StringCodec(), new ByteArrayCodec()));

    var bossGroup = new EpollEventLoopGroup(1, new MyThreadFactory("boss"));
    var childGroup = new EpollEventLoopGroup(2, new MyThreadFactory("child"));

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, childGroup)
          .channel(EpollServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                  var p = ch.pipeline();
                  p.addLast("ideStateHandler", new IdleStateHandler(10, 10, 10));
                  // outbound handlers after inbound handlers
                  p.addLast("responseEncoder", new ResponseEncoder());
                  p.addLast("preEncoderHandler", new PreEncoderHandler());

                  p.addLast("preMessageDecoder", new PreMessageDecoder());
                  p.addLast("requestDecoder", new RequestDecoder());

                  // todo play with this guy, make sure he is in the right place
                  //  come back and play with ChunkedStream
                  //  our messages are small for now
                  //                  p.addLast(new ChunkedWriteHandler());

                  // https://netty.io/4.1/api/io/netty/channel/ChannelPipeline.html
                  // we can specify and executor group to run the handler in a different thread pool
                  // so we do not block I/O

                  p.addLast("businessLogicHandler", new BusinessLogicHandler(connection));
                }
              });

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(8080).sync();

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel().closeFuture().sync();
    } finally {
      if (ForkJoinPool.commonPool().awaitQuiescence(10L, TimeUnit.SECONDS)) {
        LOG.info("ForkJoinPool.commonPool stopped all tasks");
      } else {
        LOG.warn("not able to complete all tasks");
      }

      connection.close();
      childGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }

    LOG.info("exit");
  }
}
