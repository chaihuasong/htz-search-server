package com.htz.searchserver.controller;

import com.htz.searchserver.entity.IDQuery;
import com.htz.searchserver.entity.LyricParam;
import com.htz.searchserver.entity.OriginLyricParam;
import com.htz.searchserver.entity.PageQuery;
import com.htz.searchserver.repositories.LyricRepository;
import com.htz.searchserver.repositories.OriginLyricRepository;
import com.htz.searchserver.utils.Result;
import com.htz.searchserver.utils.TextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("elasticsearchClient")
    RestHighLevelClient highLevelClient;

    @Autowired
    LyricRepository lyricRepository;

    @Autowired
    OriginLyricRepository originLyricRepository;

    @ApiOperation("保存讲解接口")
    @PostMapping("/save")
    public String saveLyric(@RequestBody LyricParam lyricParam) throws Exception {
        if (TextUtils.isEmpty(lyricParam.getId())) return "id empty!";
        if (TextUtils.isEmpty(lyricParam.getContent())) return "content empty!";
        if (TextUtils.isEmpty(lyricParam.getSutraTitle())) return "sutra title empty!";
        if (TextUtils.isEmpty(lyricParam.getTitle())) return "title empty!";
        save(lyricParam.getId(), lyricParam.getSutraTitle(), lyricParam.getTitle(), lyricParam.getContent(), INDEX_LYRIC);
        return Result.ok();
    }

    @ApiOperation("保存原文接口")
    @PostMapping("/save_origin")
    public String saveOriginLyric(@RequestBody OriginLyricParam originLyricParam) throws Exception {
        if (TextUtils.isEmpty(originLyricParam.getId())) return Result.error("id empty!");
        if (TextUtils.isEmpty(originLyricParam.getContent())) return Result.error("content empty!");
        if (TextUtils.isEmpty(originLyricParam.getSutraTitle())) return Result.error("sutra title empty!");
        if (TextUtils.isEmpty(originLyricParam.getTitle())) return Result.error("title empty!");
        saveOrigin(originLyricParam.getId(), originLyricParam.getSutraTitle(), originLyricParam.getTitle(),
                originLyricParam.getContent(), originLyricParam.getUrl1(), originLyricParam.getUrl2(), INDEX_ORIGIN_LYRIC);
        return Result.ok();
    }

    private void saveOrigin(String id, String sutraTitle, String title, String content, String url1, String url2, String index) throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("id", id);
        jsonMap.put("sutraTitle", sutraTitle);
        jsonMap.put("title", title);
        jsonMap.put("content", content);
        jsonMap.put("url1", url1);
        jsonMap.put("url2", url2);

        IndexRequest indexRequest = new IndexRequest(index)
                .id(id).source(jsonMap); //以Map形式提供的文档源，可自动转换为JSON格式

        IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    private void save(String id, String sutraTitle, String title, String content, String index) throws Exception {
        content = content.replaceAll("\n", "");
        if (!content.contains("[")) return;
        String[] split = content.split("\\[");
        System.out.println("length:" + split.length);
        for (int i = 0; i < split.length; i++) {
            if (TextUtils.isEmpty(split[i].trim())
                    || !split[i].trim().contains("]")
                    || (split[i].length() == (split[i].indexOf("]") + 1)))
                continue;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("id", id);
            jsonMap.put("sutraTitle", sutraTitle);
            jsonMap.put("title", title);
            jsonMap.put("time", split[i].substring(0, split[i].indexOf("]")));
            jsonMap.put("content", split[i].substring(split[i].indexOf("]") + 1));

            IndexRequest indexRequest = new IndexRequest(index)
                    .id(id + "_" + i).source(jsonMap); //以Map形式提供的文档源，可自动转换为JSON格式

            IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @ApiOperation("搜索讲解接口")
    @PostMapping("/search")
    public List searchLyric(@RequestBody String searchStr) throws Exception {
        return searchByIndex(INDEX_LYRIC, searchStr);
    }

    @ApiOperation("搜索原文接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchStr", value = "搜索内容", required = true)
    })
    @PostMapping("/search_origin")
    public List searchOriginLyric(@RequestBody String searchStr) throws Exception {
        return searchByIndex(INDEX_ORIGIN_LYRIC, searchStr);
    }

    @ApiOperation("根据id查找讲解")
    @PostMapping("/get_lyric")
    public List getLyricById(@RequestBody IDQuery param) throws Exception {
        return getLyricByPage(INDEX_LYRIC, null, param.getId());
    }

    @ApiOperation("根据id查找原文")
    @PostMapping("/get_origin")
    public List getOriginLyricById(@RequestBody IDQuery param) throws Exception {
        return getLyricByPage(INDEX_ORIGIN_LYRIC, null, param.getId());
    }

    @ApiOperation("获取原文接口")
    @PostMapping("/get_origins")
    public List getOriginLyric(@RequestBody PageQuery pageQuery) throws Exception {
        return getLyricByPage(INDEX_ORIGIN_LYRIC, pageQuery, null);
    }

    private List getLyricByPage(String index, PageQuery pageQuery, String id) throws Exception {
        List result = new ArrayList();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (id == null) {
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchSourceBuilder.from(pageQuery.getPage_index() * pageQuery.getPage_size());
            searchSourceBuilder.size(pageQuery.getPage_size());
        } else {
            searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
        }

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            Map<String, Object> searchHitSourceAsMap = searchHit.getSourceAsMap();
            searchHitSourceAsMap.put("id", searchHit.getId());
            result.add(searchHitSourceAsMap);
            System.out.println("searchHitSourceAsMap:" + searchHitSourceAsMap);
        }
        System.out.println("result:" + result);
        return result;
    }

    private List searchByIndex(String index, String searchStr) throws Exception {
        List searchResult = new ArrayList();
        System.out.println("searchStr:" + searchStr);
        long time = System.currentTimeMillis();
        System.out.println("searchLyric begin...........");
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(index);
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

            if (contentField != null) {
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
            @ApiImplicitParam(name = "id", value = "讲解ID", required = true)
    })
    @PostMapping("/delete")
    public void deleteLyric(@RequestBody IDQuery param) throws IOException {
        deleteByIndex(INDEX_LYRIC, param.getId());
    }

    @ApiOperation("删除原文接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "原文ID", required = true)
    })
    @PostMapping("/delete_origin")
    public void deleteOriginLyric(@RequestBody IDQuery param) throws IOException {
        DeleteRequest request = new DeleteRequest(INDEX_ORIGIN_LYRIC, param.getId());
        DeleteResponse deleteResponse = highLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(deleteResponse);
    }

    private void deleteByIndex(String index, String id) throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        // 更新时版本冲突
        request.setConflicts("proceed");
        request.setQuery(new TermQueryBuilder("id", id));
        // 批次大小
        request.setBatchSize(2000);
        // 并行
        request.setSlices(2);
        // 使用滚动参数来控制“搜索上下文”存活的时间
        request.setScroll(TimeValue.timeValueMinutes(10));
        // 超时
        request.setTimeout(TimeValue.timeValueMinutes(2));
        // 刷新索引
        request.setRefresh(true);
        BulkByScrollResponse deleteResponse = highLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.getStatus().getDeleted());
    }

    private void updateByIndex(String index, String id) throws IOException {
        UpdateByQueryRequest request = new UpdateByQueryRequest(index);
        // 更新时版本冲突
        request.setConflicts("proceed");
        // 设置查询条件，第一个参数是字段名，第二个参数是字段的值
        request.setQuery(new TermQueryBuilder("id", id));
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
        BulkByScrollResponse updateResponse = highLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        System.out.println(updateResponse.getStatus().getUpdated());
    }
}
