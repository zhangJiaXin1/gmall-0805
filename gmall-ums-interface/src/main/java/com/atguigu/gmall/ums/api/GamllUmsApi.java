package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.*;

public interface GamllUmsApi {
    @GetMapping("ums/member/query")
    public Resp<MemberEntity> queryMemberEntity( @RequestParam("username") String username,
                                                 @RequestParam("password") String password);
}
