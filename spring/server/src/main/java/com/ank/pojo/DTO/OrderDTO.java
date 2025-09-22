package com.ank.pojo.DTO;

import lombok.Data;
import java.util.List;

/**
 * 封装订单的核心数据
 * 前端提交订单时，传递 商品信息 + 优惠券信息
 * 一次只能购买一种物品
 * */

@Data
public class OrderDTO {
    private List<CommodityDTO> commodityDTOList;
    private Integer couponType;
}
