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

    public ApiResponse<Canvas> getCanvasById(int id);

    public ApiResponse<List<Canvas>> getCanvasesByUserId(int userId);

    public ApiResponse<Void> createCanvas(int userId);

    public ApiResponse<Void> updateCanvas(Canvas canvas);

    public ApiResponse<Void> deleteCanvas(int id);

    public ApiResponse<Void> saveThumbnail(int canvasId, MultipartFile thumbnail);

    public boolean addNode(int canvasId, String nodeId, String node);

    public boolean deleteNode(int canvasId, String nodeId);

    public boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue);

    public boolean addEdge(int canvasId, String edgeId, String edge);

    public boolean deleteEdge(int canvasId, String edgeId);

    public boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue);

}