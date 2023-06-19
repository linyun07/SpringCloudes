package com.linyun.order.web;

import com.linyun.order.pojo.Order;
import com.linyun.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

   @Autowired
   private OrderService orderService;

    @GetMapping("{orderId}")
    public Order queryOrderByUserId(@PathVariable("orderId") Long orderId) {
        // 根据id查询订单并返回
        return orderService.queryOrderById(orderId);
    }

    @GetMapping("query")
    public String queryOrder(){
        orderService.queryGoods("查询成功");
        return "查询成功";
    }
    @GetMapping("update")
    public String updateOrder(){
        return "修改成功";
    }

    @GetMapping("save")
    public String saveOrder(){
        orderService.queryGoods("新增成功");
        return "新增成功";
    }
}
