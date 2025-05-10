package com.conwise.service.impl;

import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.UserMapper;
import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import com.conwise.model.User;
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
    private final UserMapper userMapper;
    private final Map<Integer, LinkedHashMap<String, Integer>> nodeIndexMap = new HashMap<>();
    private final Map<Integer, LinkedHashMap<String, Integer>> edgeindexMap = new HashMap<>();

    @Autowired
    public CanvasServiceImpl(CanvasMapper canvasMapper, UserMapper userMapper) {
        this.canvasMapper = canvasMapper;
        this.userMapper = userMapper;
    }

    public Canvas getCanvasById(int id) {
        LinkedHashMap<String, Integer> nodeIndexMapOfOneCanvas = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> edgeIndexMapOfOneCanvas = new LinkedHashMap<>();
        Canvas canvas = canvasMapper.findById(id);
        List<Node> nodes = canvas.getNodes();
        for (int index = 0; index < nodes.size(); index++) {
            nodeIndexMapOfOneCanvas.put(nodes.get(index).getId(), index);
        }
        List<Edge> edges = canvas.getEdges();
        for (int index = 0; index < edges.size(); index++)
            edgeIndexMapOfOneCanvas.put(edges.get(index).getId(), index);
        nodeIndexMap.put(id, nodeIndexMapOfOneCanvas);
        edgeindexMap.put(id, edgeIndexMapOfOneCanvas);
        return canvas;
    }

    public List<Canvas> getCanvasesByUserId(int userId) {
        return canvasMapper.findByUserId(userId);
    }

    public boolean createCanvas(int userId) {
        User user = userMapper.findById(userId);
        Canvas canvas = new Canvas();
        canvas.setUserId(userId);
        canvas.setUserName(user.getUsername());
        return canvasMapper.insert(canvas) > 0;
    }

    public boolean updateCanvas(Canvas canvas) {
        return canvasMapper.update(canvas) > 0;
    }

    public boolean deleteCanvas(int id) {
        return canvasMapper.deleteById(id) > 0;
    }

    public boolean addNode(int canvasId, String nodeId, String node) {
        int inserted = canvasMapper.insertCanvasNode(canvasId, node);
        nodeIndexMap.get(canvasId).put(nodeId, nodeIndexMap.get(canvasId).size());
        return inserted > 0;
    }

    public boolean deleteNode(int canvasId, String nodeId) {
        int deleted = canvasMapper.deleteCanvasNode(canvasId, nodeId);
        LinkedHashMap<String, Integer> linkedHashMap = nodeIndexMap.get(canvasId);
        linkedHashMap.remove(nodeId);
        int newIndex = 0;
        for (Map.Entry<String, Integer> entry : linkedHashMap.entrySet()) {
            entry.setValue(newIndex++);
        }
        return deleted > 0;
    }

    /**
     * 更新画布中某个节点的属性
     *
     * @param canvasId 画布的 ID
     * @param pathList 修改的路径
     * @param newValue 新的 JSON 值 (以字符串形式传递)
     * @return 更新是否成功
     */
    public boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue) {
        String nodeIndex = String.valueOf(nodeIndexMap.get(canvasId).get(nodeId));
        pathList.addFirst(nodeIndex);
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasNodeAttribute(canvasId, path, newValue);
        return rowsUpdated > 0;
    }


    public boolean addEdge(int canvasId, String edgeId, String edge) {
        int inserted = canvasMapper.insertCanvasEdge(canvasId, edge);
        edgeindexMap.get(canvasId).put(edgeId, edgeindexMap.get(canvasId).size());
        return inserted > 0;
    }

    public boolean deleteEdge(int canvasId, String edgeId) {
        int deleted = canvasMapper.deleteCanvasEdge(canvasId, edgeId);
        LinkedHashMap<String, Integer> linkedHashMap = edgeindexMap.get(canvasId);
        linkedHashMap.remove(edgeId);
        int newIndex = 0;
        for (Map.Entry<String, Integer> entry : linkedHashMap.entrySet()) {
            entry.setValue(newIndex++);
        }
        return deleted > 0;
    }

    /**
     * @param canvasId 画布ID
     * @param edgeId   边的id
     * @param pathList 修改的边json的属性路径
     * @param newValue 新的 JSON 值 (以字符串形式传递)
     * @return 更新是否成功
     */
    public boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue) {
        String edgeIndex = String.valueOf(edgeindexMap.get(canvasId).get(edgeId));
        pathList.addFirst(edgeIndex);
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasEdgeAttribute(canvasId, path, newValue);
        return rowsUpdated > 0;
    }
}