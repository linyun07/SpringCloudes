package com.linyun.feign.clients;

import com.linyun.feign.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhangqianwei
 * @date 2023/6/1 16:02
 */

@FeignClient("userServer")
public interface UserClient {
    @GetMapping("user/{id}")
    User findById(@PathVariable("id") Long id);
}
