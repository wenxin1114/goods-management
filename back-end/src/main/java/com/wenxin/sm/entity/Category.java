package com.wenxin.sm.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;


@Data
@Table("category")
public class Category {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    private String name;
}
