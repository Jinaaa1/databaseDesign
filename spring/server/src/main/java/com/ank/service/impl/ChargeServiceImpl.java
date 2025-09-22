package com.ank.service.impl;

import com.ank.mapper.ChargeMapper;
import com.ank.pojo.Charge;
import com.ank.service.ChargeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ChargeServiceImpl extends ServiceImpl<ChargeMapper, Charge> implements ChargeService {
    @Override
    public List<Charge> listBetweenDate(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Charge> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("time", startDate, endDate);
        return list(queryWrapper);
    }
}
