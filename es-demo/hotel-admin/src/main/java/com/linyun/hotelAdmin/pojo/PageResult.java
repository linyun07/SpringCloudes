package com.linyun.hotelAdmin.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult implements Serializable {
    private Long total;
    private List<Hotel> hotels;

    public PageResult() {
    }

    public PageResult(Long total, List<Hotel> hotels) {
        this.total = total;
        this.hotels = hotels;
    }
}
