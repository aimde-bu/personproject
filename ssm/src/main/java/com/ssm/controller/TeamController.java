package com.ssm.controller;

import com.ssm.dao.TeamDao;
import com.ssm.domain.Distributor;
import com.ssm.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TeamController {
    @Autowired
    private TeamDao teamDao;

    private List<Distributor> buildTree(List<Distributor> allNodes, long parentId) {
        List<Distributor> children = new ArrayList<>();
        for (Distributor node : allNodes) {
            if (node.getParentId() == parentId) {
                node.setChildren(buildTree(allNodes, node.getId()));
                children.add(node);
            }
        }
        return children;
    }

    @GetMapping("/Teams")
    public Result getAll() {
        List<Distributor> team= teamDao.getAll();
        System.out.println(team);
        List<Distributor> tree = buildTree(team, 0);
        System.out.println(tree);
        return new Result(200, "查询成功", tree);
    }
}
