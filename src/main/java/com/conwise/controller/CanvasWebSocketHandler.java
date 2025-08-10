package com.conwise.controller;

import com.conwise.model.Edge;
import com.conwise.model.Node;
import com.conwise.model.Operation;
import com.conwise.model.RequestMessage;
import com.conwise.service.CanvasService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
@Slf4j
public class CanvasWebSocketHandler extends TextWebSocketHandler {
    private final CanvasService canvasService;
    private final ObjectMapper mapper;
    private final Map<Integer, List<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();

    // 心跳机制参数
    private static final long HEARTBEAT_INTERVAL = 30000;
    private static final long HEARTBEAT_TIMEOUT = 70000;

    @Autowired
    public CanvasWebSocketHandler(CanvasService canvasService, ObjectMapper mapper) {
        this.canvasService = canvasService;
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer canvasId = (int) session.getAttributes().get("canvasId");
        if (canvasId == null) {
            log.error("canvasId is null");
            closeSession(session);
        }
        sessionMap.computeIfAbsent(canvasId, k -> new CopyOnWriteArrayList<>()).add(session);
        log.info("客户端连接,canvasId:{},当前连接数量:{}", canvasId, sessionMap.get(canvasId).size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("收到消息");
        System.out.println(message);

        try {
            RequestMessage req = mapper.readValue(message.getPayload(), RequestMessage.class);

            // 处理心跳
            if ("ping".equals(req.getType())) {
                sendPong(session);
                return;
            }

            // 数据修改处理
            processBusinessMessage(session, req, message);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processBusinessMessage(WebSocketSession session, RequestMessage req, TextMessage message) {
        Integer canvasId = (Integer) session.getAttributes().get("canvasId");
        if (canvasId == null)
            return;
        Operation operation;
        try {
            log.info(mapper.writeValueAsString(req));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            switch (req.getType()) {
                case "addNode":
                    operation = req.getOperation();
                    canvasService.addNode(canvasId, operation.getId(), operation.getValue());
                    break;
                case "deleteNode":
                    operation = req.getOperation();
                    canvasService.deleteNodeById(canvasId, operation.getId());
                    break;
                case "updateNode":
                    operation = req.getOperation();
                    boolean nodeUpdated = canvasService.updateNodeAttribute(canvasId, operation.getId(), operation.getPath(), operation.getValue(),operation.getVersion());
                    if (!nodeUpdated) {
                        Node node = canvasService.getNode(canvasId, operation.getId());
                        Operation newOperation = new Operation();
                        newOperation.setId(operation.getId());
                        newOperation.setValue(mapper.writeValueAsString(node));
                        RequestMessage newReq = new RequestMessage();
                        newReq.setType("flushNode");
                        newReq.setOperation(newOperation);
                        TextMessage newText = new TextMessage(mapper.writeValueAsString(newReq));
                        session.sendMessage(newText);
                    }
                    break;
                case "addEdge":
                    operation = req.getOperation();
                    canvasService.addEdge(canvasId, operation.getId(), operation.getValue());
                    break;
                case "deleteEdge":
                    operation = req.getOperation();
                    canvasService.deleteEdgebyId(canvasId, operation.getId());
                    break;
                case "updateEdge":
                    operation = req.getOperation();
                    boolean edgeUpdated = canvasService.updateEdgeAttribute(canvasId, operation.getId(), operation.getPath(), operation.getValue(),operation.getVersion());
                    if (!edgeUpdated) {
                        Edge edge = canvasService.getEdge(canvasId, operation.getId());
                        Operation newOperation = new Operation();
                        newOperation.setId(operation.getId());
                        newOperation.setValue(mapper.writeValueAsString(edge));
                        RequestMessage newReq = new RequestMessage();
                        newReq.setType("flushEdge");
                        newReq.setOperation(newOperation);
                        TextMessage newText = new TextMessage(mapper.writeValueAsString(newReq));
                        session.sendMessage(newText);
                    }
                    break;
                default:
            }
            this.broadcast(session, message);
        } catch (Exception e) {
            try {
                throw e;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
//            log.error("业务逻辑执行异常，canvasId：{}，类型：{},消息:{}", canvasId, req.getType(), message);
        }
    }

    private void sendPong(WebSocketSession session) throws IOException {
        Map<String, Object> pong = new HashMap<>();
        pong.put("type", "pong");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(pong)));
    }

    private void broadcast(WebSocketSession session, TextMessage message) {
        Integer canvasId = (Integer) session.getAttributes().get("canvasId");
        List<WebSocketSession> webSocketSessions = sessionMap.get(canvasId);
        webSocketSessions.forEach(webSocketSession -> {
            if (webSocketSession.equals(session) || !session.isOpen())
                return;
            try {
                webSocketSession.sendMessage(message);
            } catch (IOException e) {
                closeSession(session);
                throw new RuntimeException(e);
            }
        });
    }

    private void closeSession(WebSocketSession session) {
        try {
            session.close(CloseStatus.SERVICE_RESTARTED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sessionMap.values().forEach(list -> list.remove(session));
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer canvasId = (Integer) session.getAttributes().get("canvasId");
        closeSession(session);
        log.info("连接关闭，canvasId: {}, 剩余连接数：{}", canvasId, sessionMap.get(canvasId).size());
    }

    // 定时清理超时Session（可选）
    @Scheduled(fixedRate = 10000)
    public void cleanupIdleSessions() {
        sessionMap.forEach((canvasId, sessions) -> {
            sessions.removeIf(session -> {
                if (!session.isOpen()) {
                    log.info("清理超时连接");
                    return true;
                }
                return false;
            });
        });
    }
}