package com.conwise.model;

import lombok.Data;

@Data
public class CanvasShare {
    private int id;
    private int canvasId;
    private int userId;
    private String permission;
}
