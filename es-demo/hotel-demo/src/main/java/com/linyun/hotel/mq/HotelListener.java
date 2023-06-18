package com.linyun.hotel.mq;

import com.linyun.hotel.constants.HotelMqConstants;
import com.linyun.hotel.service.IHotelService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author linyun
 * @date 2023/6/18 19:44
 */

@Component
public class HotelListener {
    @Resource
    private IHotelService hotelService;

    @RabbitListener(queues = HotelMqConstants.HOTEL_INSERT_QUEUE)
    public void listenHotelSaveOrUpdate(Long id) {
        hotelService.insertById(id);
    }

    @RabbitListener(queues = HotelMqConstants.HOTEL_DELETE_QUEUE)
    public void listenDelete(Long id) {
        hotelService.deleteById(id);
    }
}
