package com.ank.service;

import com.ank.pojo.Redeem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RedeemService extends IService<Redeem> {
    List<Redeem> getUserRedeem(Integer account);
}
