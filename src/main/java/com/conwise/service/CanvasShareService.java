package com.conwise.service;

import com.conwise.model.ApiResponse;
import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.model.User;

import java.util.List;

public interface CanvasShareService {
    ApiResponse<Void> shareCanvas(CanvasShare canvasShare);

    ApiResponse<Void> deleteShare(int canvasShare);

    ApiResponse<Void> updateShare(CanvasShare canvasShare);

    ApiResponse<List<Canvas>> getCanvasShareByUserId(int userId);

    ApiResponse<List<User>> getSharedUsersByCanvasId(int canvasId);
}
