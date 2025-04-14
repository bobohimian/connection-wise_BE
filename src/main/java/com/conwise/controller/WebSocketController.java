package com.conwise.controller;

import com.conwise.model.Canvas;
import com.conwise.service.CanvasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebSocketController {
    private final CanvasService canvasService;

    @Autowired
    public WebSocketController(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    @MessageMapping("/sendMessage") // 处理来自客户端的消息
//    @SendTo("/topic/messages") // 广播到所有订阅者
        public String sendMessage(String message) {
        // 处理消息，进行广播
        return message; // 这里直接返回接收到的消息
    }

    @MessageMapping("/init")
    public ResponseEntity initialize() {
        // Fetch graph data from the database
        try {
            Canvas canvas = canvasService.getCanvasById(1L);
            if (canvas != null) {
                return ResponseEntity.ok(canvas);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

//    @MessageMapping("/update-edge")
//    public void updateEdge(UpdateRequest request) {
//        // 处理边的更新逻辑
//        // 您可以调用服务层进行更新
//    }
//
//    @MessageMapping("/update-node")
//    public void updateNode(UpdateRequest request) {
//        // 处理节点的更新逻辑
//        // 您可以调用服务层进行更新
//    }
}