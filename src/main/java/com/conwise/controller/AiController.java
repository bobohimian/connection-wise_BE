package com.conwise.controller;

import com.conwise.model.Node;
import com.conwise.service.AiService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
//@CrossOrigin("*")
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService, ChatClient.Builder chatClientBuilder) {
        this.aiService = aiService;
    }


    @GetMapping(value = "/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generate(@RequestParam(value = "prompt") String userInput,
                                                  @RequestParam(value = "direction", required = false) String associateDirection) {
        return aiService.generate(userInput, associateDirection);
    }

    @GetMapping(value = "/associate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> associate(@RequestParam("prompt") String userInput) {
        return aiService.associate(userInput);
    }

}
