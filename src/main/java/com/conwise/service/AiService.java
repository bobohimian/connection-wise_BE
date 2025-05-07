package com.conwise.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface AiService {

    String call(String userInput);

    Flux<ServerSentEvent<String>> generate(String userInput, String associateDirection);

    Flux<ServerSentEvent<String>> associate(String userInput);

    Flux<ServerSentEvent<String>> generateGraph(String userInput);

    String generateGraphStr(String userInput);
}
