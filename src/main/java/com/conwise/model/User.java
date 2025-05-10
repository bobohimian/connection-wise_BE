package com.conwise.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int shareId;
    private int id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String permission;
}
