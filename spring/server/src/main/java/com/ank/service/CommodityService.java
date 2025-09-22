package com.ank.service;

import com.ank.pojo.Commodity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommodityService extends IService<Commodity> {

    /**
     *
     * @param commodityId 商品 id
     * @return 商品单价
     */
    double getPrice(int commodityId);
}
