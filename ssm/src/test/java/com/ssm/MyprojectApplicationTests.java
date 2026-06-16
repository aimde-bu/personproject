package com.ssm;

import com.ssm.domain.Distributor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MyprojectApplicationTests {


    @Test
    void contextLoads() {
        List<Distributor> list = new ArrayList<>();
        Distributor item1 = new Distributor(1, "1", "1", "1", 0, 1, 1, "1", new Date(1), null);
        Distributor item2 = new Distributor(2, "2", "2", "2", 1, 2, 2, "2", new Date(2), null);
        Distributor item3 = new Distributor(3, "3", "3", "3", 2, 3, 3, "3", new Date(3), null);
        Distributor item4 = new Distributor(4, "4", "4", "4", 3, 4, 4, "4", new Date(4), null);
        Distributor item5 = new Distributor(5, "5", "5", "5", 4, 5, 5, "5", new Date(5), null);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        list.add(item5);

        List<Distributor> tree = buildTree(list, 0);
        System.out.println(tree);
    }

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
}

