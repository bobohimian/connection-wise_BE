package com.conwise.mapper;

import com.conwise.model.Canvas;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CanvasMapper {

    // 根据ID获取画布
    Canvas findById(@Param("id") int id);

    // 根据ID列表获取画布
    List<Canvas> findByIdIn(@Param("ids") List<Integer> ids);

    // 根据用户ID获取画布
    List<Canvas> findByUserId(@Param("userId") int userId);

    // 插入新画布
    int insert(Canvas canvas);

    // 更新画布
    int update(Canvas canvas);

    // 删除画布
    int deleteById(@Param("id") int id);

    // 路径修改节点属性
    int updateCanvasNodeAttribute(
            @Param("canvasId") int canvasId,
            @Param("path") String path,
            @Param("newValue") String newValue
    );

    // 修改指定id的节点属性
    int updateCanvasNodeAttributeWithNodeId(
            @Param("canvasId") int canvasId,
            @Param("nodeId") String nodeId,
            @Param("path") String Path,
            @Param("newValue") String newValue
    );

    // 添加节点
    int insertCanvasNode(
            @Param("canvasId") int canvasId,
            @Param("node") String node
    );

    // 删除节点
    int deleteCanvasNode(
            @Param("canvasId") int canvasId,
            @Param("nodeId") String nodeId
    );

    // 路径修改边属性
    int updateCanvasEdgeAttribute(
            @Param("canvasId") int canvasId,
            @Param("path") String path,
            @Param("newValue") String value);

    // 修改指定id的边属性
    int updateCanvasEdgeAttributeWithEdgeId(
            @Param("canvasId") int canvasId,
            @Param("edgeId") String edgeId,
            @Param("path") String path,
            @Param("newValue") String value
    );

    // 添加边
    int insertCanvasEdge(
            @Param("canvasId") int canvasId,
            @Param("edge") String edge
    );

    // 删除边
    int deleteCanvasEdge(
            @Param("canvasId") int canvasId,
            @Param("edgeId") String edgeId
    );
}