package com.ank.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("membership_level")
@Data
/**
 * 会员等级实体类
 * */
public class MembershipLevel {
    @TableId(type = IdType.AUTO)
    private Integer type;
    private String name;
    private Double requiredAmount;
    private Double discount;
}
