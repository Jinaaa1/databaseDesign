package com.ank.controller;


import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user_coupon")
@Slf4j
public class UserCouponController {

    private HttpServletRequest request;
    UserCouponService userCouponService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setUserCouponService(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }


    /**
     * 获取会员持有的所有优惠券信息
     * 日期： 7.29
     *
     * @return 会员持有的所有优惠券信息
     * */
    @GetMapping("/user")
    public Result getUserCoupon() {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        Map<String, Object> map = new HashMap<>(1);
        map.put("user_id", account);
        return new  Result(userCouponService.listByMap(map));
    }
}
