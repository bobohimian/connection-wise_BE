package com.conwise.service.impl;

import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.UserMapper;
import com.conwise.model.*;
import com.conwise.service.CanvasService;
import com.conwise.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class CanvasServiceImpl implements CanvasService {

    private final MinioService minioService;
    private final CanvasMapper canvasMapper;
    private final UserMapper userMapper;
    private final Map<Integer, LinkedHashMap<String, Integer>> nodeIndexMap = new HashMap<>();
    private final Map<Integer, LinkedHashMap<String, Integer>> edgeindexMap = new HashMap<>();

    @Value("${resource.images}")
    private String DEFAULT_THUMBNAIL_PATH;

    @Autowired
    public CanvasServiceImpl(MinioService minioService, CanvasMapper canvasMapper, UserMapper userMapper) {
        this.minioService = minioService;
        this.canvasMapper = canvasMapper;
        this.userMapper = userMapper;
    }

    public ApiResponse<Canvas> getCanvasById(int id) {
        LinkedHashMap<String, Integer> nodeIndexMapOfOneCanvas = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> edgeIndexMapOfOneCanvas = new LinkedHashMap<>();
        Canvas canvas = canvasMapper.findById(id);
        if (canvas == null) {
            return ApiResponse.fail(ResponseCode.CANVAS_NOT_FOUND);
        }
        List<Node> nodes = canvas.getNodes();
        for (int index = 0; index < nodes.size(); index++) {
            nodeIndexMapOfOneCanvas.put(nodes.get(index).getId(), index);
        }
        List<Edge> edges = canvas.getEdges();
        for (int index = 0; index < edges.size(); index++)
            edgeIndexMapOfOneCanvas.put(edges.get(index).getId(), index);
        nodeIndexMap.put(id, nodeIndexMapOfOneCanvas);
        edgeindexMap.put(id, edgeIndexMapOfOneCanvas);
        return ApiResponse.ok(canvas);
    }

    public ApiResponse<List<Canvas>> getCanvasesByUserId(int userId) {
        List<Canvas> byUserId = canvasMapper.findByUserId(userId);
        return ApiResponse.ok(byUserId);
    }

    public ApiResponse<Void> createCanvas(int userId) {
        User user = userMapper.findById(userId);
        Canvas canvas = new Canvas();
        canvas.setUserId(userId);
        canvas.setUserName(user.getUsername());
        int insert = canvasMapper.insert(canvas);
        if (insert != 1) {
            return ApiResponse.fail(ResponseCode.CANVAS_CREATE_FAILED);
        }
        String thumbnailFileName = "thumbnail_canvas_" + canvas.getId() + ".png";
        minioService.uploadClassPathFileAsync(DEFAULT_THUMBNAIL_PATH, thumbnailFileName)
                .thenAccept(result -> {
                    System.out.println(String.format("缩略图[%s] 上传成功", thumbnailFileName));
                })
                .exceptionally(throwable -> {
                    System.err.println(String.format("缩略图[%s] 上传失败" + throwable.getMessage(), thumbnailFileName));
                    return null;
                });
        return ApiResponse.ok();
    }

    public ApiResponse<Void> updateCanvas(Canvas canvas) {
        int updated = canvasMapper.update(canvas);
        if (updated != 1) {
            return ApiResponse.fail(ResponseCode.CANVAS_UPDATE_FAILED);
        }
        return ApiResponse.ok();
    }

    public ApiResponse<Void> deleteCanvas(int id) {
        int deleted = canvasMapper.deleteById(id);
        if (deleted != 1) {
            return ApiResponse.fail(ResponseCode.CANVAS_DELETE_FAILED);
        }

        String thumbnailFileName = "thumbnail_canvas_" + id + ".png";
        minioService.deleteFileAsync(thumbnailFileName)
                .thenAccept(result -> {
                    System.out.println(String.format("缩略图[%s] 删除成功", thumbnailFileName));
                })
                .exceptionally(throwable -> {
                    System.err.println(String.format("缩略图[%s] 删除失败" + throwable.getMessage(), thumbnailFileName));
                    return null;
                });
        return ApiResponse.ok();
    }


    @Override
    public ApiResponse<Void> saveThumbnail(int canvasId, MultipartFile thumbnail) {
        String thumbnailFileName = "thumbnail_canvas_" + canvasId + ".png";
        minioService.uploadFileAsync(thumbnail, thumbnailFileName)
                .thenAccept(fileName -> {
                    System.out.println(String.format("缩略图[%s] 更新成功", fileName));
                })
                .exceptionally(throwable -> {
                    System.err.println(String.format("缩略图[%s] 更新失败" + throwable.getMessage(), thumbnailFileName));
                    return null;
                });
        return ApiResponse.ok();
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