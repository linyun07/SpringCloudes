package com.linyun.hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.linyun.hotel.mapper.HotelMapper;
import com.linyun.hotel.pojo.Hotel;
import com.linyun.hotel.pojo.HotelDoc;
import com.linyun.hotel.pojo.PageResult;
import com.linyun.hotel.pojo.RequestParams;
import com.linyun.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * @author linyun
 */
@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
    @Resource
    private RestHighLevelClient client;

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
            if (params.getLocation()!=null && !"".equals(params.getLocation())){
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
            if (sortValues.length>0){
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
