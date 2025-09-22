package com.ank.controller;

import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.exception.BaseException;
import com.ank.pojo.Charge;
import com.ank.pojo.UserOrder;
import com.ank.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/charge")
@Slf4j
public class ChargeController {
    private HttpServletRequest request;
    ChargeService chargeService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setChargeService(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    /**
     * 会员获取充值记录
     * 8.7
     *
     * @return 该会员的充值记录
     */
    @GetMapping("/user")
    public Result getChargeRecords() {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("user_id", account);
        return new Result(chargeService.listByMap(condition));
    }


    /**
     * 检查当前登录身份是否是管理员，若不是管理员，则抛出异常
     */
    private void checkIdentity() {
        if (JwtUtils.notAdministrator(request.getHeader(JwtUtils.AUTH_HEADER_KEY))) {
            throw new BaseException("您不具备管理员权限");
        }
    }
    /**
     * 管理员获取一段日期内的充值记录
     * 日期 8.4
     *
     * @param startDate 起始日期
     * @param endDate   截止日期
     * @return 符合条件的充值记录
     */

    @GetMapping("/administrator/between")
    public Result administratorGetChargeRecordsBetweenDate(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        checkIdentity();
        return new Result(chargeService.listBetweenDate(startDate, endDate));
    }

    /**
     * 查询所有会员充值总金额
     * 日期：8.9
     *
     * @return 订单金额总和 totalAmount
     * */
    @GetMapping("/administrator/totalAmount")
    public Result getTotalAmount() {
        checkIdentity();
        double totalAmount = 0;
        List<Charge> list = chargeService.list();
        for (Charge charge : list) {
            totalAmount += charge.getAmount();
        }
        return new Result(totalAmount);
    }

    /**
     * 管理员获取单个会员充值记录
     *
     * @param account 会员卡号
     * @return 该会员的充值记录
     */
    @GetMapping("/administrator")
    public Result administratorGetChargeRecords(@RequestParam("account") Integer account) {
        checkIdentity();
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("user_id", account);
        return new Result(chargeService.listByMap(condition));
    }
}
