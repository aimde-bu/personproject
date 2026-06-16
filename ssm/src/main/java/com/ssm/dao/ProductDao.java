package com.ssm.dao;

import com.ssm.domain.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductDao {

    @Select("SELECT * FROM product limit #{pageNum},#{pageSize}")
    List<Product> page(int pageNum, int pageSize);

    @Update("UPDATE product SET name=#{name},category=#{category},price=#{price},commission=#{commission},stock=#{stock},sales=#{sales}," +
            "imageUrl=#{imageUrl},status=#{status} WHERE id=#{id}")
    int update(Product product);

    @Delete("DELETE FROM product WHERE id=#{id}")
    int delete(int id);

    @Insert("INSERT INTO product(name,category,price,commission,stock,sales,imageUrl,status) VALUES(#{name},#{category},#{price},#{commission},#{stock},#{sales},#{imageUrl},#{status})")
    int add(Product product);

    @Select("SELECT * FROM product")
    List<Product> getAll();
}
