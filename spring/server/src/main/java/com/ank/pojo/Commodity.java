package com.ank.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@TableName("commodity")
/**
 * 商品实体类
 * */
public class Commodity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Double price;
    private Double rating;
    private String image;
    private String description;
    private String category;
}
