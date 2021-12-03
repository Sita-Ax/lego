package me.code.legoproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Proxy {

    //The proxy is both client and server in this
    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final List<LegoServer> servers;
    private int roundRobinIndex = 0;

    public Proxy(int port) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.servers = new ArrayList<>();
    }

    public void load() {
        System.out.println("load");
        File file = new File("config.txt");
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            Scanner serverInput = new Scanner(file);
            while (serverInput.hasNextLine()) {
                String line = serverInput.nextLine();
                String[] split = line.split(" ");
                servers.add(new LegoServer(split[0], Integer.parseInt(split[1])));
                System.out.println("Search: file--> " + servers);
            }
            serverInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //start the proxy server and bind it to this.port (fake),
    //That will connect to the real server
    public void start() {
        System.out.println("Proxy ");
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProxyChannelInitializer(this))
                    .bind(this.port).sync().channel().closeFuture().sync();
            System.out.println("Proxy start ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this is the round-robin that has an int to get from 0 to how many servers we got
    public LegoServer getNextServer() {
        System.out.println("Round Robin");
        if (roundRobinIndex >= servers.size())
            roundRobinIndex = 0;
        System.out.println(roundRobinIndex + " RouInd " + servers);
        return servers.get(roundRobinIndex);
    }

    //InitChannel runs twice, so increment has to run separately
    public void gotoNextServer() {
        roundRobinIndex++;
    }

    public EventLoopGroup getWorkerGroup() {
        System.out.println("Proxy Eve: " + workerGroup);
        return workerGroup;
    }
}

