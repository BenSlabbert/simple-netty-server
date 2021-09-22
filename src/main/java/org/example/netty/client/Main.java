package org.example.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;
import org.example.netty.client.handler.inbound.ClientHandler;
import org.example.netty.client.handler.inbound.ResponseDecoder;
import org.example.netty.client.handler.outbound.RequestEncoder;
import org.example.netty.common.MyThreadFactory;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class);

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

              // todo play with this guy, make sure he is in the right place
              //  come back and play with ChunkedStream
              //  our messages are small for now
              //                  p.addLast(new ChunkedWriteHandler());
              p.addLast(new ClientHandler());
            }
          });

      // Start the client.
      ChannelFuture f = b.connect(host, port).sync();

      // Wait until the connection is closed.
      f.channel().closeFuture().sync();
    } finally {
      LOG.info("shutting down worker group");
      workerGroup.shutdownGracefully();
    }

    LOG.info("exit");
  }
}
