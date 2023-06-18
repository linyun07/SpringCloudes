package com.linyun.hotelAdmin.service.impl;

import com.linyun.hotelAdmin.mapper.HotelMapper;
import com.linyun.hotelAdmin.pojo.Hotel;
import com.linyun.hotelAdmin.service.IHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author linyun
 */
@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
}
