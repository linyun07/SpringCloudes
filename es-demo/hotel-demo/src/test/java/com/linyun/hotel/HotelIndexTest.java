package com.linyun.hotel;



import com.alibaba.fastjson.JSON;
import com.linyun.hotel.pojo.Hotel;
import com.linyun.hotel.pojo.HotelDoc;
import com.linyun.hotel.service.IHotelService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author zhangqianwei
 * @date 2023/6/2 16:24
 */

@SpringBootTest
public class HotelIndexTest {

    private RestHighLevelClient client;
    @Resource
    private IHotelService hotelService;
    @Test
    void testInit(){
        System.out.println(client);
    }
    @Test
    void matchAllTest() throws IOException {
        //1.准备request
        SearchRequest request = new SearchRequest("hotel");
        //2.拼接条件
        request.source().query(QueryBuilders.matchAllQuery());
        //发起请求
        SearchResponse response=client.search(request, RequestOptions.DEFAULT);

        System.out.println(response);
    }


    @BeforeEach
    void setUp(){
        this.client=new RestHighLevelClient(RestClient.builder(
                HttpHost.create("47.120.37.50:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException{
        this.client.close();
    }
}
