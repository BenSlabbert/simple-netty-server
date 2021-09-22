# Simple Netty Server

Playing with netty as a server. Playing around with the concepts and NIO programming.

todo:

1. define simple protocol
2. encode message to bytes: io.netty.handler.codec.MessageToByteEncoder
3. decode message from bytes: io.netty.handler.codec.ByteToMessageDecoder


https://learning.oreilly.com/library/view/netty-in-action/9781617291470/kindle_split_015.html#ch06table03
lists the lifecycle methods of interface ChannelInboundHandler.

https://learning.oreilly.com/library/view/netty-in-action/9781617291470/kindle_split_015.html#ch06table04
shows all of the methods defined locally by ChannelOutboundHandler
