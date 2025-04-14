package com.conwise.mapper;

import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CanvasMapper {
    // 获取所有画布
    List<Canvas> findAll();
    
    // 根据ID获取画布
    Canvas findById(@Param("id") Long id);
    
    // 根据用户ID获取画布
    List<Canvas> findByUserId(@Param("userId") Long userId);
    
    // 插入新画布
    int insert(Canvas canvas);
    
    // 更新画布
    int update(Canvas canvas);
    
    // 删除画布
    int deleteById(@Param("id") Long id);

    // 修改节点属性
    int updateCanvasNodeAttribute(
            @Param("canvasId") int canvasId,
            @Param("path") String path,
            @Param("newValue") String newValue
    );

    // 添加节点
    int insertCanvasNode(
            @Param("canvasId")int canvasId,
            @Param("node")String node
            );
    // 删除节点
    int deleteCanvasNode(
            @Param("canvasId") int canvasId,
            @Param("nodeId") String nodeId
            );

    // 修改边属性
    int updateCanvasEdgeAttribute(
            @Param("canvasId") int canvasId,
            @Param("path") String path,
            @Param("newValue") String value);

    // 添加边
    int insertCanvasEdge(
            @Param("canvasId")int canvasId,
            @Param("edge") String edge
    );
    // 删除边
    int deleteCanvasEdge(
            @Param("canvasId") int canvasId,
            @Param("edgeId") String edgeId
    );
}