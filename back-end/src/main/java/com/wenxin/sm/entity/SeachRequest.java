package com.wenxin.sm.entity;


import lombok.Data;

@Data
public class SeachRequest {

    private String title;

    private Integer startPrice;

    private Integer endPrice;

    private String orderByAsc;

    private String orderByDesc;

    private String type;

}
