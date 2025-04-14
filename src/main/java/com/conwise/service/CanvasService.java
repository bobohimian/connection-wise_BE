package com.conwise.service;

import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface CanvasService {

    public List<Canvas> getAllCanvases();

    public Canvas getCanvasById(Long id);

    public List<Canvas> getCanvasesByUserId(Long userId);

    public boolean createCanvas(Canvas canvas);

    public boolean updateCanvas(Canvas canvas) ;

    public boolean deleteCanvas(Long id);
    
    public boolean addNode(int canvasId,String node) ;

    public boolean deleteNode(int canvasId,String nodeId);

    public boolean updateNodeAttribute(int canvasId,String nodeId, List<String> pathList, String newValue);

    public boolean addEdge(int canvasId,String edge);

    public boolean deleteEdge(int canvasId,String edgeId) ;

    public boolean updateEdgeAttribute(int canvasId,String edgeId, List<String> pathList, String newValue) ;
}