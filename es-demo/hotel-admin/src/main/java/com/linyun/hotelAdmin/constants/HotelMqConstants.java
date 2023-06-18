package com.linyun.hotelAdmin.constants;

/**
 * @author linyun
 * @date 2023/6/18 19:22
 */


public class HotelMqConstants {
    /**
     * 交换机
     */
    public final static String HOTEL_EXCHANGE="hotel.topic";
    /**
     * 监听修改或新增队列
     */
    public final static String HOTEL_INSERT_QUEUE="hotel.insert.queue";
    /**
     * 监听删除队列
     */
    public final static String HOTEL_DELETE_QUEUE="hotel.delete.queue";
    /**
     * 监听修改或新增Routing Key
     */
    public final static String HOTEL_INSERT_KEY="hotel.insert";
    /**
     * 监听删除Routing Key
     */
    public final static String HOTEL_DELETE_KEY="hotel.delete";
}
