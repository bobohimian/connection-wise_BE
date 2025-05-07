package com.conwise.controller;

import com.conwise.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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

    @GetMapping(value = "/generate-graph", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generateGraph(@RequestParam("prompt") String userInput) {
        return aiService.generateGraph(userInput);
    }
    @GetMapping(value = "/generate-graph-str", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String generateGraphStr(@RequestParam("prompt") String userInput) {
        return aiService.generateGraphStr(userInput);
    }
}
