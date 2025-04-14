package com.conwise.handler;

import com.conwise.model.Canvas;
import com.conwise.model.JsonUpdateOperation;
import com.conwise.model.RequestMessage;
import com.conwise.model.ResponseMessage;
import com.conwise.service.CanvasService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final CanvasService canvasService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    public MyWebSocketHandler(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Canvas canvas = canvasService.getCanvasById(1L);
        ResponseMessage message = new ResponseMessage("init", canvas);
        String response = mapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(response));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        RequestMessage myMessage = mapper.readValue(message.getPayload(), RequestMessage.class);
        JsonUpdateOperation operation;
        System.out.println("type:" + myMessage.getType());
        switch (myMessage.getType()) {
            case "get-canvas":
                operation = myMessage.getOperation();
                Canvas canvas = canvasService.getCanvasById((long) operation.getCanvasId());
                ResponseMessage responseMessage = new ResponseMessage("canvasData", canvas);
                String response = mapper.writeValueAsString(responseMessage);
                session.sendMessage(new TextMessage(response));
            case "add-node":
                operation = myMessage.getOperation();
                canvasService.addNode(operation.getCanvasId(), operation.getValue());
                System.out.println(operation.getValue());
                break;
            case "delete-node":
                operation = myMessage.getOperation();
                canvasService.deleteNode(operation.getCanvasId(), operation.getJsonObjId());
                System.out.println(operation.getJsonObjId());
                break;
            case "update-node":
                operation = myMessage.getOperation();
                boolean nodeUpdated = canvasService.updateNodeAttribute(operation.getCanvasId(), operation.getJsonObjId(), operation.getPath(), operation.getValue());
                System.out.println(myMessage.getOperation());
                break;
            case "add-edge":
                operation = myMessage.getOperation();
                canvasService.addEdge(operation.getCanvasId(), operation.getValue());
                System.out.println(operation.getValue());
                break;
            case "delete-edge":
                operation = myMessage.getOperation();
                canvasService.deleteEdge(operation.getCanvasId(), operation.getJsonObjId());
                System.out.println(operation.getJsonObjId());
                break;
            case "update-edge":
                operation = myMessage.getOperation();
                boolean edgeUpdated = canvasService.updateEdgeAttribute(operation.getCanvasId(), operation.getJsonObjId(), operation.getPath(), operation.getValue());
                System.out.println(myMessage.getOperation());
                break;
            default:
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 连接关闭后的处理逻辑
    }
}