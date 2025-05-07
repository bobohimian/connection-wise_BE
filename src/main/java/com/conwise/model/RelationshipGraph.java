package com.conwise.model;

import lombok.Data;

import java.util.List;

// 代表整个关系图数据结构的主类
@Data
public class RelationshipGraph {
    private List<GraphNode> nodes; // 节点列表，包含ID、标题和内容
    private List<GraphEdge> edges; // 关系列表
}

class GraphNode {
    private int id; // 节点唯一标识符
    private String title; // 节点名称或标题
    private String content; // 节点描述内容
}

// 代表关系的类
@Data
class GraphEdge {
    private int source; // 关系起始节点的ID
    private int target; // 关系目标节点的ID
    private String relation; // 关系描述
}