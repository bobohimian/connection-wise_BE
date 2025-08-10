package com.conwise.model;

import lombok.Data;

import java.util.List;

@Data
public class Operation {
    private String id;
    private List<String> path;
    private String value;
    private Integer version;
}
