package com.conwise.service;

import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.model.ApiResponse;
import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface CanvasService {

    ApiResponse<Canvas> getCanvasById(int id);

    ApiResponse<List<Canvas>> getCanvasesByUserId(int userId);

    ApiResponse<Void> createCanvas(int userId);

    ApiResponse<Void> updateCanvas(Canvas canvas);

    ApiResponse<Void> deleteCanvas(int id);

    ApiResponse<Void> saveThumbnail(int canvasId, MultipartFile thumbnail);

    boolean addNode(int canvasId, String nodeId, String node);

    boolean deleteNode(int canvasId, String nodeId);

    boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue);

    boolean addEdge(int canvasId, String edgeId, String edge);

    boolean deleteEdge(int canvasId, String edgeId);

    boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue);

}