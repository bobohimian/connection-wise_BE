package com.conwise.model;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class Canvas {
    private int id;
    private int userId;
    private String userName;
    private String title;
    private String description;
    private boolean isPublic;
    private String thumbnailUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<Node> nodes;
    private List<Edge> edges;
    private Object settings; // 或使用特定的设置类
}

