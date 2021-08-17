package com.vincent.web.websocket;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OkExWebSocketListener extends WebSocketListener {

    @Resource
    private OkExWebServiceHandler okExWebServiceHandler;

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        log.info("[OkEx] => Subscribe channel on open");
        okExWebServiceHandler.subscribeTickers(webSocket, response);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        log.info("[OkEx] => Subscribe channel on closed");
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
        log.info("[OkEx] => Subscribe channel on closing");
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        log.info("[OkEx] => Subscribe channel on failure");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        log.info("[OkEx] => Subscribe channel receive text message: {}", text);
        okExWebServiceHandler.doReceiveMessage(webSocket, text);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        log.info("[OkEx] => Subscribe channel on byte message");
    }
}
