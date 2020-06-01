package com.htz.searchserver.controller;

import com.htz.searchserver.entity.LyricParam;
import com.htz.searchserver.repositories.LyricRepository;
import com.htz.searchserver.repositories.OriginLyricRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@RestController
@RequestMapping("/")
@Api(tags = "搜索原文/讲解信息相关接口")
public class LyricController {
    private static final String INDEX_LYRIC = "htz_lyric";
    private static final String INDEX_ORIGIN_LYRIC = "htz_origin_lyric";

    @Autowired
    RestHighLevelClient highLevelClient;

    @Autowired
    LyricRepository lyricRepository;

    @Autowired
    OriginLyricRepository originLyricRepository;

    @ApiOperation("保存讲解接口")
    @PostMapping("/lyric")
    public String saveLyric(@RequestBody LyricParam lyricParam) throws Exception {
        //System.out.println("lyric:" + lyric);
        save(lyricParam.getSutraId(), lyricParam.getItemId(), lyricParam.getTitle(), lyricParam.getContent(), INDEX_LYRIC);
        return "ok";
    }

    @ApiOperation("保存原文接口")
    @PostMapping("/origin_lyric")
    public String saveOriginLyric(@RequestBody LyricParam lyricParam) throws Exception {
        save(lyricParam.getSutraId(), lyricParam.getItemId(), lyricParam.getTitle(), lyricParam.getContent(), INDEX_ORIGIN_LYRIC);
        return "ok";
    }

    private void save(String sutraId, String itemId, String title, String content, String index) throws Exception {
        content = content.replaceAll("\n\n", "\n");
        System.out.println(content);
        String[] split = content.split("\n");
        System.out.println("length:" + split.length);
        for (int i = 0; i < split.length; i++) {
            if (TextUtils.isEmpty(split[i].trim())) continue;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("itemId", itemId);
            jsonMap.put("sutraId", sutraId);
            jsonMap.put("title", title);
            jsonMap.put("time", split[i].substring(1, split[i].indexOf("]")));
            jsonMap.put("content", split[i].substring(split[i].indexOf("]") + 1));

            IndexRequest indexRequest = new IndexRequest(index)
                    .id(itemId + "_" + (i + 1)).source(jsonMap); //以Map形式提供的文档源，可自动转换为JSON格式

            IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @ApiOperation("搜索接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchStr", value = "搜索内容", required = true)
    })
    @PostMapping("/get")
    public List searchLyric(@RequestBody String searchStr) throws Exception {
        List searchResult = new ArrayList();
        System.out.println("searchStr:" + searchStr);
        long time = System.currentTimeMillis();
        System.out.println("searchLyric begin...........");
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(INDEX_LYRIC);
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("content", searchStr));
        searchSourceBuilder.size(1000);

        //配置标题高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder(); //生成高亮查询器
        //highlightBuilder.field("title");      //高亮查询字段
        highlightBuilder.field("content");    //高亮查询字段
        //highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
        highlightBuilder.preTags("<span style=\"color:red\">");   //高亮设置
        highlightBuilder.postTags("</span>");

        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT); //通过发送初始搜索请求来初始化搜索上下文
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        System.out.println("searchLyric length:" + searchHits.length);
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            //System.out.println(searchHit.getSourceAsString());
            Map<String, Object> searchHitSourceAsMap = searchHit.getSourceAsMap();

            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField contentField = highlightFields.get("content");

            if(contentField != null) {
                Text[] fragments = contentField.fragments();
                String name = "";
                for (Text text : fragments) {
                    name += text;
                }
                searchHitSourceAsMap.put("content", name);   //高亮字段替换掉原本的内容
            }

            searchResult.add(searchHitSourceAsMap);



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
        System.out.println("searchLyric end:" + succeeded + " cost:" + (System.currentTimeMillis() - time));

        return searchResult;
    }

    @ApiOperation("删除讲解接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "音频ID", required = true)
    })
    @PostMapping("/delete_lyric")
    public void deleteLyric(String itemId) throws IOException {
        deleteByIndex(INDEX_LYRIC, itemId);
    }

    @ApiOperation("删除原文接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "音频ID", required = true)
    })
    @PostMapping("/delete_origin_lyric")
    public void deleteOriginLyric(String itemId) throws IOException {
        deleteByIndex(INDEX_ORIGIN_LYRIC, itemId);
    }

    private void deleteByIndex(String index, String itemId) throws IOException{
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        // 更新时版本冲突
        request.setConflicts("proceed");
        // 设置查询条件，第一个参数是字段名，第二个参数是字段的值
        request.setQuery(new TermQueryBuilder("itemId", itemId));
        // 批次大小
        request.setBatchSize(1000);
        // 并行
        request.setSlices(2);
        // 使用滚动参数来控制“搜索上下文”存活的时间
        request.setScroll(TimeValue.timeValueMinutes(10));
        // 超时
        request.setTimeout(TimeValue.timeValueMinutes(2));
        // 刷新索引
        request.setRefresh(true);
        BulkByScrollResponse deleteResponse = highLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        System.out.println(deleteResponse);
    }
}
