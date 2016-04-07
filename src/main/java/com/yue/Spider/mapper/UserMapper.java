package com.yue.Spider.mapper;

import org.apache.ibatis.annotations.*;


/**
 * Created by andrew on 16/1/31.
 */
public interface UserMapper {

    @Delete("delete from user where id=#{id}")
    public void deleteUser(int id);
}
