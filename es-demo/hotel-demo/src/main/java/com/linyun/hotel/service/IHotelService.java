package com.linyun.hotel.service;

import com.linyun.hotel.pojo.Hotel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linyun.hotel.pojo.PageResult;
import com.linyun.hotel.pojo.RequestParams;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author linyun
 */
public interface IHotelService extends IService<Hotel> {


    PageResult search(RequestParams params);

    Map<String, List<String>> filter(RequestParams params);

    List<String> listSuggestion(String prefix);

    void deleteById(Long id);

    void insertById(Long id);
}
