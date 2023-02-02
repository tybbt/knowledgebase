package com.tybbt.knowledgebase.service;

import com.tybbt.knowledgebase.websocket.WebSocketServer;
import jakarta.annotation.Resource;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsService {

    @Resource
    private WebSocketServer webSocketServer;


    // 异步化解耦消息发送与点赞功能
    @Async
    public void voteNotification(String message, String logId){
        // 推送消息
        MDC.put("LOG_ID", logId);
        webSocketServer.sendInfo(message);
    }
}
