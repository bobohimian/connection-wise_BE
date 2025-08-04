package com.conwise.service.impl;

import com.conwise.helper.PostgresPathHelper;
import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.UserMapper;
import com.conwise.model.*;
import com.conwise.service.CanvasService;
import com.conwise.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
public class CanvasServiceImpl implements CanvasService {

    private final MinioService minioService;
    private final CanvasMapper canvasMapper;
    private final UserMapper userMapper;

    @Value("${resource.images}")
    private String DEFAULT_THUMBNAIL_PATH;

    @Autowired
    public CanvasServiceImpl(MinioService minioService, CanvasMapper canvasMapper, UserMapper userMapper) {
        this.minioService = minioService;
        this.canvasMapper = canvasMapper;
        this.userMapper = userMapper;
    }

    public ApiResponse<Canvas> getCanvasById(int id) {
        Canvas canvas = canvasMapper.findById(id);
        if (canvas == null) {
            return ApiResponse.fail(ResponseCode.CANVAS_NOT_FOUND);
        }
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
        String thumbnailFileName = "thumbnail_canvas_" + canvas.getId() + ".webp";
        minioService.uploadClassPathFileAsync(DEFAULT_THUMBNAIL_PATH, thumbnailFileName)
                .thenAccept(result -> log.info("缩略图[{}] 上传成功", thumbnailFileName))
                .exceptionally(throwable -> {
                    log.error("缩略图[{}] 上传失败{}", thumbnailFileName, throwable.getMessage());
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
    public boolean updateNodeAttribute(int canvasId, String nodeId, List<String> pathList, String newValue) {
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasNodeAttributeWithNodeId(canvasId,nodeId,path,newValue);
        return rowsUpdated > 0;
    }


    public boolean addEdge(int canvasId, String edgeId, String edge) {
        int inserted = canvasMapper.insertCanvasEdge(canvasId, edge);
        return inserted > 0;
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
    public boolean updateEdgeAttribute(int canvasId, String edgeId, List<String> pathList, String newValue) {
        String path = PostgresPathHelper.formatPath(pathList);
        int rowsUpdated = canvasMapper.updateCanvasEdgeAttributeWithEdgeId(canvasId, edgeId,path, newValue);
        return rowsUpdated > 0;
    }


}