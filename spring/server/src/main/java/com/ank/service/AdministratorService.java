package com.ank.service;

import com.ank.pojo.Administrator;
import com.ank.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdministratorService extends IService<Administrator> {

    String login(String account, String password);

    boolean checkAdministratorPassword(String account, String password);

    boolean changeUser(User user);
}
