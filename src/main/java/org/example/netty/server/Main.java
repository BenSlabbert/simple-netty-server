package org.example.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.concurrent.ThreadFactory;
import org.example.netty.server.handler.inbound.BusinessLogicHandler;
import org.example.netty.server.handler.inbound.PreBusinessLogicHandler;
import org.example.netty.server.handler.outbound.EncoderHandler;
import org.example.netty.server.handler.outbound.PreEncoderHandler;

public class Main {

  record MyThreadFactory(String prefix) implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r);
      thread.setName(prefix + "-" + thread.getId());
      return thread;
    }
  }

  public static void main(String[] args) throws Exception {

    var bossGroup = new NioEventLoopGroup(1, new MyThreadFactory("boss"));
    var workerGroup = new NioEventLoopGroup(2, new MyThreadFactory("worker"));

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                  // https://netty.io/4.1/api/io/netty/channel/ChannelPipeline.html
                  // we can specify and executor group to run the handler in a different thread pool
                  // so we do not block I/O
                  var p = ch.pipeline();
                  // outbound handlers after inbound handlers
                  p.addLast(new EncoderHandler());
                  p.addLast(new PreEncoderHandler());
                  p.addLast(new PreBusinessLogicHandler());
                  p.addLast(new ChunkedWriteHandler());
                  p.addLast(new BusinessLogicHandler());
                }
              });

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(8080).sync();

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
