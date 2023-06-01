package com.linyun.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author zhangqianwei
 * @date 2023/6/1 17:24
 */

@Order(-1)
@Component
public class AuthorizeFilter implements GlobalFilter {
    /**
     * 处理当前请求 有必要的话通过 将请求交给下一个过滤器处理
     *
     * @param exchange 请求上下文 可以获取Request、Response等信息
     * @param chain    用来请求委托给下一个过滤器
     * @return Mono<Void> 返回表示当前过滤器结束
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取请求参数
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params = request.getQueryParams();
        //2.获取参数中的username
        String username = params.getFirst("username");

        //3.判断参数值是否等于admin
        if ("admin".equals(username)) {
            //4.是 放行
            return chain.filter(exchange);
        }
        //5.否 拦截
        //5.1设置状态码
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
