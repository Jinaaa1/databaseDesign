package com.ank.service.impl;

import com.ank.common.JwtUtils;
import com.ank.exception.BaseException;
import com.ank.exception.UserException;
import com.ank.mapper.MembershipLevelMapper;
import com.ank.mapper.UserMapper;
import com.ank.pojo.*;
import com.ank.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    MembershipLevelMapper membershipLevelMapper;

    CouponService couponService;

    UserCouponService userCouponService;

    ChargeService chargeService;

    RedeemService redeemService;

    @Autowired
    public void setChargeService(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @Autowired
    public void setRedeemService(RedeemService redeemService) { this.redeemService = redeemService; }

    @Autowired
    public void setUserCouponService(UserCouponService userCouponService) { this.userCouponService = userCouponService; }

    @Autowired
    public void setCouponService(CouponService couponService) { this.couponService = couponService; }

    @Autowired
    public void setMembershipLevelMapper(MembershipLevelMapper membershipLevelMapper) { this.membershipLevelMapper = membershipLevelMapper; }

    @Override
    public Map<String, Integer> register(User user) {
        save(user);
        log.info("Register new user: " + user);
        Map<String, Integer> map = new HashMap<>(1);
        map.put("account", user.getAccount());
        return map;
    }

    @Override
    public Map<String, Object> login(Integer account, String password) {
        //检查数据
        if(!(account == null || StringUtils.isEmpty(password))){
            String newPassword;

            Map<String, Object> conditions = new HashMap<>(2);
            conditions.put("account", account);
            conditions.put("password", password);
            List<User> users = getBaseMapper().selectByMap(conditions);
            if (users.size() > 0) {
                users.forEach(System.out::println);
                if (users.size() > 1) {
                    log.warn("登录异常：查询到的账户大于一个");
                }
                User temp = users.get(0);
                temp.setPassword(null);
                String authorization = JwtUtils.getUserToken(temp);
                Map<String, Object> map = new HashMap<>(2);
                map.put("user", temp);
                map.put("authorization", authorization);
                return map;
            }else {
                throw new UserException("卡号或密码错误");
            }
        }
        throw new BaseException("卡号或密码不可为空");
}

    @Override
    @Transactional//加入 @transactional 注解，抛出异常之后，事务会自动回滚，数据不会插入到数据库中。
    public boolean recharge(Integer account, Double amount) {
        //MyBatis-Plus查询构造器，用于构建SQL条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //按 account 查询 相当于 WHERE account = #{account}，指定只查询balance字段
        queryWrapper.eq("account", account).select("balance");
        List<Map<String, Object>> list = listMaps(queryWrapper);
        Double balance = (Double) list.get(0).get("balance");
        //MyBatis-Plus更新构造器
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        //更新条件 WHERE account = #{account}，设置新余额 SET balance = #{当前余额 + 充值金额}
        updateWrapper.eq("account", account).set("balance", amount + balance);
        if (update(updateWrapper)) {
            Charge charge = new Charge();
            charge.setUserId(account);
            charge.setAmount(amount);
            charge.setTime(LocalDateTime.now());
            log.info("charge:" + charge);
            return chargeService.save(charge);
        } else {
            return false;
        }
    }

    @Override
    public boolean changePassword(Integer account, String oldPassword, String newPassword) {
        //MyBatis-Plus更新构造器
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account).eq("password", oldPassword);
        updateWrapper.set("password", newPassword);
        log.info("changePassword:" + updateWrapper);
        return update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean redeem(Integer account, Integer couponType, Integer number) {
        // 查询会员积分
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account).select("points");;
        Map<String, Object> conditions = getMap(queryWrapper);
        Integer points = (Integer) conditions.get("points");

        // 查询兑换的折扣券所需要的积分并且计算出总共需要的积分
        QueryWrapper<Coupon> couponQueryWrapper = new QueryWrapper<>();
        couponQueryWrapper.eq("type", couponType).select("cost");
        conditions = couponService.getMap(couponQueryWrapper);
        Integer costPoints = (Integer) conditions.get("cost") * number;

        // 判断会员的积分是否足够
        if (points >= costPoints){
            // 当足够兑换时，先在 user_coupon 中查找会员对应满减券的数量，然后加上兑换的数量，
            // 若没有记录则新建一条
            UpdateWrapper<UserCoupon> userCouponUpdateWrapper = new UpdateWrapper<>();
            userCouponUpdateWrapper.eq("user_id", account)
                    .eq("coupon_type", couponType).setSql("quantity = quantity + " + number);
            // 若更新失败，则将 entity 插入。以下为 entity
            if (!userCouponService.update(userCouponUpdateWrapper)) {
                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setUserId(account);
                userCoupon.setCouponType(couponType);
                userCoupon.setQuantity(number);
                if (!userCouponService.save(userCoupon)) {
                    throw new BaseException("兑换失败：增加会员满减券数量失败");
                }
            }

            //扣除对应积分
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("account", account).set("points", points - costPoints);
            // 向 redeem 表中插入兑换记录
            if (update(updateWrapper)) {
                Redeem redeem = new Redeem();
                redeem.setUserId(account);
                redeem.setCouponType(couponType);
                redeem.setNumber(number);
                redeem.setValue(costPoints);
                redeem.setTime(LocalDateTime.now());
                if (redeemService.save(redeem)){
                    return true;
                } else {
                    throw new BaseException("兑换失败：兑换记录存储失败");
                }
            } else {
                throw new BaseException("兑换失败：扣除会员积分失败");
            }

        }
        return false;
    }

    @Override
    public boolean deductBalance(Integer account, Double amount) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account).ge("balance", amount).setSql("balance = balance - " + amount);
        return update(updateWrapper);
    }

    @Override
    public boolean addPoints(Integer account, Integer points) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account).setSql("points = points + " + points);
        return update(updateWrapper);
    }

    @Override
    public boolean levelUp(Integer account, double totalAmount) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);


        MembershipLevel targetLevel = membershipLevelMapper.selectOne(
                new LambdaQueryWrapper<MembershipLevel>()
                        .le(MembershipLevel::getRequiredAmount, totalAmount)
                        .orderByDesc(MembershipLevel::getRequiredAmount)
                        .last("LIMIT 1")
        );

        if (targetLevel == null) {
            return false; // 消费金额不足以达到任何等级
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account)
                .set("level", targetLevel.getType());

        return update(updateWrapper);
    }

}
