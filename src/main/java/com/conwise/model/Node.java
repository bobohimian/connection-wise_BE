package com.conwise.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Node {
    private String id;
    private String type;
    private Position position;
    private Object data;
    private String className;
    private Object style;
    private ArrayList<Float> origin;
}

