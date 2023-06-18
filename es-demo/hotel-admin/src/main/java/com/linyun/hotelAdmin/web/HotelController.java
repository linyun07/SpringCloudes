package com.linyun.hotelAdmin.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linyun.hotelAdmin.constants.HotelMqConstants;
import com.linyun.hotelAdmin.pojo.Hotel;
import com.linyun.hotelAdmin.pojo.PageResult;
import com.linyun.hotelAdmin.service.IHotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.InvalidParameterException;

@Slf4j
@RestController
@RequestMapping("hotel")
public class HotelController {

    @Resource
    private IHotelService hotelService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/{id}")
    public Hotel queryById(@PathVariable("id") Long id) {
        return hotelService.getById(id);
    }

    @GetMapping("/list")
    public PageResult hotelList(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    ) {
        Page<Hotel> result = hotelService.page(new Page<>(page, size));
        return new PageResult(result.getTotal(), result.getRecords());
    }

    @PostMapping

    public void saveHotel(@RequestBody Hotel hotel) {
        // 新增酒店
        hotelService.save(hotel);
        // 发送MQ消息
        rabbitTemplate.convertAndSend(HotelMqConstants.HOTEL_EXCHANGE, HotelMqConstants.HOTEL_INSERT_KEY, hotel.getId());
    }

    @PutMapping()
    public void updateById(@RequestBody Hotel hotel) {
        if (hotel.getId() == null) {
            throw new InvalidParameterException("id不能为空");
        }
        hotelService.updateById(hotel);

        // 发送MQ消息
        rabbitTemplate.convertAndSend(HotelMqConstants.HOTEL_EXCHANGE, HotelMqConstants.HOTEL_INSERT_KEY, hotel.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        hotelService.removeById(id);

        // 发送MQ消息
        rabbitTemplate.convertAndSend(HotelMqConstants.HOTEL_EXCHANGE, HotelMqConstants.HOTEL_DELETE_KEY, id);
    }
}
