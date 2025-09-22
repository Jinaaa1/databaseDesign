package com.ank.service.impl;

import com.ank.mapper.CommodityMapper;
import com.ank.pojo.Commodity;
import com.ank.service.CommodityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {

    @Override
    public double getPrice(int commodityId) {
        QueryWrapper<Commodity> commodityQueryWrapper = new QueryWrapper<>();
        commodityQueryWrapper.eq("id", commodityId)
                .select("price");
        return (double) getMap(commodityQueryWrapper).get("price");
    }
}
