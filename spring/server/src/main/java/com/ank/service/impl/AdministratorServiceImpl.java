package com.ank.service.impl;

import com.ank.common.JwtUtils;
import com.ank.exception.BaseException;
import com.ank.mapper.AdministratorMapper;
import com.ank.mapper.UserMapper;
import com.ank.pojo.Administrator;
import com.ank.pojo.User;
import com.ank.service.AdministratorService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AdministratorServiceImpl extends ServiceImpl<AdministratorMapper, Administrator> implements AdministratorService {
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 管理员登录
     * 日期： 8.3
     *
     * @param account  管理员账号
     * @param password 管理员密码
     * @return token
     */
    @Override
    public String login(String account, String password) {
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            throw new BaseException("账号或密码不可为空白");
        }
        Map<String, Object> conditions = new HashMap<>(2);
        conditions.put("account", account);
        conditions.put("password", password);
        List<Administrator> administratorList = listByMap(conditions);
        if (administratorList.size() > 0) {
            return JwtUtils.getAdministratorToken(administratorList.get(0));
        } else {
            throw new BaseException("账号或密码错误");
        }
    }


    /**
     * 检查管理员密码是否正确
     *
     * @param password 密码原数据
     * @return 密码是否正确
     */
    @Override
    public boolean checkAdministratorPassword(String account, String password) {
        try {
            Map<String, Object> conditions = new HashMap<>(2);
            conditions.put("account", account);
            conditions.put("password", password);
            List<Administrator> administratorList = listByMap(conditions);
            return administratorList.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 修改会员信息
     *
     * @param user User实体
     * @return 是否修改成功
     */
    @Override
    public boolean changeUser(User user) {
        try {
            if (user.getPassword() != null) {
                user.setPassword(user.getPassword());
            }
            user.setBalance(null);
            user.setLevel(null);
            user.setPoints(null);
            return userMapper.updateById(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }
}
