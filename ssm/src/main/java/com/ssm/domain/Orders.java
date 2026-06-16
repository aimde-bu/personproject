package com.ssm.domain;
import lombok.Data;
import java.sql.Date;

@Data
public class Orders {
    private long id;
    private String orderNumber;
    private String productID;
    private String distributorID;
    private long orderNum;
    private long totalAmount;
    private long totalcommission;
    private String status;
    private Date createdTime;
}
