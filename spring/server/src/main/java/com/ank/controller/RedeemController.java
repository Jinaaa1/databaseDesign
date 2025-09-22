package com.ank.controller;

import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.exception.BaseException;
import com.ank.service.RedeemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/redeem")
@Slf4j
public class RedeemController {

    private HttpServletRequest request;
    RedeemService redeemService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setRedeemService(RedeemService redeemService) {
        this.redeemService = redeemService;
    }


    /**
     * 会员获取满减券兑换记录
     *  8.7
     *
     * @return 该会员的满减券兑换记录
     */
    @GetMapping("/user")
    public Result getRedeemRecords() {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return new Result(redeemService.getUserRedeem(account));
    }


    /**
     * 管理员获取会员满减券兑换记录
     *
     * @param account 会员卡号
     * @return 该会员的满减券兑换记录
     */
    @GetMapping("/administrator")
    public Result adminGetRedeemRecords(@RequestParam("account") Integer account) {
        checkIdentity();
        return new Result(redeemService.getUserRedeem(account));
    }

    /**
     * 检查当前登录身份是否是管理员，若不是管理员，则抛出异常
     */
    private void checkIdentity() {
        if (JwtUtils.notAdministrator(request.getHeader(JwtUtils.AUTH_HEADER_KEY))) {
            throw new BaseException("您不具备管理员权限");
        }
    }
}
