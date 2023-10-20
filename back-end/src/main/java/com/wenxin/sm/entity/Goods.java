package com.wenxin.sm.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 商品
 */
@Data
@ToString
@Table("goods")
public class Goods extends BaseEntity {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    @NotBlank(message = "商品名称不能为空")
    private String name;
    // 进价
    private BigDecimal purchasePrice;
    // 售价
    @NotBlank(message = "售价不得为空")
    private BigDecimal sellingPrice;
    // 最低售价，起拍价，可为空
    private BigDecimal upsetPrice;
    // 商品图片，存放地址，可为空
    private String cover;
    // 商品类型【饮料，生活用品，酒，食品等】
    private String category;
}
