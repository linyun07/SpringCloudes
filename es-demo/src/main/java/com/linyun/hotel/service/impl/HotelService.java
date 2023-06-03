package com.linyun.hotel.service.impl;

import com.linyun.hotel.mapper.HotelMapper;
import com.linyun.hotel.pojo.Hotel;
import com.linyun.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author linyun
 */
@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
}
