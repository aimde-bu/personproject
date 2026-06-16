package com.ssm.controller;

import com.ssm.dao.OrdersDao;
import com.ssm.domain.Orders;
import com.ssm.domain.Product;
import com.ssm.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersDao ordersDao;

    @GetMapping("/all")
    public Result getAll() {
        List<Orders> orders= ordersDao.getAll();
        System.out.println(orders);
        return new Result(200, "查询成功", orders);
    }

    @PutMapping
    public Result update(@RequestBody Orders orders) {
        System.out.println(orders);
        int i = ordersDao.update(orders);
        if (i > 0) {
            return new Result(200, "修改成功", null);
        } else {
            return new Result(500, "修改失败", null);
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id) {
        int i = ordersDao.delete(id);
        if (i > 0) {
            return new Result(200, "删除成功", null);
        } else {
            return new Result(500, "删除失败", null);
        }
    }

    @PostMapping
    public Result add(@RequestBody Orders orders) {
        int i = ordersDao.add(orders);
        if (i > 0) {
            return new Result(200, "添加成功", null);
        } else {
            return new Result(500, "添加失败", null);
        }
    }
}
