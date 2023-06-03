package com.linyun.hotel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangqianwei
 * @date 2023/6/3 10:44
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestParams {
    private String key;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String city;
    private String brand;
    private String starName;
    private Integer maxPrice;
    private Integer minPrice;
    private String location;

}
