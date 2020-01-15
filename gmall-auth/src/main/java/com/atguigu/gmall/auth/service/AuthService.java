package com.atguigu.gmall.auth.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.ums.entity.MemberEntity;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthService {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private GmallUmsClient umsClient;
    public String accredit(String username, String password) {
        try {
            //远程调用ums微服务
            Resp<MemberEntity> memberEntityResp = this.umsClient.queryMemberEntity(username, password);
            MemberEntity memberEntity = memberEntityResp.getData();
            //判断用户是否为空
            if(memberEntity==null){
                return null;
            }
            //返回jwt对象,需要map存储数据
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",memberEntity.getId());
            map.put("username",memberEntity.getUsername());
            String jwtToken = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), 30);
            return jwtToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
