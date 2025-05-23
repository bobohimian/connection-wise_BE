package com.conwise.controller;

import com.conwise.model.Operation;
import com.conwise.model.RequestMessage;
import com.conwise.service.CanvasService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class CanvasWebSocketHandler extends TextWebSocketHandler {
    private final CanvasService canvasService;
    private final ObjectMapper mapper;
    private final Map<Integer, List<WebSocketSession>> sessionMap = new HashMap<>();

    @Autowired
    public CanvasWebSocketHandler(CanvasService canvasService, ObjectMapper mapper) {
        this.canvasService = canvasService;
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        int canvasId = (int) attributes.get("canvasId");
        sessionMap.computeIfAbsent(canvasId, k -> new ArrayList<>()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        int canvasId = (int) attributes.get("canvasId");
        RequestMessage myMessage = mapper.readValue(message.getPayload(), RequestMessage.class);
        log.info(mapper.writeValueAsString(myMessage));
        Operation operation=null;
        switch (myMessage.getType()) {
            case "addNode":
                operation = myMessage.getOperation();
                canvasService.addNode(canvasId, operation.getId(), operation.getValue());
                break;
            case "deleteNode":
                operation = myMessage.getOperation();
                canvasService.deleteNode(canvasId, operation.getId());
                break;
            case "updateNode":
                operation = myMessage.getOperation();
                boolean nodeUpdated = canvasService.updateNodeAttribute(canvasId, operation.getId(), operation.getPath(), operation.getValue());
                break;
            case "addEdge":
                operation = myMessage.getOperation();
                canvasService.addEdge(canvasId, operation.getId(), operation.getValue());
                break;
            case "deleteEdge":
                operation = myMessage.getOperation();
                canvasService.deleteEdge(canvasId, operation.getId());
                break;
            case "updateEdge":
                operation = myMessage.getOperation();
                boolean edgeUpdated = canvasService.updateEdgeAttribute(canvasId, operation.getId(), operation.getPath(), operation.getValue());
                break;
            default:
        }
        this.broadcast(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭后的处理逻辑
        Map<String, Object> attributes = session.getAttributes();
        int canvasId = (int) attributes.get("canvasId");
        sessionMap.get(canvasId).remove(session);
        log.info("connection closed");
    }

    private void broadcast(WebSocketSession session, TextMessage message) {
        Map<String, Object> attributes = session.getAttributes();
        int canvasId = (int) attributes.get("canvasId");
        List<WebSocketSession> webSocketSessions = sessionMap.get(canvasId);
        webSocketSessions.forEach(webSocketSession -> {
            if (webSocketSession.equals(session))
                return;
            try {
                webSocketSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}