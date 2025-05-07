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

    public Canvas getCanvasById(int id);

    public List<Canvas> getCanvasesByUserId(int userId);

    public boolean createCanvas(int userId);

    public boolean updateCanvas(Canvas canvas) ;

    public boolean deleteCanvas(int id);
    
    public boolean addNode(int canvasId,String nodeId,String node) ;

    public boolean deleteNode(int canvasId,String nodeId);

    public boolean updateNodeAttribute(int canvasId,String nodeId, List<String> pathList, String newValue);

    public boolean addEdge(int canvasId,String edgeId,String edge);

    public boolean deleteEdge(int canvasId,String edgeId) ;

    public boolean updateEdgeAttribute(int canvasId,String edgeId, List<String> pathList, String newValue) ;
}