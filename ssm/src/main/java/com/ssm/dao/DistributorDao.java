package com.ssm.dao;

import com.ssm.domain.Distributor;
import com.ssm.domain.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DistributorDao {



    @Select("SELECT * FROM distributor limit #{pageNum},#{pageSize}")
    public List<Distributor> page(int pageNum, int pageSize);

    @Update("UPDATE distributor SET realName=#{realName},phone=#{phone},status=#{status},parentId=#{parentId} WHERE id=#{id}")
    int update(Distributor distributor);

    @Delete("DELETE FROM distributor WHERE id=#{id}")
    int delete(int id);

    @Select("SELECT * FROM distributor")
    List<Distributor> getAll();

    @Insert("INSERT INTO distributor(realName,phone,parentId,status) VALUES(#{realName},#{phone},#{parentId},#{status})")
    int add(Distributor distributor);
}
