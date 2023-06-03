package com.linyun.hotel.web;

import com.linyun.hotel.pojo.Hotel;
import com.linyun.hotel.pojo.PageResult;
import com.linyun.hotel.pojo.RequestParams;
import com.linyun.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.InvalidParameterException;

/**
 * @author linyun
 */
@RestController
@RequestMapping("hotel")
public class HotelController {

    @Resource
    private IHotelService hotelService;

    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams params) {
        System.out.println(params);
        return hotelService.search(params);
    }
}
