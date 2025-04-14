package com.conwise.handler;

import com.conwise.model.Edge;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EdgeListTypeHandler extends BaseTypeHandler<List<Edge>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Edge> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting List<Edge> to JSON string", e);
        }
    }

    @Override
    public List<Edge> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseEdgeList(rs.getString(columnName));
    }

    @Override
    public List<Edge> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseEdgeList(rs.getString(columnIndex));
    }

    @Override
    public List<Edge> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseEdgeList(cs.getString(columnIndex));
    }

    private List<Edge> parseEdgeList(String json) {
        if (json == null) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<Edge>>() {});
        } catch (JsonProcessingException e) {
            // 记录错误并返回空列表
            System.err.println("Error parsing JSON to List<Edge>: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}