package com.ank.service.impl;

import com.ank.mapper.OrderCommodityMapper;
import com.ank.pojo.OrderCommodity;
import com.ank.service.OrderCommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderCommodityServiceImpl extends ServiceImpl<OrderCommodityMapper, OrderCommodity> implements OrderCommodityService {
}
