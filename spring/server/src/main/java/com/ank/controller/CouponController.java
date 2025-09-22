package com.ank.controller;

import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.exception.BaseException;
import com.ank.pojo.Coupon;
import com.ank.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/coupon")
@Slf4j
public class CouponController {
    private HttpServletRequest request;
    CouponService couponService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * 查询所有优惠券信息
     * 日期： 7.29
     *
     * @return 所有优惠券信息
     * */
    @GetMapping("/all")
    public Result getAllCoupon() {
        log.info(couponService.list().toString());
        return new Result(couponService.list());}


    /**
     * 管理员增加满减券
     * 日期：8.9
     *
     * @param coupon 满减券实体
     * @return 是否添加成功
     */

    @PostMapping("/add")
    public Result addCoupon(@RequestBody Coupon coupon) {
        checkIdentity();
        coupon.setType(null);
        return couponService.save(coupon) ? new Result(coupon.getType())
                : new Result("添加失败");
    }

    /**
     * 管理员删除满减券
     * 日期：8.9
     *
     * @param couponId 满减券id
     * @return 是否删除成功
     */
    @GetMapping("/delete")
    public Result deleteCoupon(@RequestParam("couponId") Integer couponId) {
        checkIdentity();
        try {
            return couponService.removeById(couponId) ? Result.success()
                    : new Result("删除失败");
        } catch (DataIntegrityViolationException e) {
            return new Result("删除失败");
        }
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
