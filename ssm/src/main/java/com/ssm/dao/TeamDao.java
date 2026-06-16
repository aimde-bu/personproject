package com.ssm.dao;

import com.ssm.domain.Distributor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamDao {

    @Select("SELECT * FROM distributor")
    public List<Distributor> getAll();
}
