package com.ank.pojo.DTO;

import lombok.Data;

/**
 * 结算请求的顶层封装
 * 前端提交结算请求时，传递 用户账户 + 订单详情
 * */
@Data
public class CheckoutDTO {
    private Integer account;
    private OrderDTO order;
}

