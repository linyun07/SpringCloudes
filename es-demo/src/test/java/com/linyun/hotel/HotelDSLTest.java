package com.linyun.hotel;

import com.mysql.cj.QueryBindings;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author zhangqianwei
 * @date 2023/6/2 16:30
 */

@SpringBootTest
public class HotelDSLTest {
private RestHighLevelClient client;
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
                HttpHost.create("http://47.120.37.50:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}
