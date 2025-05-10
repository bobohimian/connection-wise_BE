package com.conwise.service;

import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.model.User;

import java.util.List;

public interface CanvasShareService {
    boolean shareCanvas(CanvasShare canvasShare);

    boolean deleteShare(int canvasShare);

    boolean updateShare(CanvasShare canvasShare);

    List<Canvas> getCanvasShareByUserId(int userId);

    List<User> getSharedUsersByCanvasId(int canvasId);
}
