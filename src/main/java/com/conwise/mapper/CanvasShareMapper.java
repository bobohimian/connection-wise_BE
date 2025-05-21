package com.conwise.mapper;

import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface CanvasShareMapper {
    
    // 插入一条分享记录
    int insert(CanvasShare canvasShare);
    
    // 根据ID删除分享记录
    int deleteById(Integer id);
    
    // 根据画布ID和用户ID删除分享记录
    int deleteByCanvasIdAndUserId(@Param("canvasId") Integer canvasId, @Param("userId") Integer userId);
    
    // 根据画布ID删除所有分享记录
    int deleteByCanvasId(Integer canvasId);
    
    // 更新分享记录
    int update(CanvasShare canvasShare);
    
    // 根据ID查询分享记录
    CanvasShare selectById(Integer id);
    
    // 根据画布ID和用户ID查询分享记录
    CanvasShare selectByCanvasIdAndUserId(@Param("canvasId") Integer canvasId, @Param("userId") Integer userId);
    
    // 根据画布ID查询所有分享记录
    List<CanvasShare> selectByCanvasId(Integer canvasId);

    // 根据用户ID查询所有分享给该用户的画布
    List<Canvas> selectCanvasesByUserId(Integer userId);

    // 根据用户ID查询所有分享给该用户的记录
    List<CanvasShare> selectByUserId(Integer userId);
    
    // 查询所有分享记录
    List<CanvasShare> selectAll();
}