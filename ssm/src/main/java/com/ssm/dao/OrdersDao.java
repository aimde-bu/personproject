package com.ssm.dao;

import com.ssm.domain.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersDao {
    @Select("SELECT * FROM orders order by totalAmount desc limit #{pageNum},#{pageSize}")
    public List<Orders> page(int pageNum, int pageSize);

    @Update("UPDATE orders SET productID=#{productID},distributorID=#{distributorID},orderNum=#{orderNum},totalAmount=#{totalAmount},totalcommission=#{totalcommission} WHERE id= #{id}")
    int update(Orders orders);

    @Delete("DELETE FROM orders WHERE id=#{id}")
    int delete(int id);

    @Insert("INSERT INTO orders(orderNumber,productID,distributorID,orderNum,totalAmount,totalcommission,status) VALUES(#{orderNumber},#{productID},#{distributorID},#{orderNum},#{totalAmount},#{totalcommission},#{status})")
    int add(Orders orders);

    @Select("SELECT * FROM orders")
    List<Orders> getAll();
}
