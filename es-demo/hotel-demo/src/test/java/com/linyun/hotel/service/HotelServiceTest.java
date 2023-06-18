package com.linyun.hotel.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author linyun
 * @date 2023/6/18 11:02
 */

@SpringBootTest
public class HotelServiceTest {
    @Resource
    private IHotelService hotelService;

    @Test
    void  filterTest(){
//        Map<String, List<String>> filter = hotelService.filter();
//        System.out.println(filter);
    }

}
