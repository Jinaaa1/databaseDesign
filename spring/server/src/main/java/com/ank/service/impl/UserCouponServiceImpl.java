package com.ank.service.impl;

import com.ank.mapper.UserCouponMapper;
import com.ank.pojo.UserCoupon;
import com.ank.service.UserCouponService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {
    @Override
    public boolean useCoupon(Integer account, Integer couponType) {
        UpdateWrapper<UserCoupon> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", account)
                .eq("coupon_type", couponType)
                .gt("quantity", 0)
                .setSql("quantity = quantity - 1");
        return update(updateWrapper);
    }
}
