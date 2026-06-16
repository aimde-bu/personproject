package com.ssm.dao;

import com.ssm.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("SELECT * FROM t_user WHERE username=#{username} AND password=#{password}")
    public User login(String username, String password);
}
