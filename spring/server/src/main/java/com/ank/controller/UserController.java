package com.ank.controller;



import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.pojo.User;
import com.ank.pojo.UserOrder;
import com.ank.service.UserOrderService;
import com.ank.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author ANK.
 *
 *
 * */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private HttpServletRequest request;
    private UserService userService;
    private UserOrderService userOrderService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserOrderService(UserOrderService userOrderService) { this.userOrderService = userOrderService; }
    /**
     * 会员登录
     *
     * 日期：7.23
     * @param parameters
     * @return user表中的所有属性，password 为 NULL
     * */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, Object> parameters) {
        //遍历map
        parameters.forEach((key, value) -> log.info("Key: " + key + " Value: " + value));
        Map<String, Object> map = userService.login(
                Integer.valueOf((String) parameters.get("account")),
                (String) parameters.get("password"));
                return new Result(map);

    }


    /**
     * 会员注册
     *
     * 日期 ：7.22
     * @param user 为注册的会员信息
     * @return 会员卡号
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return new Result(userService.register(user));
    }


    /**
     * 会员余额充值
     * 日期：7.27
     *
     * @param amount 结构 { "amount": Double }
     * @return 充值是否成功
     * */
    @PostMapping("/recharge")
    public Result recharge(@RequestBody Map<String, Double> amount) {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return userService.recharge(account, amount.get("amount")) ?
                Result.success() : new Result("充值失败");
    }


    /**
     * 会员修改密码
     * 日期：7.28
     *
     * @param parameters 结构 { "oldPassword": String, "newPassword": String}
     * @return 修改是否成功
     * */
    @PostMapping("/change_password")
    public Result changePassword(@RequestBody Map<String, String> parameters) {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return userService.changePassword(
                account,
                parameters.get("oldPassword"),
                parameters.get("newPassword")
        )? Result.success() : new Result("原密码输入错误");
    }

    /**
     * 会员兑换折扣券
     * 日期： 7.31
     *
     * @param couponType 兑换的折扣券种类
     * @param number     兑换的数量
     * @return 是否成功
     * */
    @GetMapping("/redeem")
    public Result redeem(@RequestParam("couponType") Integer couponType,
                         @RequestParam("number") Integer number) {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return userService.redeem(account, couponType, number) ?
                Result.success() : new Result("兑换失败：积分不足");
    }

    /**
     * 用户升级会员等级
     *
     * @param totalConsumption 会员的消费金额
     * @return 是否成功
     * */
    @GetMapping("/levelUp")
    public Result levelUp(@RequestParam("totalConsumption") Double totalConsumption) {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return userService.levelUp(account, totalConsumption) ? Result.success() : new Result("升级失败：消费不足");

    }


}
