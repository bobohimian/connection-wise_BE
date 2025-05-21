package com.conwise.service.impl;

import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.CanvasShareMapper;
import com.conwise.mapper.UserMapper;
import com.conwise.model.*;
import com.conwise.service.CanvasService;
import com.conwise.service.CanvasShareService;
import com.conwise.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanvasShareServiceImpl implements CanvasShareService {
    private final CanvasShareMapper canvasShareMapper;
    private final CanvasMapper canvasMapper;
    private final UserMapper userMapper;

    public CanvasShareServiceImpl(CanvasShareMapper canvasShareMapper, CanvasMapper canvasMapper, UserMapper userMapper) {
        this.canvasShareMapper = canvasShareMapper;
        this.canvasMapper = canvasMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<Void> shareCanvas(CanvasShare canvasShare) {
        String userName = canvasShare.getUserName();
        User byUsername = userMapper.findByUsername(userName);
        if (byUsername == null) {
            return ApiResponse.fail(ResponseCode.CANVAS_SHARE_USER_NOT_FOUNT);
        }
        canvasShare.setUserId(byUsername.getId());
        int insert = canvasShareMapper.insert(canvasShare);
        return insert == 1 ? ApiResponse.ok() : ApiResponse.fail(ResponseCode.CANVAS_SHARE_FAILED);
    }

    @Override
    public ApiResponse<Void> deleteShare(int shareId) {
        int deleted = canvasShareMapper.deleteById(shareId);
        return deleted == 1 ? ApiResponse.ok() : ApiResponse.fail(ResponseCode.CANVAS_SHARE_FAILED);
    }

    @Override
    public ApiResponse<Void> updateShare(CanvasShare canvasShare) {
        int updated = canvasShareMapper.update(canvasShare);
        return updated == 1 ? ApiResponse.ok() : ApiResponse.fail(ResponseCode.CANVAS_SHARE_MODIFY_PERMISSION_FAILED);
    }

    @Override
    public ApiResponse<List<Canvas>> getCanvasShareByUserId(int userId) {
        List<Canvas> canvasList = canvasShareMapper.selectCanvasesByUserId(userId);
        return ApiResponse.ok(canvasList);
    }

    @Override
    public ApiResponse<List<User>> getSharedUsersByCanvasId(int canvasId) {
        List<User> userList = userMapper.findShareUsersByCanvasId(canvasId);
        return ApiResponse.ok(userList);

    }
}
