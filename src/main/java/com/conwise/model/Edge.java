package com.conwise.model;

import lombok.Data;

@Data
public class Edge {
    private String id;
    private String source;
    private String target;
    private String sourceHandle;
    private String targetHandle;
    private String type;
    private Object data;
    private boolean animated;
    private Object style;
}
