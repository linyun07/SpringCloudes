package com.linyun.hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.linyun.hotel.constants.HotelMqConstants;
import com.linyun.hotel.mapper.HotelMapper;
import com.linyun.hotel.pojo.Hotel;
import com.linyun.hotel.pojo.HotelDoc;
import com.linyun.hotel.pojo.PageResult;
import com.linyun.hotel.pojo.RequestParams;
import com.linyun.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author linyun
 */
@Slf4j
@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
    @Resource
    private RestHighLevelClient client;

    @Resource
    private IHotelService hotelService;

    @Override
    public PageResult search(RequestParams params) {

        try {
            //1.准备request
            SearchRequest request = new SearchRequest("hotel");
            //2.构建query
            buildBasicQuery(params, request);
            //分页
            request.source().from((params.getPage() - 1) * params.getSize()).size(params.getSize());
            //添加距离排序
            if (params.getLocation() != null && !"".equals(params.getLocation())) {
                request.source().sort(SortBuilders.
                        geoDistanceSort("location", new GeoPoint(params.getLocation()))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS)
                );


            }
            //发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return getHits(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Map<String, List<String>> filter(RequestParams params) {


        try {
            String[] filterName = {"brand", "city", "starName"};

            //1.获取request
            SearchRequest request = new SearchRequest("hotel");
            //2设置Aggregation信息
            buildAggregationRequest(filterName, request);
            //2.1设置查询信息
            buildBasicQuery(params, request);
            //3.发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //4.解析
            Aggregations aggregations = response.getAggregations();
            //4.1 创建结果map
            Map<String, List<String>> result = new HashMap<>();
            //4.2处理获取的结果
            handleResult(filterName, aggregations, result);

            //5.返回结果
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<String> listSuggestion(String prefix) {
        try {
            //1.准备request
            SearchRequest request = new SearchRequest("hotel");
            //2.准备DSL
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            //3.发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //4.解析结果
            Suggest suggest = response.getSuggest();
            //4.1分局补全查询名称，获取补全信息
            CompletionSuggestion suggestion = suggest.getSuggestion("suggestions");
            //4.3获取option
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
           List<String> list = new ArrayList<>(options.size());
            //4.3遍历
            for (CompletionSuggestion.Entry.Option option : options) {
                list.add(option.getText().string());
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = HotelMqConstants.HOTEL_DELETE_QUEUE),
            exchange = @Exchange(name = HotelMqConstants.HOTEL_EXCHANGE,type = ExchangeTypes.TOPIC),
            key = HotelMqConstants.HOTEL_DELETE_KEY
    ))
    public void deleteById(Long id) {
        try {
            // 1.准备Request      // DELETE /hotel/_doc/{id}
            DeleteRequest request = new DeleteRequest("hotel", id.toString());
            // 2.发送请求
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = HotelMqConstants.HOTEL_INSERT_QUEUE),
            exchange = @Exchange(name = HotelMqConstants.HOTEL_EXCHANGE,type = ExchangeTypes.TOPIC),
            key = HotelMqConstants.HOTEL_INSERT_KEY
    ))
    public void insertById(Long id) {
        try {
            //1.根据id查数据
            Hotel hotel= hotelService.getById(id);

            // 2.转换为HotelDoc
            HotelDoc hotelDoc = new HotelDoc(hotel);
            // 3.转JSON
            String json = JSON.toJSONString(hotelDoc);

            // 1.准备Request
            IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
            // 2.准备请求参数DSL，其实就是文档的JSON字符串
            request.source(json, XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleResult(String[] filterName, Aggregations aggregations, Map<String, List<String>> result) {

        //4.2 通过数组内容便利查询到的信息
        for (int i = 0; i < filterName.length; i++) {
            Terms keyAgg = aggregations.get(filterName[i] + "Agg");
            List<? extends Terms.Bucket> buckets = keyAgg.getBuckets();
            List<String> message = new ArrayList<>();
            for (Terms.Bucket bucket : buckets) {
                String key = bucket.getKeyAsString();
                message.add(key);
            }
            //4.3存储到map
                result.put(filterName[i], message);



        }
    }

    private static void buildAggregationRequest(String[] filterName, SearchRequest request) {
        //2.去除查询到的文本
        request.source().size(0);
        //2.1拼接聚合
        for (int i = 0; i < filterName.length; i++) {
            request.source().aggregation(AggregationBuilders
                    .terms(filterName[i] + "Agg")
                    .field(filterName[i])
                    .size(100)
            );
        }
    }

    private static void buildBasicQuery(RequestParams params, SearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //搜索框
        if (params.getKey() == null || "".equals(params.getKey())) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {

            boolQuery.must(QueryBuilders.matchQuery("all", params.getKey()));
        }
        //城市条件过滤
        if (params.getCity() != null && !"".equals(params.getCity())) {
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        //品牌条件过滤
        if (params.getBrand() != null && !"".equals(params.getBrand())) {
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        //星级条件过滤
        if (params.getStarName() != null && !"".equals(params.getStarName())) {
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        //价格条件过滤
        if (params.getMaxPrice() != null && params.getMinPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(params.getMinPrice()).lt(params.getMaxPrice()));
        }


        request.source().query(boolQuery);
    }


    private PageResult getHits(SearchResponse response) {
        //4.解析结果
        SearchHits searchHits = response.getHits();
        //4.1获取总条数
        long total = searchHits.getTotalHits().value;
        System.err.println("查询总条数是：" + total);
        SearchHit[] hits = searchHits.getHits();
        ArrayList<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                Object sortValue = sortValues[0];
                hotelDoc.setDistance(sortValue);
            }
            hotels.add(hotelDoc);

            //获取高亮结果
            /*Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("city");
                if (highlightField != null) {
                    String city = highlightField.getFragments()[0].string();
                    hotelDoc.setCity(city);
                }
            }*/
            //根据字段名获取高连结果

        }
        System.out.println(hotels);
        return new PageResult(total, hotels);
    }
}
