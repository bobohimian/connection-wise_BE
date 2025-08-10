package com.conwise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {
    private String id;
    private String type;
    private Position position;
    private Object data;
    private String className;
    private Object style;
    private ArrayList<Float> origin;
    private Integer version;
}

