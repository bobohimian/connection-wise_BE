package com.conwise.service.impl;

import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import com.conwise.service.CanvasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CanvasServiceImpl implements CanvasService {

    private final CanvasMapper canvasMapper;

    private Map<String, String> nodeIndexMap = new LinkedHashMap<>();
    private Map<String, String> edgeindexMap = new LinkedHashMap<>();

    @Autowired
    public CanvasServiceImpl(CanvasMapper canvasMapper) {
        this.canvasMapper = canvasMapper;
    }

    private void updateMap(){
        Canvas canvas = canvasMapper.findById(1L);
        List<Node> nodes = canvas.getNodes();
        for (int index = 0; index < nodes.size(); index++) {
            nodeIndexMap.put(nodes.get(index).getId(), String.valueOf(index));
        }
        List<Edge> edges = canvas.getEdges();
        for (int index = 0; index < edges.size(); index++)
            edgeindexMap.put(edges.get(index).getId(), String.valueOf(index));
    }
    public List<Canvas> getAllCanvases() {
        return canvasMapper.findAll();
    }

    public Canvas getCanvasById(Long id) {
        Canvas canvas = canvasMapper.findById(id);
        List<Node> nodes = canvas.getNodes();
        for (int index = 0; index < nodes.size(); index++) {
            nodeIndexMap.put(nodes.get(index).getId(), String.valueOf(index));
        }
        List<Edge> edges = canvas.getEdges();
        for (int index = 0; index < edges.size(); index++)
            edgeindexMap.put(edges.get(index).getId(), String.valueOf(index));
        return canvas;
    }

    public List<Canvas> getCanvasesByUserId(Long userId) {
        return canvasMapper.findByUserId(userId);
    }

    public boolean createCanvas(Canvas canvas) {
        return canvasMapper.insert(canvas) > 0;
    }

    public boolean updateCanvas(Canvas canvas) {
        return canvasMapper.update(canvas) > 0;
    }

    public boolean deleteCanvas(Long id) {
        return canvasMapper.deleteById(id) > 0;
    }

    public boolean addNode(int canvasId,String node) {
        int inserted = canvasMapper.insertCanvasNode(canvasId, node);
        updateMap();
        return inserted>0;
    }

    public boolean deleteNode(int canvasId,String nodeId) {
        int deleted = canvasMapper.deleteCanvasNode(canvasId, nodeId);
        updateMap();
        return deleted>0;
    }
    /**
     * 更新画布中某个节点的属性
     *
     * @param canvasId 画布的 ID
     * @param pathList 修改的路径
     * @param newValue 新的 JSON 值 (以字符串形式传递)
     * @return 更新是否成功
     */
    public boolean updateNodeAttribute(int canvasId,String nodeId, List<String> pathList, String newValue) {
        String nodeIndex = nodeIndexMap.get(nodeId);
        pathList.addFirst(nodeIndex);
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasNodeAttribute(canvasId, path, newValue);
        return rowsUpdated > 0;
    }




    public boolean addEdge(int canvasId,String edge) {
        int inserted = canvasMapper.insertCanvasEdge(canvasId, edge);
        updateMap();
        return inserted>0;
    }
    public boolean deleteEdge(int canvasId,String edgeId) {
        int deleted = canvasMapper.deleteCanvasEdge(canvasId, edgeId);
        updateMap();
        return deleted>0;
    }
    /**
     *
     * @param canvasId 画布ID
     * @param edgeId 边的id
     * @param pathList 修改的边json的属性路径
     * @param newValue 新的 JSON 值 (以字符串形式传递)
     * @return 更新是否成功
     */
    public boolean updateEdgeAttribute(int canvasId,String edgeId, List<String> pathList, String newValue) {
        String edgeIndex = edgeindexMap.get(edgeId);
        pathList.addFirst(edgeIndex);
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasEdgeAttribute(canvasId, path, newValue);
        return rowsUpdated > 0;
    }
}