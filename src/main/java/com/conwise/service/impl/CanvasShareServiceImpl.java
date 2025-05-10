package com.conwise.service.impl;

import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.CanvasShareMapper;
import com.conwise.mapper.UserMapper;
import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.model.User;
import com.conwise.service.CanvasService;
import com.conwise.service.CanvasShareService;
import com.conwise.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public boolean shareCanvas(CanvasShare canvasShare) {
        String userName = canvasShare.getUserName();
        User byUsername = userMapper.findByUsername(userName);
        if (byUsername == null) {
            return false;
        }
        canvasShare.setUserId(byUsername.getId());
        int insert = canvasShareMapper.insert(canvasShare);
        return insert > 0;
    }

    @Override
    public boolean deleteShare(int shareId) {
        int deleted = canvasShareMapper.deleteById(shareId);
        return deleted > 0;
    }

    @Override
    public boolean updateShare(CanvasShare canvasShare) {
        int updated = canvasShareMapper.update(canvasShare);
        return updated > 0;
    }

    @Override
    public List<Canvas> getCanvasShareByUserId(int userId) {
        List<CanvasShare> canvasShares = canvasShareMapper.selectByUserId(userId);
        if(canvasShares.isEmpty())
            return new ArrayList<>();
        List<Integer> canvasIds = canvasShares.stream()
                .map(CanvasShare::getCanvasId)
                .toList();
        List<Canvas> canvasList = canvasMapper.findByIdIn(canvasIds);
        if(canvasList.isEmpty()) {
            return new ArrayList<>();
        }
        return canvasList;
    }

    @Override
    public List<User> getSharedUsersByCanvasId(int canvasId) {
        List<User> userList = userMapper.findShareUsersByCanvasId(canvasId);
//        List<Integer> userIds = canvasShares.stream()
//                .map(CanvasShare::getUserId)
//                .toList();
//        if(canvasShares.isEmpty())
//            return new ArrayList<>();
//        List<User> sharedUserList = userMapper.findByIds(userIds);
//        for (int i = 0; i < userIds.size(); i++) {
//            sharedUserList.get(i).setPermission(canvasShares.get(i).getPermission());
//        }
        return userList;

    }
}
