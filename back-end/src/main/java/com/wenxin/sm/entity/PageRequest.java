package com.wenxin.sm.entity;


import lombok.Data;

@Data
public class PageRequest {

    private Integer pageNumber=1;

    private Integer pageSize=10;

    private Integer totalRow;

    private String orderByAsc;

    private String orderByDesc;


}
