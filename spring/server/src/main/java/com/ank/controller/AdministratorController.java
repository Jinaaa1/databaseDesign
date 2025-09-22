package com.ank.controller;

import com.ank.common.JwtUtils;
import com.ank.common.Result;
import com.ank.exception.BaseException;
import com.ank.pojo.Administrator;
import com.ank.pojo.MembershipLevel;
import com.ank.pojo.User;
import com.ank.service.AdministratorService;
import com.ank.service.MembershipLevelService;
import com.ank.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/administrator")
public class AdministratorController {
    private HttpServletRequest request;
    AdministratorService administratorService;
    UserService userService;
    MembershipLevelService membershipLevelService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setAdministratorService(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @Autowired
    public void setMembershipLevelService(MembershipLevelService membershipLevelService) {
        this.membershipLevelService = membershipLevelService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 管理员登录
     * 日期 7.23
     *
     * @param administrator 管理员实体
     * @return token
     */
    @PostMapping("/login")
    public Result login(@RequestBody Administrator administrator) {
        String account = administrator.getAccount();
        String password = administrator.getPassword();
        Map<String, String> map = new HashMap<>(1);
        map.put("authorization", administratorService.login(account, password));
        return new Result(map);
    }

    /**
     * 检查当前登录身份是否是管理员，若不是管理员，则抛出异常
     */
    private  void checkIdentity() {
        if (JwtUtils.notAdministrator(request.getHeader(JwtUtils.AUTH_HEADER_KEY))) {
            throw new BaseException("您不具备管理员权限");
        }
    }


    /**
     * 管理员获取所有会员信息
     *
     * @return 所有会员实体
     */
    @GetMapping("/get/users")
    public Result getUsers() {
        checkIdentity();
        List<User> userList = userService.list();
        for (User user : userList) {
            user.setPassword(null);
        }
        return new Result(userList);
    }

    /**
     * 获取当前管理员账号
     *
     * @return 管理员账号
     */
    private String getAccount() {
        return JwtUtils.getAdministratorAccount(request.getHeader(JwtUtils.AUTH_HEADER_KEY));
    }

    /**
     * 检查管理员密码是否正确，若错误，则抛出异常
     *
     * @param password 管理员密码
     */
    private void checkPassword(String password) {
        if (!administratorService.checkAdministratorPassword(getAccount(), password)) {
            throw new BaseException("管理员密码验证错误");
        }
    }

    /**
     * 修改会员信息
     *
     * @param map 会员实体以及管理员密码
     * @return 是否修改成功
     */

    @PostMapping("/change/user")
    public Result changeUser(@RequestBody Map<String, Object> map) {
        checkIdentity();
        checkPassword((String) map.get("password"));
        try {
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>)
                    map.get("user");
            ObjectMapper objectMapper = new ObjectMapper();
            String userJson = objectMapper.writeValueAsString(linkedHashMap);
            User user = objectMapper.readValue(userJson, User.class);
            return administratorService.changeUser(user) ? Result.success()
                    : new Result("修改失败");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BaseException("数据格式错误");
        }
    }


    /**
     * 修改会员等级
     *
     * 8.14
     * @param map 会员等级实体及管理员密码
     * @return 是否修改成功
     */
    @PostMapping("/change/membership_level")
    public Result changeMembershipLevel(@RequestBody Map<String, Object> map) {
        checkIdentity();
        checkPassword((String) map.get("password"));
        try {
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>)
                    map.get("membershipLevel");
            ObjectMapper objectMapper = new ObjectMapper();
            String membershipLevelJson = objectMapper.writeValueAsString(linkedHashMap);
            MembershipLevel membershipLevel = objectMapper
                    .readValue(membershipLevelJson, MembershipLevel.class);
            return membershipLevelService.updateById(membershipLevel) ?
                    Result.success() : new Result("更新失败");
        } catch (JsonProcessingException e) {
            throw new BaseException("数据格式错误");
        }
    }

}
