package com.ank.service;

import com.ank.pojo.UserCoupon;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserCouponService extends IService<UserCoupon> {
    boolean useCoupon(Integer account, Integer couponType);
}
