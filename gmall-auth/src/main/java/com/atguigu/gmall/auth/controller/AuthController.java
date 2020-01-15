package com.atguigu.gmall.auth.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.UmsException;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AuthService authService;
    @PostMapping("accredit")
    public Resp<Object> accredit(@RequestParam("username")String username,
                                 @RequestParam("password") String password,
                                 HttpServletRequest request, HttpServletResponse response
                                 ){
        //将代码逻辑传到service中操作
        String token=this.authService.accredit(username,password);
        if(StringUtils.isEmpty(token)){
             throw new UmsException("用户名或密码错误");
        }
        //将jwt对象封装到cookie中
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
        return Resp.ok("登录成功");
    }
}
