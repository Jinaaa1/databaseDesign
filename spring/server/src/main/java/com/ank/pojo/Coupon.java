package com.ank.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("coupon")
@Data
/**
 * 折扣券实体类
 * */
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Integer type;
    private Double reductionAmount;
    private Double lowestAmount;
    private Integer cost;

}
