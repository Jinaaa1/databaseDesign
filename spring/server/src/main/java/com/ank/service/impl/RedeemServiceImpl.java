package com.ank.service.impl;

import com.ank.mapper.RedeemMapper;
import com.ank.pojo.Redeem;
import com.ank.service.RedeemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RedeemServiceImpl extends ServiceImpl<RedeemMapper, Redeem> implements RedeemService {

    /**
     * 根据会员卡号获取会员兑换满减券记录
     * @param account 会员卡号
     * @return redeem 兑换记录 List
     */
    @Override
    public List<Redeem> getUserRedeem(Integer account) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("user_id", account);
        return listByMap(map);
    }
}
