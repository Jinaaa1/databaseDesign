package com.ank.service;

import com.ank.pojo.MembershipLevel;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MembershipLevelService extends IService<MembershipLevel> {
    Double getDiscount(Integer level);
}
