package com.ank.controller;

import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.exception.BaseException;
import com.ank.pojo.DTO.CheckoutDTO;
import com.ank.pojo.DTO.OrderDTO;
import com.ank.pojo.OrderCommodity;
import com.ank.pojo.UserOrder;
import com.ank.service.OrderCommodityService;
import com.ank.service.UserOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class UserOrderController {
    private HttpServletRequest request;
    UserOrderService userOrderService;
    OrderCommodityService orderCommodityService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setUserOrderService(UserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    @Autowired
    public void setOrderCommodityService(OrderCommodityService orderCommodityService) {
        this.orderCommodityService = orderCommodityService;
    }


    /**
     * 结账产生订单
     *日期： 8.3
     *
     * @param checkoutDTO 包含满减券以及商品信息
     * @return 结账信息，包含花费与积分
     */

    @PostMapping("/checkout")
    public Result checkout(@RequestBody CheckoutDTO checkoutDTO) {
        Integer account = checkoutDTO.getAccount();
        OrderDTO orderDTO = checkoutDTO.getOrder();
        return new Result(userOrderService.checkout(account, orderDTO));
    }


    /**
     * 会员查询自己的订单
     * 日期： 8.7
     *
     * @return 订单信息及订单货品信息
     */
    @GetMapping("/user")
    public Result getOrdersFromUser() {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return new Result(getOrdersFromUser(account));
    }

    private Map<String, List> getOrdersFromUser(int account) {
        Map<String, Object> map1 = new HashMap<>(1);
        map1.put("user_id", account);
        List<UserOrder> userOrderList = userOrderService.listByMap(map1);
        List<List<OrderCommodity>> orderCommodityList = new ArrayList<>(userOrderList.size());
        Map<String, Object> map2 = new HashMap<>(1);
        for (UserOrder userOrder : userOrderList) {
            map2.put("order_id", userOrder.getId());
            orderCommodityList.add(orderCommodityService.listByMap(map2));
        }
        Map<String, List> returnMap = new HashMap<>(userOrderList.size());
        returnMap.put("UserOrderList", userOrderList);
        returnMap.put("OrderCommodityList", orderCommodityList);
        return returnMap;
    }

    @GetMapping("/administrator/between")
    public Result administratorGetUserOrdersBetweenDate(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        checkIdentity();
        return new Result(userOrderService.listBetweenDate(startDate, endDate));
    }

    /**
     * 管理员查询总订单金额
     * 日期：8.9
     *
     * @return 订单金额总和 totalAmount
     * */
    @GetMapping("/administrator/totalAmount")
    public Result getTotalAmount() {
        checkIdentity();
        double totalAmount = 0;
        List<UserOrder> list = userOrderService.list();
        for (UserOrder userOrder : list) {
            totalAmount += userOrder.getPaymentAmount();
        }
        return new Result(totalAmount);
    }


    /**
     * 用户查询自己的总订单金额
     *
     * @return 订单金额总和 totalAmount
     * */
    @GetMapping("/user/totalAmount")
    public Result userGetTotalAmount() {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        double totalAmount = 0;
        List<UserOrder> list = userOrderService.userGetTotalAmount(account);
        for (UserOrder userOrder : list) {
            System.out.println(userOrder);
            totalAmount += userOrder.getPaymentAmount();
            System.out.println(totalAmount);
        }
        return new Result(totalAmount);
    }


    /**
     * 用户查询自己一周内的消费
     * 日期：8.10
     *
     * @return 一周内的消费
     * */
    @GetMapping("/user/weekAmount")
    public Result getUserWeekAmount(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate)
    {
        Integer account = JwtUtils.getUserAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
        return new Result(userOrderService.userListBetweenDate(account, startDate, endDate));
    }

    /**
     * 管理员查询会员订单
     *
     * @param account 会员卡号
     * @return 订单信息及订单货品信息
     */
    @GetMapping("/administrator")
    public Result administratorGetUserOrders(@RequestParam("account") Integer account) {
        checkIdentity();
        return new Result(getOrdersFromUser(account));
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
