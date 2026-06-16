package com.ssm.controller;

import com.ssm.dao.DistributorDao;
import com.ssm.domain.Distributor;
import com.ssm.domain.Orders;
import com.ssm.domain.Product;
import com.ssm.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/distributor")
public class DistributorController {
    @Autowired
    private DistributorDao distributorDao;

    @GetMapping("/all")
    public Result getAll() {
        List<Distributor> distributors= distributorDao.getAll();
        System.out.println(distributors);
        return new Result(200, "查询成功", distributors);
    }

    @PutMapping
    public Result update(@RequestBody Distributor distributor) {
        int i = distributorDao.update(distributor);
        if (i > 0) {
            return new Result(200, "修改成功", null);
        } else {
            return new Result(500, "修改失败", null);
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id) {
        int i = distributorDao.delete(id);
        System.out.println(i);
        if (i > 0) {
            return new Result(200, "删除成功", null);
        } else {
            return new Result(500, "删除失败", null);
        }
    }

    @PostMapping
    public Result add(@RequestBody Distributor distributor) {
        System.out.println(distributor);
        int i = distributorDao.add(distributor);
        if (i > 0) {
            return new Result(200, "添加成功", null);
        } else {
            return new Result(500, "添加失败", null);
        }
    }
}
