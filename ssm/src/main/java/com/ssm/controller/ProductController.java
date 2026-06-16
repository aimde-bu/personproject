package com.ssm.controller;

import com.ssm.dao.ProductDao;
import com.ssm.domain.Product;
import com.ssm.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @GetMapping("/all")
    public Result getAll() {
        List<Product> products= productDao.getAll();
        System.out.println(products);
        return new Result(200, "查询成功", products);
    }

    @PutMapping
    public Result update(@RequestBody Product product) {
        int i = productDao.update(product);
        if (i > 0) {
            return new Result(200, "修改成功", null);
        } else {
            return new Result(500, "修改失败", null);
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id) {
        int i = productDao.delete(id);
        if (i > 0) {
            return new Result(200, "删除成功", null);
        } else {
            return new Result(500, "删除失败", null);
        }
    }

    @PostMapping
    public Result add(@RequestBody Product product) {
        int i = productDao.add(product);
        if (i > 0) {
            return new Result(200, "添加成功", null);
        } else {
            return new Result(500, "添加失败", null);
        }
    }
}
