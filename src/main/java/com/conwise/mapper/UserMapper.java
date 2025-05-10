package com.conwise.mapper;

import com.conwise.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    
    User findById(Integer id);

    List<User> findByIds(List<Integer> ids);

    List<User> findShareUsersByCanvasId(Integer id);

    User findByUsername(String username);
    
    User findByEmail(String email);
    
    int insert(User user);
    
    int update(User user);
    
    int deleteById(Integer id);
}