package me.code.legoproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Proxy proxy;

    public ProxyChannelInitializer(Proxy proxy){
        this.proxy = proxy;
    }

    //This creates when the server has connected. Bootstrap is the client part in proxy.
    //this socketChannel is the clients' connection to the proxy "ProxyChannelInitializer"
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        System.out.println("In initChannelProxy: creates ");
        LegoServer server = proxy.getNextServer();
        Bootstrap bootstrap = new Bootstrap();
        System.out.println("In initChannelProxy: " + server + bootstrap);
        Channel channel = bootstrap.group(proxy.getWorkerGroup())
                .channel(NioSocketChannel.class)
                .handler(new ServerChannelInitializer(socketChannel))
                .connect(server.getAddress(), server.getPort()).sync().channel();
        //this socketChannel is the clients' connection to the proxy "ProxyChannelInitial
        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
                ByteBuf copy = Unpooled.copiedBuffer(buf);
                channel.writeAndFlush(copy);
                proxy.gotoNextServer();
                System.out.println(channel + " In initChannelProxy: " + server);
            }
        });
    }
}

