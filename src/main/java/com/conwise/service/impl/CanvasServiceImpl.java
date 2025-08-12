package com.conwise.service.impl;

import com.conwise.controller.CanvasWebSocketHandler;
import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.CanvasShareMapper;
import com.conwise.mapper.UserMapper;
import com.conwise.model.*;
import com.conwise.service.CanvasService;
import com.conwise.service.MinioService;
import com.conwise.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Service
@Slf4j
public class CanvasServiceImpl implements CanvasService {

    private final MinioService minioService;
    private final CanvasMapper canvasMapper;
    private final UserMapper userMapper;
    private final VersionService versionService;
    private final CanvasShareMapper canvasShareMapper;
    @Value("${resource.static.images}")
    private String DEFAULT_THUMBNAIL_PATH;

    @Autowired
    public CanvasServiceImpl(MinioService minioService, CanvasMapper canvasMapper, UserMapper userMapper, VersionService versionService, CanvasShareMapper canvasShareMapper) {
        this.minioService = minioService;
        this.canvasMapper = canvasMapper;
        this.userMapper = userMapper;
        this.versionService = versionService;
        this.canvasShareMapper = canvasShareMapper;
    }
    public ApiResponse<Canvas> getCanvasById(int id, int userId) {
        Canvas canvas = canvasMapper.findById(id);
        if (canvas == null) {
            return ApiResponse.fail(ResponseCode.CANVAS_NOT_FOUND);
        }
        if (canvas.getUserId() == userId) {
            canvas.setPermission("owner");
            return ApiResponse.ok(canvas);
        }
        CanvasShare canvasShare = canvasShareMapper.selectByCanvasIdAndUserId(id, userId);
        if (canvasShare == null || canvasShare.getPermission().isEmpty()) {
            return ApiResponse.fail(ResponseCode.CANVAS_PERMISSION_DENIED);
        }
        canvas.setPermission(canvasShare.getPermission());
        return ApiResponse.ok(canvas);

    }

    public ApiResponse<List<Canvas>> getCanvasesByUserId(int userId) {
        List<Canvas> byUserId = canvasMapper.findByUserId(userId);
        byUserId.forEach(canvas -> canvas.setPermission("owner"));
        return ApiResponse.ok(byUserId);
    }

    public ApiResponse<Void> createCanvas(int userId) {
        User user = userMapper.findById(userId);
        Canvas canvas = new Canvas();
        canvas.setUserId(userId);
        canvas.setUserName(user.getUsername());
        int insert = canvasMapper.insert(canvas);
        this.insertDefaultData(canvas.getId());
        if (insert != 1) {
            return ApiResponse.fail(ResponseCode.CANVAS_CREATE_FAILED);
        }
        String thumbnailFileName = "thumbnail_canvas_" + canvas.getId() + ".webp";
        minioService.uploadFileSystemFileAsync(DEFAULT_THUMBNAIL_PATH, thumbnailFileName)
                .thenAccept(result -> log.info("缩略图[{}] 上传成功", thumbnailFileName))
                .exceptionally(throwable -> {
                    log.error("缩略图[{}] 上传失败{}", thumbnailFileName, throwable.getMessage());
                    return null;
                });
        return ApiResponse.ok();
    }

    private void insertDefaultData(int canvasId) {
        String nodeId1 = UUID.randomUUID().toString();
        String nodeId2 = UUID.randomUUID().toString();
        String edgeId = UUID.randomUUID().toString();
        String defaultNode1 = String.format("{\"id\": \"%s\", \"data\": {\"text\": \"这是一个示例节点，你可以在其中编辑文本，拖拽节点和连接不同的节点。\", \"theme\": \"bg-linear-to-r from-green-300 via-emerald-300 to-teal-300\"}, \"type\": \"textNode\", \"version\": 1, \"position\": {\"x\": -10.5, \"y\": -150.5}}", nodeId1);
        String defaultNode2 = String.format(" {\"id\": \"%s\", \"data\": {\"text\": \"这是一个示例节点，你可以在其中编辑文本，拖拽节点和连接不同的节点。\", \"theme\": \"bg-linear-to-r from-purple-300 via-indigo-300 to-blue-300\"}, \"type\": \"textNode\", \"version\": 1, \"position\": {\"x\": 300, \"y\": 60}}", nodeId2);
        String defaultEdge = String.format("{\"id\": \"%s\", \"data\": {\"label\": \"[关系]\"}, \"type\": \"curvedEdge\", \"source\": \"%s\", \"target\": \"%s\", \"version\": 1, \"animated\": true, \"sourceHandle\": null, \"targetHandle\": null}", edgeId, nodeId1, nodeId2);
        this.addNode(canvasId, nodeId1, defaultNode1);
        this.addNode(canvasId, nodeId2, defaultNode2);
        this.addEdge(canvasId, edgeId, defaultEdge);
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

        String thumbnailFileName = "thumbnail_canvas_" + id + ".webp";
        minioService.deleteFileAsync(thumbnailFileName)
                .thenAccept(result -> log.info("缩略图[{}] 删除成功", thumbnailFileName))
                .exceptionally(throwable -> {
                    log.error("缩略图[{}] 删除失败 {}", thumbnailFileName, throwable.getMessage());
                    return null;
                });
        return ApiResponse.ok();
    }


    @Override
    public ApiResponse<Void> saveThumbnail(int canvasId, MultipartFile thumbnail) {
        String thumbnailFileName = "thumbnail_canvas_" + canvasId + ".webp";
        minioService.uploadFileAsync(thumbnail, thumbnailFileName)
                .thenAccept(fileName -> log.info("缩略图[{}] 更新成功", fileName))
                .exceptionally(throwable -> {
                    log.error("缩略图[{}] 更新失败{}", thumbnailFileName, throwable.getMessage());
                    return null;
                });
        return ApiResponse.ok();
    }

    public boolean addNode(int canvasId, String nodeId, String node) {
        int inserted = canvasMapper.insertCanvasNode(canvasId, node);
        return inserted > 0;
    }

    @Override
    public Node getNode(int canvasId, String nodeId) {
        Node node = canvasMapper.getNode(canvasId, nodeId);
        return node;
    }

    public boolean deleteNodeById(int canvasId, String nodeId) {
        int deleted = canvasMapper.deleteCanvasNode(canvasId, nodeId);
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
    public boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue, Integer version) {
        boolean isUpdated = versionService.tryUpdateNodeVersion(canvasId, nodeId, version);
        if (!isUpdated) {
            return false;
        }
        String path = PostgresPathHelper.formatPath(pathList);
        List<String> versionList = new ArrayList<>();
        versionList.add("version");
        String versionPath = PostgresPathHelper.formatPath(versionList);
        int rowsUpdated = canvasMapper.updateCanvasNodeAttributeWithNodeId(canvasId, nodeId, path, newValue, versionPath, version + 1);
        return rowsUpdated > 0;
    }


    public boolean addEdge(int canvasId, String edgeId, String edge) {
        int inserted = canvasMapper.insertCanvasEdge(canvasId, edge);
        return inserted > 0;
    }

    @Override
    public Edge getEdge(int canvasId, String edgeId) {
        Edge edge = canvasMapper.getEdge(canvasId, edgeId);
        return edge;
    }

    public boolean deleteEdgebyId(int canvasId, String edgeId) {
        int deleted = canvasMapper.deleteCanvasEdge(canvasId, edgeId);
        return deleted > 0;
    }

    /**
     * @param canvasId 画布ID
     * @param edgeId   边的id
     * @param pathList 修改的边json的属性路径
     * @param newValue 新的 JSON 值 (以字符串形式传递)
     * @return 更新是否成功
     */
    public boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue, Integer version) {
        boolean isUpdated = versionService.tryUpdateEdgeVersion(canvasId, edgeId, version);
        if (!isUpdated) {
            return false;
        }
        List<String> versionList = new ArrayList<>();
        versionList.add("version");
        String versionPath = PostgresPathHelper.formatPath(versionList);
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasEdgeAttributeWithEdgeId(canvasId, edgeId, path, newValue, versionPath, version + 1);
        return rowsUpdated > 0;
    }
}