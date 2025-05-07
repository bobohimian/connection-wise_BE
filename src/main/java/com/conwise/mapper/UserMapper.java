package com.conwise.mapper;

import com.conwise.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    
    User findById(Integer id);
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    int insert(User user);
    
    int update(User user);
    
    int deleteById(Integer id);
}