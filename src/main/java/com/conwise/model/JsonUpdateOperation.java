package com.conwise.model;

import lombok.Data;

import java.util.List;

@Data
public class JsonUpdateOperation {
    private int canvasId;
    private String jsonObjId;
    private List<String> path;
    private String value;
}
