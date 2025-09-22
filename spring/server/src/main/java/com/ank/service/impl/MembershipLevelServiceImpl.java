package com.ank.service.impl;


import com.ank.mapper.MembershipLevelMapper;
import com.ank.pojo.MembershipLevel;
import com.ank.service.MembershipLevelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MembershipLevelServiceImpl extends ServiceImpl<MembershipLevelMapper, MembershipLevel>  implements MembershipLevelService {

    @Override
    public Double getDiscount(Integer level) {
        QueryWrapper<MembershipLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", level).select("discount");
        return (Double) getMap(queryWrapper).get("discount");
    }
}
