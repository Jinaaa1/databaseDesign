package com.ank.pojo.DTO;

import lombok.Data;

/**
 * 表示订单中单个商品的信息
 * 前端提交订单时，传递 商品ID + 购买数量 的列表
 * */

@Data
public class CommodityDTO {
    private Integer commodityId; // 商品ID
    private Integer number;      // 商品数量
}
