package com.linyun.order.service;

import com.linyun.feign.clients.UserClient;
import com.linyun.feign.pojo.User;
import com.linyun.order.mapper.OrderMapper;
import com.linyun.order.pojo.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private UserClient userClient;

    public Order queryOrderById(Long orderId) {


        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        //用feign调用接口
        User user = userClient.findById(order.getUserId());

        /*String url = "http://userServer/user/" + order.getUserId();
        //发送url请求
        User user = restTemplate.getForObject(url, User.class);*/
        order.setUser(user);
        // 4.返回
        return order;
    }
}
