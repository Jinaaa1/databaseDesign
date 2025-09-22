package com.ank.service;

import com.ank.pojo.DTO.OrderDTO;
import com.ank.pojo.UserOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UserOrderService extends IService<UserOrder> {
    double REWARD_POINTS_RETURN_RATE = 0.1;

    Map<String, Object> checkout(Integer account, OrderDTO orderDTO);

    List<UserOrder> listBetweenDate(LocalDate startDate, LocalDate endDate);

    List<UserOrder> userListBetweenDate(Integer account, LocalDate startDate, LocalDate endDate);

    List<UserOrder> userGetTotalAmount(Integer account);
}
