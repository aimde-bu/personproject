package com.ssm.domain;

import lombok.Data;

@Data
public class Product {
    private long id;
    private String name;
    private String category;
    private double price;
    private double commission;
    private long stock;
    private String sales;
    private String imageUrl;
    private String status;
}
