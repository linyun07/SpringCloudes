package com.linyun.hotel;

import com.alibaba.fastjson.JSON;
import com.linyun.hotel.pojo.Hotel;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        getHits(response);

    }

    @Test
    void matchQueryTest() throws IOException {
        //1.准备request
        SearchRequest request = new SearchRequest("hotel");
        //2.拼接条件
        request.source().query(QueryBuilders.matchQuery("all", "如家"));
        //发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        getHits(response);

    }

    @Test
    void termAndRangQueryTest() throws IOException {
        //1.准备request
        SearchRequest request = new SearchRequest("hotel");
        //2.拼接条件
        request.source().query(QueryBuilders.termQuery("city", "上海"));
//        request.source().query(QueryBuilders.rangeQuery("price").gt(100).lte(150));
        //发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        getHits(response);

    }

    @Test
    void boolQueryTest() throws IOException {
        //1.准备request
        SearchRequest request = new SearchRequest("hotel");
        //2.拼接条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city", "上海"))
                .filter(QueryBuilders.rangeQuery("price").gte(150).lt(200));
        request.source().query(boolQuery).from(0).size(5).sort("price", SortOrder.ASC);
        //发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        getHits(response);

    }

    @Test
    void highlighterTest() throws IOException {
        //1.准备request
        SearchRequest request = new SearchRequest("hotel");
        //2.拼接条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city", "上海"))
                .filter(QueryBuilders.rangeQuery("price").gte(150).lt(300));
        request.source().query(boolQuery)
                .from(0).size(5)
                .sort("price", SortOrder.ASC);
        request.source().highlighter(new HighlightBuilder()
                .field("city")
                .requireFieldMatch(false));

        //发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        getHits(response);

    }


    @Test
    void testSuggest() throws IOException {
        //1.准备request
        SearchRequest request = new SearchRequest("hotel");
        //2.准备DSL
        request.source().suggest(new SuggestBuilder().addSuggestion(
                "suggestions",
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix("hz")
                        .skipDuplicates(true)
                        .size(10)
        ));
        //3.发起请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4.解析结果

        Suggest suggest = response.getSuggest();
        CompletionSuggestion suggestion = suggest.getSuggestion("suggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();

        for (CompletionSuggestion.Entry.Option option : options) {
            System.out.println(option.getText().string());
        }
    }

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://47.120.37.50:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    private static void getHits(SearchResponse response) {
        //4.解析结果
        SearchHits searchHits = response.getHits();
        //4.1获取总条数
        long total = searchHits.getTotalHits().value;
        System.err.println("查询总条数是：" + total);
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Hotel hotel = JSON.parseObject(json, Hotel.class);

            //获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("city");
                if (highlightField != null) {
                    String name = highlightField.getFragments()[0].string();
                    hotel.setCity(name);
                }
            }
            //根据字段名获取高连结果

            System.out.println(hotel.toString());
        }
    }
}
