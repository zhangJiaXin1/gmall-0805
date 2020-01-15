package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    private static final String pubKeyPath = "E:\\gmall-0805\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\gmall-0805\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

   @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzkxMDU1ODd9.ZTVryk61vofWWjBd1BLwNsZtKZwU9PmyPYKbMseCYKByz8KJSXopVnkCNUB7yz0SgIYx1RjIXtXINfnykJdVCqrCCvotaeONIdzD8b24-0sxyWiFnaBUJvVnk0-1Yz91DmhjFjl21vuTnUf_fei9Gj08aTglwq1WCMU_BxpP_v8FRQ7V_JG3dRa4cxnbn3hVRA9gR4BV6ahwGCLo-tvYTr8I70fbba6sEUE75SBudZ8phWo4UWSZ09NczhrqE8C-BpF3TUT5h_1hsxGHL8DdOlpMrBj973iRDbEDljZxymn5fYl0bBtKlmkrKCCqTIPN9gRSGHGR3kEiyTYUp4BtVA";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}
