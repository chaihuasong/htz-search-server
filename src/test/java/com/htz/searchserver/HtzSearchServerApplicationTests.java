package com.htz.searchserver;

import com.htz.searchserver.utils.TextUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.singletonMap;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@SpringBootTest
class HtzSearchServerApplicationTests {
    @Autowired
    @Qualifier("elasticsearchClient")
    RestHighLevelClient highLevelClient;
    private static final String INDEX_LYRIC = "htz_lyric";
    private static final String INDEX_ORIGIN_LYRIC = "htz_origin_lyric";

    @Test
    void contextLoads() {
    }

    @Test
    public void test() throws Exception {
        IndexRequest request = new IndexRequest("htz_sutra")
                .source(singletonMap("feature", "high-level-rest-client"))
                .setRefreshPolicy(IMMEDIATE);

        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
    }

    @Test
    public void deleteDataById() throws Exception {
        for (int i = 0; i < 350; i++) {
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_LYRIC, "5ec67c56e2e76d28eb049215_" + i);
            DeleteResponse deleteResponse = highLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            System.out.println(deleteResponse);
        }
    }

    @Test
    public void getById() throws IOException {
        List result = new ArrayList();
        SearchRequest searchRequest = new SearchRequest(INDEX_ORIGIN_LYRIC);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id", "1qw4inw75g6ww"));

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
        System.out.println("result:" + result + " " + (result.size() > 0));
    }

    @Test
    public void deleteDataByQuery() throws Exception {
        DeleteByQueryRequest request = new DeleteByQueryRequest(INDEX_LYRIC);
        // 更新时版本冲突
        request.setConflicts("proceed");
        request.setQuery(new TermQueryBuilder("id", "5ed7b236e2e76d72fe7e6ee2"));
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

    @Test
    public void createMapping() throws Exception {
        String content = readFileByLines("D:\\htz\\resource\\lyrics\\lyrics\\5ed7b236e2e76d72fe7e6ee2").toString();
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
            jsonMap.put("id", "5ed7b236e2e76d72fe7e6ee2");
            jsonMap.put("sutraTitle", "幸福内心禅");
            jsonMap.put("title", "001.幸福的诀窍");
            jsonMap.put("time", split[i].substring(0, split[i].indexOf("]")));
            jsonMap.put("content", split[i].substring(split[i].indexOf("]") + 1));
            System.out.println("jsonMap:" + jsonMap);

            IndexRequest indexRequest = new IndexRequest(INDEX_LYRIC)
                    .id("5ed7b236e2e76d72fe7e6ee2_" + i).source(jsonMap); //以Map形式提供的文档源，可自动转换为JSON格式

            IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
        System.out.println("huasong createMapping index:" + INDEX_LYRIC + " done.... length:" + split.length);
        //highLevelClient.close();
    }

    @Test
    public void readFile() {
        //System.out.println(readFileByLines("/home/local/SPREADTRUM/chrison.chai/htz/lyrics/xfnxc_441.lrc"));

        String lyric = readFileByLines("/home/local/SPREADTRUM/chrison.chai/htz/lyrics/xfnxc_441.lrc").toString();
        String[] split = lyric.split("\n");
        System.out.println("length:" + split.length);
    }

    public static StringBuffer readFileByLines(String fileName) {
        StringBuffer sb = new StringBuffer();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                if (!TextUtils.isEmpty(tempString)) {
                    sb.append(tempString);
                    sb.append("\n");
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb;
    }

    @Test
    public void testScrollSearch() throws Exception {
        long time = System.currentTimeMillis();
        List searchResult = new ArrayList();
        System.out.println("huasong testScrollSearch begin...........");
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest("htz_lyric");
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("content", "改变"));
        searchSourceBuilder.size(1000);


        //配置标题高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder(); //生成高亮查询器
        highlightBuilder.field("title");      //高亮查询字段
        highlightBuilder.field("content");    //高亮查询字段
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
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
        System.out.println("huasong testScrollSearch length:" + searchHits.length);
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            Map<String, Object> searchHitSourceAsMap = searchHit.getSourceAsMap();
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
        System.out.println("huasong testScrollSearch end:" + succeeded + " cost:" + (System.currentTimeMillis() - time));
        JSONArray jsonArray = new JSONArray(searchResult);
        System.out.println(jsonArray);
    }


    @Test
    public void testSearch() throws Exception {
        SearchRequest searchRequest = new SearchRequest("htz_lyric");
        searchRequest.routing("routing");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.query(QueryBuilders.queryStringQuery("时间"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        Iterator<SearchHit> iterator = searchHits.iterator();
        System.out.println("huasong length:" + searchHits.getHits().length);
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            //System.out.println(searchHit.getSourceAsString());
            Map<String, Object> searchHitSourceAsMap = searchHit.getSourceAsMap();
            //System.out.println(searchHitSourceAsMap.get("id"));
            System.out.println(searchHitSourceAsMap.get("content"));
            //System.out.println(searchHitSourceAsMap.get("sutraItem"));
        }
    }


}
