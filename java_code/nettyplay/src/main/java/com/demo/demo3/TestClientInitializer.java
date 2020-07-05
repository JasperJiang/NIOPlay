package com.demo.demo3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class TestClientInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        /**
         * 1) lengthFieldOffset  //长度字段的偏差
         * 2) lengthFieldLength  //长度字段占的字节数
         * 3) lengthAdjustment  //添加到长度字段的补偿值
         * 4) initialBytesToStrip  //从解码帧中第一次去除的字节数
         */
        //ChannelInboundHandlerAdapter
        pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
        //ChannelOutboundHandlerAdapter
        pipeline.addLast(new LineEncoder());
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));  //将byte数据解码成String
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));  //将字符串编码成byte数据
        pipeline.addLast(new TestClientHandler());
    }
}
