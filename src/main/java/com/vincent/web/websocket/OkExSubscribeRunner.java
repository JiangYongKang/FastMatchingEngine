package com.vincent.web.websocket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public class OkExSubscribeRunner implements CommandLineRunner {

    @Resource
    private OkExWebSocketListener webSocketListener;

    @Override
    public void run(String... args) throws Exception {
        Proxy localProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080));
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(localProxy)
                .retryOnConnectionFailure(true)
                .build();
        Request request = new Request.Builder()
                .url("wss://ws.okex.com:8443/ws/v5/public")
                .build();
        client.dispatcher().cancelAll();
        client.newWebSocket(request, webSocketListener);
    }

}
