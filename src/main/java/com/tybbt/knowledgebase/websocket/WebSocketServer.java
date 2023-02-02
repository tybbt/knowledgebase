package com.tybbt.knowledgebase.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    private String token = "";

    // 用于放置所有链接
    private static HashMap<String, Session> map = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        map.put(token, session);
        this.token = token;
        LOG.info("【WebSocket】 有新连接 token {}， session id：{}，当前连接数： {}；", token, session.getId(), map.size());
    }

    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        LOG.info("【WebSocket】 关闭了一项连接 token {}， session id：{}，当前连接数： {}；", token, session.getId(), map.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("【WebSocket】 收到消息 token {}， message：{}；", token, message );
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("【WebSocket】 发生错误", error);
    }

    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("【WebSocket】 推送消息失败：token {}, message: {}；", token, message);
            }
            LOG.info("【WebSocket】 推送消息：token {}, message: {}；", token, message);
        }
    }

}
