package com.ank.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("redeem")
/**
 * 兑换折扣券实体类
 * */
public class Redeem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer value;
    private Integer couponType;
    private Integer number;
    private Integer userId;
    private LocalDateTime time;
}
