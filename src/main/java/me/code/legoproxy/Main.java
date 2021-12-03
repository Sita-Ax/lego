package me.code.legoproxy;

public class Main {
    public static void main(String[] args) {
        Proxy proxy = new Proxy(6000);
        proxy.load();
        proxy.start();//This is the proxy port and the client will also connect to 6000 to get a connection
    }
}
