package com.ank.mapper;


import com.ank.pojo.MembershipLevel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MembershipLevelMapper extends BaseMapper<MembershipLevel> {
    @Select("SELECT * FROM membership_level WHERE required_amount <= #{amount} " +
            "ORDER BY required_amount DESC LIMIT 1")
    MembershipLevel selectMaxLevelByAmount(@Param("amount") Double amount);
}
