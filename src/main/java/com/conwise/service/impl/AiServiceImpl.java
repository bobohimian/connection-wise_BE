package com.conwise.service.impl;

import com.conwise.model.RelationshipGraph;
import com.conwise.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.List;

@Slf4j
@Service
public class AiServiceImpl implements AiService {
    private final ChatClient chatClient;

    private final String listStringConverter;

    private final String relationShipGraphConverter;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem("你是一个学习专家,能够帮助用户扩展知识面").build();
        BeanOutputConverter<List<String>> listBeanOutputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<String>>() {
                }
        );
        this.listStringConverter = listBeanOutputConverter.getFormat();
        BeanOutputConverter<RelationshipGraph> relationshipGraphBeanOutputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<RelationshipGraph>() {
        });
        this.relationShipGraphConverter = relationshipGraphBeanOutputConverter.getFormat();
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
                现在需要基于一段信息,做知识扩展.请你以指定格式返回扩展的方向,方向只需要提供简单的描述,指出4个方向即可.只返回纯 JSON 格式，不包含任何额外的符号、反引号、代码块标记或文本说明
                
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
    public Flux<ServerSentEvent<String>> generateGraph(String userInput) {
        String prompt = String.format("""
                请帮我解决问题，要求返回一个结构化的关系图数据。输出需严格按照 JSON 格式，返回准确且完整的节点和关系信息。每个节点需包含唯一的 ID、名称和简介，关系需包含起始节点 ID、目标节点 ID 和具体的关联描述。
                
                     示例输入:请帮我生成《白鹿原》中的人物简介和人物关系
                     输出格式示例：
                     {
                       "nodes": [
                         {"id": 1, "title": "白嘉轩", "content": "白鹿村族长，腰杆笔挺的仁义代表"},
                         {"id": 2, "title": "鹿子霖", "content": "乡约，精于算计的鹿家掌门人"},
                         {"id": 3, "title": "田小娥", "content": "反抗封建礼教的悲剧女性"},
                         {"id": 4, "title": "黑娃", "content": "鹿三长子，从长工到土匪最终觉醒"},
                         {"id": 5, "title": "白孝文", "content": "白家长子，经历堕落与重生的复杂人物"},
                         {"id": 6, "title": "鹿兆鹏", "content": "鹿家长子，坚定的革命领导者"},
                         {"id": 7, "title": "鹿兆海", "content": "鹿家次子，国民党军官，白灵的初恋"},
                         {"id": 8, "title": "朱先生", "content": "关中大儒，白嘉轩姐夫"},
                         {"id": 9, "title": "冷秋月", "content": "鹿兆鹏原配，封建婚姻牺牲品"},
                         {"id": 10, "title": "白灵", "content": "白嘉轩幼女，投身革命的新女性"},
                         {"id": 11, "title": "鹿三", "content": "白家长工，黑娃之父"},
                         {"id": 12, "title": "白赵氏", "content": "白嘉轩之母，封建家长代表"},
                         {"id": 13, "title": "岳维山", "content": "国民党县书记，反派代表"},
                         {"id": 14, "title": "韩裁缝", "content": "地下共产党员，革命引路人"}
                       ],
                       "relationships": [
                         {"source": 1, "target": 2, "relation": "世代仇怨"},
                         {"source": 4, "target": 3, "relation": "夫妻关系"},
                         {"source": 3, "target": 2, "relation": "肉体交易"},
                         {"source": 11, "target": 3, "relation": "杀害"},
                         {"source": 5, "target": 3, "relation": "畸形恋情"},
                         {"source": 6, "target": 10, "relation": "革命同志"},
                         {"source": 7, "target": 10, "relation": "初恋情人"},
                         {"source": 8, "target": 1, "relation": "精神导师"},
                         {"source": 6, "target": 9, "relation": "名义夫妻"},
                         {"source": 14, "target": 6, "relation": "革命引路"},
                         {"source": 13, "target": 6, "relation": "政治敌对"},
                         {"source": 12, "target": 10, "relation": "祖孙冲突"},
                         {"source": 4, "target": 6, "relation": "童年伙伴"},
                         {"source": 5, "target": 2, "relation": "权力博弈"}
                       ]
                     }
                
                     要求：
                     1. 确保节点 ID 是唯一的，从 1 开始递增。
                     2. 每个节点的 "content" 字段需提供简洁且准确的介绍.
                     3. 关系数据需全面且逻辑正确，确保 "source" 和 "target" 字段使用正确的节点 ID，"relation" 字段清晰描述两者之间的关系。
                     4. 返回的数据需尽量完整
                
                     输入的作品名称：%s
                     返回数据格式: %s
                     请根据上述要求和格式生成关系图数据。
                """, userInput, relationShipGraphConverter);
        Flux<ServerSentEvent<String>> stringFlux = callFluxStream(prompt);
        return stringFlux;
    }

    public String generateGraphStr(String userInput) {
        String prompt = String.format("""
                请帮我解决问题，要求返回一个结构化的关系图数据。输出需严格按照 JSON 格式,只返回纯 JSON 格式，不包含任何额外的符号、反引号、代码块标记或文本说明
                
                     示例输入:请帮我生成《白鹿原》中的人物简介和人物关系
                     输出格式示例：
                     {
                       "nodes": [
                         {"id": 1, "title": "白嘉轩", "content": "白鹿村族长，腰杆笔挺的仁义代表"},
                         {"id": 2, "title": "鹿子霖", "content": "乡约，精于算计的鹿家掌门人"},
                         {"id": 3, "title": "田小娥", "content": "反抗封建礼教的悲剧女性"},
                         {"id": 4, "title": "黑娃", "content": "鹿三长子，从长工到土匪最终觉醒"},
                         {"id": 5, "title": "白孝文", "content": "白家长子，经历堕落与重生的复杂人物"},
                         {"id": 6, "title": "鹿兆鹏", "content": "鹿家长子，坚定的革命领导者"},
                         {"id": 7, "title": "鹿兆海", "content": "鹿家次子，国民党军官，白灵的初恋"},
                         {"id": 8, "title": "朱先生", "content": "关中大儒，白嘉轩姐夫"},
                         {"id": 9, "title": "冷秋月", "content": "鹿兆鹏原配，封建婚姻牺牲品"},
                         {"id": 10, "title": "白灵", "content": "白嘉轩幼女，投身革命的新女性"},
                         {"id": 11, "title": "鹿三", "content": "白家长工，黑娃之父"},
                         {"id": 12, "title": "白赵氏", "content": "白嘉轩之母，封建家长代表"},
                         {"id": 13, "title": "岳维山", "content": "国民党县书记，反派代表"},
                         {"id": 14, "title": "韩裁缝", "content": "地下共产党员，革命引路人"}
                       ],
                       "edges": [
                         {"source": 1, "target": 2, "relation": "世代仇怨"},
                         {"source": 4, "target": 3, "relation": "夫妻关系"},
                         {"source": 3, "target": 2, "relation": "肉体交易"},
                         {"source": 11, "target": 3, "relation": "杀害"},
                         {"source": 5, "target": 3, "relation": "畸形恋情"},
                         {"source": 6, "target": 10, "relation": "革命同志"},
                         {"source": 7, "target": 10, "relation": "初恋情人"},
                         {"source": 8, "target": 1, "relation": "精神导师"},
                         {"source": 6, "target": 9, "relation": "名义夫妻"},
                         {"source": 14, "target": 6, "relation": "革命引路"},
                         {"source": 13, "target": 6, "relation": "政治敌对"},
                         {"source": 12, "target": 10, "relation": "祖孙冲突"},
                         {"source": 4, "target": 6, "relation": "童年伙伴"},
                         {"source": 5, "target": 2, "relation": "权力博弈"}
                       ]
                     }
                
                     要求：
                     1. 确保节点 ID 是唯一的，从 1 开始递增。
                     2. 每个节点的 "content" 字段需提供简洁且准确的介绍.
                     3. 关系数据需全面且逻辑正确，确保 "source" 和 "target" 字段使用正确的节点 ID，"relation" 字段清晰描述两者之间的关系。
                     4. 返回的数据需尽量完整
                
                     输入的作品名称：%s
                     返回数据格式: %s
                     请根据上述要求和格式生成关系图数据。
                """, userInput, relationShipGraphConverter);
        String result = chatClient.prompt()
                .user(prompt)
                .call().content();
        System.out.println(result);
        return result;
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
