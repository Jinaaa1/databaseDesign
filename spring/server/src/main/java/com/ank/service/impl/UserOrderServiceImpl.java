package com.ank.service.impl;

import com.ank.exception.BaseException;
import com.ank.mapper.UserOrderMapper;
import com.ank.pojo.Coupon;
import com.ank.pojo.DTO.CommodityDTO;
import com.ank.pojo.DTO.OrderDTO;
import com.ank.pojo.OrderCommodity;
import com.ank.pojo.User;
import com.ank.pojo.UserOrder;
import com.ank.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements UserOrderService {
    CommodityService commodityService;
    UserService userService;
    MembershipLevelService membershipLevelService;
    CouponService couponService;
    UserCouponService userCouponService;
    OrderCommodityService orderCommodityService;

    @Autowired
    public void setCommodityService(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMembershipLevelService(MembershipLevelService membershipLevelService) {
        this.membershipLevelService = membershipLevelService;
    }

    @Autowired
    public void setUserCouponService(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @Autowired
    public void setOrderCommodityService(OrderCommodityService orderCommodityService) {
        this.orderCommodityService = orderCommodityService;
    }

    @Autowired
    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }


    /**
     * 结账生成订单
     *
     * @param account  会员卡号
     * @param orderDTO 订单详情
     * @return 包含支付价格与赠送积分
     */
    @Override
    @Transactional
    public Map<String, Object> checkout(Integer account, OrderDTO orderDTO) {
        double finalPrice;         // 最终支付价格
        String couponInfo = null;  // 优惠券信息（格式：满减条件/减免金额）
        double price = 0.00D;      // 商品初始总价

        CommodityDTO commodityDTO = orderDTO.getCommodityDTOList().get(0);
        double commodityPrice = commodityService.getPrice(commodityDTO.getCommodityId());
        price = commodityPrice * commodityDTO.getNumber(); // 直接计算总价

        // 计算订单返还的积分
        int points = (int) (price * REWARD_POINTS_RETURN_RATE);
        // 获取用户信息 用户等级对应的折扣
        User user = userService.getById(account);
        double discount = membershipLevelService.getDiscount(user.getLevel());

        if (orderDTO.getCouponType() != null){
            // 若使用折扣券，查询使用的折扣券
            Coupon coupon = couponService.getById(orderDTO.getCouponType());
            if (price > coupon.getLowestAmount()){
                finalPrice = price * discount - coupon.getReductionAmount();
                // 减少会员对应的满减券
                if (!userCouponService.useCoupon(account, coupon.getType())) {
                    throw new BaseException("结算失败，会员不持有该满减券");
                }
                couponInfo = coupon.getLowestAmount() + "/" + coupon.getReductionAmount();
            } else {
                throw new BaseException("结算失败，未能达成使用满减券条件");
            }
        } else {
            finalPrice = price * discount;
        }
        //防止极端情况（如优惠券减免金额过大）导致负价格。
        finalPrice = finalPrice >= 0 ? finalPrice : 0;
        // 减少会员余额
        if (!userService.deductBalance(account, finalPrice)) {
            throw new BaseException("结算失败，会员卡内余额不足");
        }
        // 赠送会员积分
        if (!userService.addPoints(account, points)) {
            throw new BaseException("结算失败，赠送会员积分失败");
        }
        //  order 表插入信息
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(account);
        userOrder.setPaymentAmount(finalPrice);
        userOrder.setRewardPoints(points);
        userOrder.setTime(LocalDateTime.now());
        userOrder.setCouponInfo(couponInfo);
        if (!save(userOrder)) {
            throw new BaseException("结算失败，订单记录存储失败");
        }
        // 插入 order_commodity 表信息
            OrderCommodity orderCommodity = new OrderCommodity();
            orderCommodity.setOrderId(userOrder.getId());
            orderCommodity.setCommodityId(commodityDTO.getCommodityId());
            orderCommodity.setQuantity(commodityDTO.getNumber());
            orderCommodity.setOriginalPrice(price);
            orderCommodity.setActualPrice(finalPrice);

        if (!orderCommodityService.save(orderCommodity)) {
            throw new BaseException("结算失败，订单商品记录存储失败");
        }
        Map<String, Object> returnMap = new HashMap<>(2);
        returnMap.put("finalPrice", finalPrice);
        returnMap.put("points", points);
        return returnMap;
    }

    @Override
    public List<UserOrder> listBetweenDate(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("time", startDate, endDate);
        return list(queryWrapper);
    }

    @Override
    public List<UserOrder> userListBetweenDate(Integer account, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", account).between("time", startDate, endDate);
        return list(queryWrapper);
    }

    @Override
    public List<UserOrder> userGetTotalAmount(Integer account) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", account);
        return list(queryWrapper);

    }
}
