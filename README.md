# Simple Netty Server

Playing with netty as a server. Playing around with the concepts and NIO programming.

https://learning.oreilly.com/library/view/netty-in-action/9781617291470/kindle_split_015.html#ch06table03
lists the lifecycle methods of interface ChannelInboundHandler.

https://learning.oreilly.com/library/view/netty-in-action/9781617291470/kindle_split_015.html#ch06table04
shows the methods defined locally by ChannelOutboundHandler

## Todo

1. swap JSON for protobuf
2. add compression for funzies. Update the protocol to include an extra byte which will describe 8 flags (each bit is a feature), one of those will be compression.
3. add tls for funzies
