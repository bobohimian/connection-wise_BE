package com.conwise.service.impl;

import com.conwise.model.Node;
import com.conwise.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.List;

@Slf4j
@Service
public class AiServiceImpl implements AiService {
    private final ChatClient chatClient;

    private final String listStringConverter;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem("你是一个学习专家,能够帮助用户扩展知识面").build();
        BeanOutputConverter<List<String>> listBeanOutputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<String>>() {
                }
        );
        this.listStringConverter = listBeanOutputConverter.toString();
    }

    @Override
    public String call(String userInput) {
        String call = this.chatClient.prompt()
                .user(userInput)
                .call()
                .content();
        return call;
    }

    @Override
    public Flux<ServerSentEvent<String>> associate(String userInput) {
        String prompt = String.format("""
                现在需要基于一段信息,做知识扩展.请你以指定格式返回扩展的方向,方向只需要提供简单的描述,指出4个方向即可
                
                例如:
                输入:React主要用于构建UI。可以在React里传递多种类型的参数，如声明代码，帮助开发者渲染出UI、也可以是静态的HTML DOM元素、也可以传递动态变量、甚至是可交互的应用组件。
                输出:["React 组件开发","props 传递与类型检查","React 钩子","虚拟 DOM 原理"]
                
                已知
                知识:%s
                格式:%s
                """, userInput, listStringConverter);

        Flux<ServerSentEvent<String>> stringFlux = callFluxStream(prompt);
        return stringFlux;
    }

    @Override
    public Flux<ServerSentEvent<String>> generate(String userInput, String associateDirection) {
        String prompt = userInput;
        if (associateDirection == null)
            prompt = String.format("""
                    %s
                    基于方向"%s"帮助我扩展知识
                    """, userInput, associateDirection);
        Flux<ServerSentEvent<String>> stringFlux = callFluxStream(prompt);
        return stringFlux;
    }

    private Flux<ServerSentEvent<String>> callFluxStream(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .onErrorResume(e -> Flux.just("error"))
                .map(chunk -> ServerSentEvent.<String>builder()
                        .event("push")
                        .data(chunk)
                        .build())
                .concatWith(Mono.just(ServerSentEvent.<String>builder()
                        .event("close")
                        .data("close")
                        .build()))
                // 资源释放逻辑
                .doOnCancel(() -> {
                    log.info("客户端主动断开连接,释放资源");
                })
                .doFinally(signalType -> {
                    if (signalType.equals(SignalType.CANCEL)) {
                        log.info("连接已经终止(客户端主动关闭或网络中断)");
                    } else if (signalType.equals(SignalType.ON_COMPLETE)) {
                        log.info("数据流正常结束,资源已释放");
                    }
                });
    }
}
