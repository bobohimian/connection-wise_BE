package com.conwise.handler;

import com.conwise.model.Node;
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

public class NodeListTypeHandler extends BaseTypeHandler<List<Node>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Node> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting List<Node> to JSON string", e);
        }
    }

    @Override
    public List<Node> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseNodeList(rs.getString(columnName));
    }

    @Override
    public List<Node> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseNodeList(rs.getString(columnIndex));
    }

    @Override
    public List<Node> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseNodeList(cs.getString(columnIndex));
    }

    private List<Node> parseNodeList(String json) {
        if (json == null) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<Node>>() {});
        } catch (JsonProcessingException e) {
            // 记录错误并返回空列表
            System.err.println("Error parsing JSON to List<Node>: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}