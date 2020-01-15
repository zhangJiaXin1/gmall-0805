package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.exception.UmsException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkout(String data, Integer type) {
        //添加用户，判断用户是否存在,检验数据是否可用
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type){
            case 1: wrapper.eq("username",data) ;break;
            case 2: wrapper.eq("mobile",data) ;break;
            case 3: wrapper.eq("email",data) ;break;
            default:
                return null;
        }
        boolean b = this.memberDao.selectCount(wrapper) == 0;
        //0可用，1不可用
        return b;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        //先判断将判断验证码是否正确，然后将密码进行撒盐
        //先将手机号作为key,从redis中获取验证码是否正确
//        String mobile = memberEntity.getMobile();
//        //在redis中生成6位数的验证码
//        String cdoe = UUID.randomUUID().toString().substring(0, 6);
//        this.redisTemplate.opsForValue().set(mobile,code,60, TimeUnit.SECONDS);
        //先判断验证码和redis中的是否一致
        String rediscode = this.redisTemplate.opsForValue().get(memberEntity.getMobile());
        if(!StringUtils.equals(code,rediscode)){
            //如果不相等就结束方法
            throw new UmsException("验证码不对");
        }
        //将密码进行加密撒盐
        String salt= UUID.randomUUID().toString().substring(0,6);
        memberEntity.setSalt(salt);
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword() + salt));
        memberEntity.setCreateTime(new Date());
        memberEntity.setLevelId(1l);
        memberEntity.setStatus(1);
        memberEntity.setSourceType(1);
        memberEntity.setGrowth(1000);
        memberEntity.setIntegration(1000);
        this.save(memberEntity);
        //注册成功删除redis中的记录
    }

    @Override
    public MemberEntity queryMemberEntity(String username, String password) {
        //先通过用户名判断用户是否存在,然后判断密码是否正确
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        if(memberEntity==null){
            return null;
        }
        //判断密码，先撒盐
        //将盐加入到密码进行加密
        String passwordmd5 = DigestUtils.md5Hex(password + memberEntity.getSalt());
        if(!StringUtils.equals(passwordmd5,memberEntity.getPassword())){
            return null;
        }
        return memberEntity;
    }

}