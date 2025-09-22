package com.ank.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
/**
 * 用户实体类
 * */
public class User {
    @TableId(type = IdType.AUTO)
    private Integer account;
    private String password;
    private String name;
    private String phoneNumber;
    private String idNumber;
    private Double balance;
    private Integer points;
    private Integer level;
}
