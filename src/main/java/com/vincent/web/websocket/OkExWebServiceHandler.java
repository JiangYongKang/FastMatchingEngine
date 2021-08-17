package com.vincent.web.websocket;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OkExWebServiceHandler {

    /**
     * send request to subscribe the tickers channel
     *
     * @param webSocket
     * @param response
     */
    public void subscribeTickers(WebSocket webSocket, Response response) {
        webSocket.send("{\"op\":\"subscribe\",\"args\":[{\"channel\":\"tickers\",\"instId\":\"BTC-USDT\"}]}");
    }

    public void doReceiveMessage(WebSocket webSocket, String text) {
    }

}
