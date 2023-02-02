package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.websocket.WebSocketServer;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsService {

    @Resource
    private WebSocketServer webSocketServer;


    // 异步化解耦消息发送与点赞功能
    @Async
    public void voteNotification(String message){
        // 推送消息
        webSocketServer.sendInfo(message);
    }
}
