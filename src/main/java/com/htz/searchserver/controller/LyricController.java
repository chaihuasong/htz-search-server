package com.htz.searchserver.controller;

import com.htz.searchserver.entity.Lyric;
import com.htz.searchserver.entity.OriginLyric;
import com.htz.searchserver.repositories.LyricRepository;
import com.htz.searchserver.repositories.OriginLyricRepository;
import org.apache.http.util.TextUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static java.util.Collections.singletonMap;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@RestController
@RequestMapping("/")
public class LyricController {

    @Autowired
    RestHighLevelClient highLevelClient;

    @Autowired
    LyricRepository lyricRepository;

    @Autowired
    OriginLyricRepository originLyricRepository;

    @PostMapping("/createIndex")
    public String createIndex(String name) throws Exception {
        IndexRequest request = new IndexRequest(name)
                .source(singletonMap("feature", "high-level-rest-client"))
                .setRefreshPolicy(IMMEDIATE);

        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
        return "ok";
    }

    @PostMapping("/lyric")
    public String saveLyric(@RequestBody Lyric lyric) throws Exception {
        //System.out.println("lyric:" + lyric);
        save(lyric.getSutraId(), lyric.getId(), lyric.getTitle(), lyric.getContent(), "htz_lyric");
        return "ok";
    }

    @PostMapping("/origin_lyric")
    public String saveOriginLyric(@RequestBody OriginLyric originLyric) throws Exception {
        save(originLyric.getSutraId(), originLyric.getId(), originLyric.getTitle(), originLyric.getContent(), "htz_origin_lyric");
        return "ok";
    }

    private void save(String sutraId, String itemId, String title, String content, String index) throws Exception {
        content = content.replaceAll("\\r\\n", "\\\\n");
        System.out.println(content);
        String[] split = content.split("\\\\n");
        System.out.println("length:" + split.length);
        for (int i = 0; i < split.length; i++) {
            if (TextUtils.isEmpty(split[i].trim())) continue;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("id", itemId);
            jsonMap.put("sutraId", sutraId);
            jsonMap.put("title", title);
            jsonMap.put("time", split[i].substring(1, split[i].indexOf("]")));
            jsonMap.put("content", split[i].substring(split[i].indexOf("]") + 1));

            IndexRequest indexRequest = new IndexRequest(index)
                    .id(itemId + "_" + (i + 1)).source(jsonMap); //以Map形式提供的文档源，可自动转换为JSON格式

            IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @PostMapping("/get")
    public List saveLyric(@RequestBody String searchStr) throws Exception {
        List result = new ArrayList<String>();
        System.out.println("searchStr:" + searchStr);
        long time = System.currentTimeMillis();
        System.out.println("huasong testScrollSearch begin...........");
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest("htz_lyric");
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("content", searchStr));
        searchSourceBuilder.size(1000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT); //通过发送初始搜索请求来初始化搜索上下文
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        System.out.println("huasong testScrollSearch length:" + searchHits.length);
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            Map<String, Object> searchHitSourceAsMap = searchHit.getSourceAsMap();
            result.add(searchHitSourceAsMap.toString());
            //System.out.println(searchHitSourceAsMap.get("id"));
            //System.out.println(searchHitSourceAsMap.get("Content"));
            //System.out.println(searchHitSourceAsMap.get("sutraItem"));
        }

        while (searchHits != null && searchHits.length > 0) { //通过循环调用搜索滚动api来检索所有搜索命中，直到没有文档返回
            //处理返回的搜索结果
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId); //创建一个新的搜索滚动请求，保存最后返回的滚动标识符和滚动间隔
            scrollRequest.scroll(scroll);
            searchResponse = highLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
            //System.out.println("huasong testScrollSearch2 length:" + searchHits.length);
        }
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); //完成滚动后，清除滚动上下文
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = highLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
        System.out.println("huasong testScrollSearch end:" + succeeded + " cost:" + (System.currentTimeMillis() - time));

        return result;
    }
}
