package com.ank.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("administrator")
@Data
/**
 * 管理员实体类
 * */
public class Administrator {
    @TableId(type = IdType.AUTO)
    private String account;
    private String password;
}