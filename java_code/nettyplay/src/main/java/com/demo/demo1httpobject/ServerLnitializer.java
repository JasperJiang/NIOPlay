package com.demo.demo1httpobject;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerLnitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ServerHandler());
    }
}
