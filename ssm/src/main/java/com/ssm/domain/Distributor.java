package com.ssm.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Distributor {
    private long id;
    private String realName;
    private String phone;
    private String level;
    private long parentId;
    private long totalSales;
    private long totalCommission;
    private String status;
    private Date createdTime;
    private List<Distributor> children;
}
