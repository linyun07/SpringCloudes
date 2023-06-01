package com.linyun.order.pojo;

import com.linyun.feign.pojo.User;
import lombok.Data;

/**
 * @author linyun
 */
@Data
public class Order {
    private Long id;
    private Long price;
    private String name;
    private Integer num;
    private Long userId;
    private User user;
}