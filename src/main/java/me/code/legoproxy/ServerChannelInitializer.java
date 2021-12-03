package me.code.legoproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel channel;

    public ServerChannelInitializer(Channel channel) {
        this.channel = channel;
    }

    //Make this callback and skip create a separate handler class
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline()
                .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
                        ByteBuf copy = Unpooled.copiedBuffer(byteBuf);
                        channel.writeAndFlush(copy);
                        System.out.println("In initChannel: byte" + byteBuf + " copy " + copy);
                    }
                });
    }
}

