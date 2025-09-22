package com.ank.controller;


import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.exception.BaseException;
import com.ank.pojo.Commodity;
import com.ank.service.CommodityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/commodity")
@Slf4j
public class CommodityController {
    private HttpServletRequest request;
    CommodityService commodityService;


    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setCommodityService(CommodityService commodityService) {
        this.commodityService = commodityService;
    }


    /**
     * 查询所有商品的信息
     *
     * @return 所有商品的信息
     * */
    @GetMapping("/all")
    public Result allCommodity() { return new Result(commodityService.list()); }


    /**
     * 查询单个商品信息
     * 8.7
     *
     * @param commodityId 商品id
     * @return 商品信息
     */

    @GetMapping("/query")
    public Result queryCommodity(@RequestParam("commodityId") Integer commodityId) {
        return new Result(commodityService.getById(commodityId));
    }



    /**
     * 管理员新增商品
     * 日期： 8.5
     *
     * @param commodity 商品实体
     * @return 是否新增成功
     */
    @PostMapping("/add")
    public Result addCommodity(@RequestBody Commodity commodity) {
        checkIdentity();
        commodity.setId(null);
        return commodityService.save(commodity) ? new Result(commodity.getId())
                : new Result("添加失败");
    }

    /**
     * 管理员修改商品信息
     * 8.14
     *
     * @param commodity 商品实体
     * @return 是否修改成功
     */
    @PostMapping("/change")
    public Result changeCommodity(@RequestBody Commodity commodity) {
        checkIdentity();
        return commodityService.updateById(commodity) ? Result.success()
                : new Result("更新失败");
    }


    /**
     * 管理员删除商品
     * 8.14
     *
     * @param commodityId 商品id
     * @return 是否删除成功
     */
    @GetMapping("/delete")
    public Result deleteCommodity(@RequestParam("commodityId") Integer commodityId) {
        checkIdentity();
        try {
            return commodityService.removeById(commodityId) ? Result.success()
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
