package org.example.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.concurrent.ThreadFactory;
import org.apache.log4j.Logger;
import org.example.netty.client.handler.inbound.ClientHandler;
import org.example.netty.client.handler.inbound.ResponseDecoder;
import org.example.netty.client.handler.outbound.RequestEncoder;

public class Main {

  private static final Logger logger = Logger.getLogger(Main.class);

  record MyThreadFactory(String prefix) implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r);
      thread.setName(prefix + "-" + thread.getId());
      return thread;
    }
  }

  public static void main(String[] args) throws Exception {
    String host = "localhost";
    int port = 8080;
    var workerGroup = new EpollEventLoopGroup(2, new MyThreadFactory("worker"));

    try {
      Bootstrap b = new Bootstrap();
      b.group(workerGroup);
      b.channel(EpollSocketChannel.class);
      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.handler(
          new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              var p = ch.pipeline();
              p.addLast(new RequestEncoder());
              p.addLast(new ResponseDecoder());
              p.addLast(new ChunkedWriteHandler());
              p.addLast(new ClientHandler());
            }
          });

      // Start the client.
      ChannelFuture f = b.connect(host, port).sync();

      // Wait until the connection is closed.
      f.channel().closeFuture().sync();
    } finally {
      logger.info("shutting down worker group");
      workerGroup.shutdownGracefully();
    }
  }
}
