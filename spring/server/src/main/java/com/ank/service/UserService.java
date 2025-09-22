package com.ank.service;

import com.ank.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Integer> register(User user);

    Map<String, Object> login(Integer account, String password);

    boolean recharge(Integer account, Double amount);

    boolean changePassword(Integer account, String oldPassword, String newPassword);

    boolean redeem(Integer account, Integer couponType, Integer number);

    boolean deductBalance(Integer account, Double amount);

    boolean addPoints(Integer account, Integer points);

    boolean levelUp(Integer account, double totalAmount);
}