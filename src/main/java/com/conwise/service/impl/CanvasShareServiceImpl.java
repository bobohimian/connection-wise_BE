package com.conwise.service.impl;

import com.conwise.mapper.CanvasMapper;
import com.conwise.mapper.CanvasShareMapper;
import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.service.CanvasService;
import com.conwise.service.CanvasShareService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanvasShareServiceImpl implements CanvasShareService {
    private final CanvasShareMapper canvasShareMapper;
    private final CanvasMapper canvasMapper;

    public CanvasShareServiceImpl(CanvasShareMapper canvasShareMapper, CanvasMapper canvasMapper, CanvasService canvasService) {
        this.canvasShareMapper = canvasShareMapper;
        this.canvasMapper = canvasMapper;
    }

    @Override
    public boolean shareCanvas(CanvasShare canvasShare) {
        canvasShare.setPermission("edit");
        int insert = canvasShareMapper.insert(canvasShare);
        return insert > 0;
    }

    @Override
    public boolean deleteShare(CanvasShare canvasShare) {
        int deleted = canvasShareMapper.deleteByCanvasIdAndUserId(canvasShare.getId(), canvasShare.getUserId());
        return deleted > 0;
    }

    @Override
    public boolean updateShare(CanvasShare canvasShare) {
        int updated = canvasShareMapper.update(canvasShare);
        return updated > 0;
    }

    @Override
    public List<Canvas> getSharedCanvas(int userId) {
        List<CanvasShare> canvasShares = canvasShareMapper.selectByUserId(userId);
        List<Integer> canvasIds = canvasShares.stream()
                .map(CanvasShare::getCanvasId)
                .toList();
        List<Canvas> canvasList = canvasMapper.findByIdIn(canvasIds);
        if(canvasList.isEmpty()) {
            return new ArrayList<>();
        }
        return canvasList;
    }
}
