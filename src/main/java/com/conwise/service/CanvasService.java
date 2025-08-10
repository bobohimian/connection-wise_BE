package com.conwise.service;

import com.conwise.model.ApiResponse;
import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CanvasService {

    ApiResponse<Canvas> getCanvasById(int id,int userId);

    ApiResponse<List<Canvas>> getCanvasesByUserId(int userId);

    ApiResponse<Void> createCanvas(int userId);

    ApiResponse<Void> updateCanvas(Canvas canvas);

    ApiResponse<Void> deleteCanvas(int id);

    ApiResponse<Void> saveThumbnail(int canvasId, MultipartFile thumbnail);

    boolean addNode(int canvasId, String nodeId, String node);

    Node getNode(int canvasId, String nodeId);

    boolean deleteNodeById(int canvasId, String nodeId);

    boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue,Integer version);

    boolean addEdge(int canvasId, String edgeId, String edge);

    Edge getEdge(int canvasId, String edgeId);

    boolean deleteEdgebyId(int canvasId, String edgeId);

    boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue,Integer version);

}