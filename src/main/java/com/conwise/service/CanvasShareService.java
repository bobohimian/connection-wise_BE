package com.conwise.service;

import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;

import java.util.List;

public interface CanvasShareService {
    boolean shareCanvas(CanvasShare canvasShare);

    boolean deleteShare(CanvasShare canvasShare);

    boolean updateShare(CanvasShare canvasShare);

    List<Canvas> getSharedCanvas(int userId);
}
