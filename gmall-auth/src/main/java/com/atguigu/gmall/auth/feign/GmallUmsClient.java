package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GamllUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("ums-service")
public interface GmallUmsClient  extends GamllUmsApi {

}
