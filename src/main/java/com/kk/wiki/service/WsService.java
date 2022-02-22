package com.kk.wiki.service;

import com.kk.wiki.webSocketServer.WebSocketServer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WsService {

    @Resource
    private WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String info) {
        webSocketServer.sendInfo(info);
    }
}
