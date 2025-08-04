package com.conwise.service;

import com.conwise.model.ApiResponse;
import com.conwise.model.Canvas;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CanvasService {

    ApiResponse<Canvas> getCanvasById(int id);

    ApiResponse<List<Canvas>> getCanvasesByUserId(int userId);

    ApiResponse<Void> createCanvas(int userId);

    ApiResponse<Void> updateCanvas(Canvas canvas);

    ApiResponse<Void> deleteCanvas(int id);

    ApiResponse<Void> saveThumbnail(int canvasId, MultipartFile thumbnail);

    boolean addNode(int canvasId, String nodeId, String node);

    boolean deleteNodeById(int canvasId, String nodeId);

    boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue);

    boolean addEdge(int canvasId, String edgeId, String edge);

    boolean deleteEdgebyId(int canvasId, String edgeId);

    boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue);

}