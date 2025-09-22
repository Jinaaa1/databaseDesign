package com.ank.controller;


import com.ank.common.Result;
import com.ank.service.MembershipLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/membership_level")
@Slf4j
public class MembershipLevelController {

    MembershipLevelService membershipLevelService;

    @Autowired
    public void setMembershipLevelService(MembershipLevelService membershipLevelService) {
        this.membershipLevelService = membershipLevelService;
    }

    @GetMapping("/all")
    public Result getAllLevels() {
//        List list = membershipLevelService.list();
        return new Result(membershipLevelService.list());
    }
}
