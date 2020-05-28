package com.htz.searchserver;

import org.apache.http.util.TextUtils;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static java.util.Collections.singletonMap;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.IMMEDIATE;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@SpringBootTest
class HtzSearchServerApplicationTests {
    @Autowired
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
    public void test123() {
        String lyric = "[00:00.28]幸福内心禅\\r\\n\\r\\n[00:10.28]各位听众朋友大家好！\\r\\n[00:20.28]欢迎收听《幸福内心禅》第441集。\\r\\n[00:50.28]有些事情是我们可以决定的，\\r\\n[00:58.93]老天爷把很多的决定权交在我们手上，\\r\\n[01:03.29]比如说只要我们多努力，\\r\\n[01:06.71]你收获一定会多一点。\\r\\n[01:10.32]你只要对人更好，\\r\\n[01:12.64]然后你得到的回馈会更多，\\r\\n[01:15.49]你交到朋友也会更多。\\r\\n[01:17.74]这些是我们可以努力做的事，\\r\\n[01:21.86]人和比地利还强，\\r\\n[01:24.48]地利比天时还强，\\r\\n[01:26.80]当然天时地利人和都很重要，\\r\\n[01:29.20]但是最重要的是什么呢？\\r\\n[01:31.61]是人和。\\r\\n[01:34.22]好比说这样你是可以努力的，\\r\\n[01:36.75]好比天时（春夏秋冬一定会来的呀），\\r\\n[01:39.57]你不找它它也会来，这就是命哪。\\r\\n[01:43.73]天定的是无可改变的，\\r\\n[01:47.70]地理上的事情也不是你能改变的，\\r\\n[01:51.64]你生长在江南或江北的人是不一样的，\\r\\n[01:54.49]你生长在沙漠的人也是不一样的。\\r\\n[01:56.42]但是如果你的人和很好。\\r\\n[01:59.27]你在春季、夏季、秋季、\\r\\n[02:02.33]冬季，都有人帮你。\\r\\n[02:05.85]不管你在沙漠地带也好，\\r\\n[02:07.84]还是在江南地带富庶的地方也好，\\r\\n[02:12.25]也是会有许多人帮你的。\\r\\n[02:14.58]这就是人和比天时、地利强的地方。\\r\\n[02:19.49]常常有人说那命运是不是定的，\\r\\n[02:23.43]那很多人会告诉你不是。\\r\\n[02:25.08]说是人定胜天，\\r\\n[02:27.46]要不就是另外一边人告诉你，\\r\\n[02:30.07]那个是早八辈子前就定了的。\\r\\n[02:36.63]会走入两个极端，\\r\\n[02:38.35]任何一辈子人都有一半以上事情是可以由自己决定的，\\r\\n[02:43.16]但是也有一些事情是你无法决定的。\\r\\n[02:45.56]我们不得不承认这个事实，\\r\\n[02:47.52]就像春夏秋冬来找你，\\r\\n[02:50.16]哪天下了一场暴风雪，\\r\\n[02:52.33]结果你努力了很久的农作物泡汤了，\\r\\n[02:55.17]这不是你努力就有结果，\\r\\n[02:59.54]这个叫作命，你努力都没有用。\\r\\n[03:02.93]但是常态来说努力有没有用？\\r\\n[03:05.10]有用啊，你比隔壁农夫更努力、更细心，\\r\\n[03:10.53]你的收成一定比较好。\\r\\n[03:14.03]那命可不可以改呢？\\r\\n[03:16.80]可以努力的部分，当然可以改，\\r\\n[03:19.65]不能努力的部分任你怎么努力都没用啦。\\r\\n[03:22.97]比如说：突然来了一个台风，\\r\\n[03:26.26]这怎么改呀?我请问你，\\r\\n[03:28.43]气象专家来了怎么改，\\r\\n[03:30.23]只能预报而已，没得改。\\r\\n[03:32.66]似类像这样的例子，\\r\\n[03:34.54]你生长在谁家，\\r\\n[03:35.71]被谁生出来，\\r\\n[03:37.41]有办法改吗？\\r\\n[03:39.01]没有办法改法。\\r\\n[03:40.55]我们要说的是，\\r\\n[03:42.43]命运里面，有些可以改，有些不能改。\\r\\n[03:46.22]那不能改的就叫作命。\\r\\n[04:06.38]那圣贤君子和凡夫俗子不一样的是什么？\\r\\n[04:11.22]圣贤君子是能改变的一定要努力，\\r\\n[04:15.24]不能改变的就认命。\\r\\n[04:21.85]而凡夫俗子，是怎样呢？\\r\\n[04:24.54]我们可以改变的，\\r\\n[04:26.24]也许会不努力也许会巧取豪夺。\\r\\n[04:31.20]正人君子，他能够改变的会很努力，\\r\\n[04:35.09]他对得起自己，对得起别人。\\r\\n[04:38.70]他是以很公平，开阔，光明磊落的方式去努力，\\r\\n[04:44.47]他不会想要多得一点，然后就对不起人。\\r\\n[04:48.83]不能努力的是尽力了，\\r\\n[04:51.76]还是会碰上恶运，一笑置之，\\r\\n[04:55.62]坦然接受，然后会历事炼心。\\r\\n[05:00.12]这个就是圣贤君子跟我们不一样的地方。\\r\\n[05:03.38]讲到这里，我举一段例子孔子与师徒对答：\\r\\n[05:12.18]孔子和师徒被重兵包围，七日不食，\\r\\n[05:26.21]弟子们都饿的头晕眼花，\\r\\n[05:37.73]然而孔子却读书习礼不倦，\\r\\n[05:42.83]孔子还在那里读诗、拉胡琴、唱歌。\\r\\n[05:54.48]弟子就开始有怨言了，\\r\\n[05:57.30]弟子服侍师夫一辈子很恭敬，\\r\\n[06:01.24]结果饿了七天的时候，\\r\\n[06:02.94]个个都想叛变，\\r\\n[06:05.66]怒目相视，可孔子还在那边拉胡琴，唱歌，读书，\\r\\n[06:11.56]你看气不气人，\\r\\n[06:14.54]子路进谏曰：为善者天报之以福，\\r\\n[06:26.40]为不善者天报之以祸。\\r\\n[06:32.17]善有善报，恶有恶报。\\r\\n[06:35.65]今夫子积德怀义，行之久矣，奚居之穷也？\\r\\n[06:41.55]他说孔夫子你累积仁德这么久了，\\r\\n[06:44.63]你告诉我们说善有善报，恶有恶报，\\r\\n[06:47.09]也都是您平常所说的。\\r\\n[06:48.76]我看您一辈子都在积善，\\r\\n[06:50.56]心地这么善良，\\r\\n[06:52.65]我已经跟您几十年了，\\r\\n[06:57.17]意思说如果这是个宇宙定律的话，\\r\\n[07:02.26]我们不应该得到善报吗？\\r\\n[07:03.83]为什么我们现在快饿死了呢？\\r\\n[07:05.48]还被重兵包围着，他们还拿着武器要我们的性命。\\r\\n[07:16.68]我在想，现在恍然大悟，\\r\\n[07:27.60]孔夫子你是不是还有什么没料到、没想好，没做的，\\r\\n[07:33.77]除了善有善报，恶有恶报，\\r\\n[07:36.62]你不是一直说这是宇宙定侓吗？\\r\\n[07:38.24]为什么我们到现在没有善报，\\r\\n[07:39.96]到哪里都被排挤，\\r\\n[07:42.73]现在还被重兵包围，饿个半死。\\r\\n[07:48.37]还是你私下有做什么不对的，\\r\\n[07:51.35]不然怎么会弄到这般田地呢？\\r\\n[07:58.11]为什么这个困穷总是离不开我们呢？\\r\\n[08:02.92]我跟你求学问，\\r\\n[08:04.46]我到哪都被欺负，到哪都被歧视，\\r\\n[08:08.04]到哪都被酸言酸语。\\r\\n[08:10.71]各位你要知道，\\r\\n[08:12.22]孔子在战国时代，\\r\\n[08:13.71]他是个大学问家，\\r\\n[08:15.41]当时大学问家很多，\\r\\n[08:17.11]然后大多数都因为时代乱而隐居了。\\r\\n[08:21.60]只有孔子周游列国，\\r\\n[08:22.93]想要找到一个救众生天下的办法，\\r\\n[08:26.35]而大家都酸言酸笑，\\r\\n[08:29.23]就笑他是为了求名求利。\\r\\n[08:31.86]然后到处都碰到困难。\\r\\n[08:35.50]善有善报，\\r\\n[08:36.54]恶有恶报，是铁的定侓，\\r\\n[08:37.87]为什么你行善这么久，\\r\\n[08:39.21]积善这么久，读这么多书，我们总被欺负呢？\\r\\n[08:43.36]是不是还有什么德性欠缺的地方？\\r\\n[08:53.86]孔子说：求学的目的不是为了要名利遂顺，\\r\\n[09:06.79]不是为了要四通八达。\\r\\n[09:13.61]各位你现在读大学是为什么？\\r\\n[09:19.98]是为了充实知识吗？\\r\\n[09:21.97]有，但并不多，大部分是为什么？\\r\\n[09:25.05]为了找个好工作，\\r\\n[09:26.64]以后好养家糊口、买车买房，\\r\\n[09:29.05]为什么你很努力，\\r\\n[09:30.74]这边送红包，那边送红包。\\r\\n[09:33.20]就是想买一条路，到处亨通嘛。\\r\\n[09:36.62]你做什么事不是想到处亨通？\\r\\n[09:40.98]各位一定会想，那到底是为什么 ？\\r\\n[09:47.20]那我学学问这么多，\\r\\n[09:48.80]如果不是为了更通达，\\r\\n[09:50.65]那我是为什么？\\r\\n[09:52.22]孔子说：\\r\\n[09:53.97]“为穷而不困，忧而志不衰也。”\\r\\n[10:12.15]靠山山倒，靠水水干，\\r\\n[10:16.17]才叫做穷。\\r\\n[10:19.49]买个股票也被吞掉，\\r\\n[10:23.80]你不买没事，你一买就下跌，\\r\\n[10:25.63]连房子都卖了，\\r\\n[10:29.36]这么样窘迫的境遇之下，\\r\\n[10:32.92]它的内心怡然自得、轻轻松松。\\r\\n[10:38.30]该怎么做就怎么做。各位如果你现在境遇很窘迫，\\r\\n[10:43.89]你嫁个先生或讨个老婆对你很不好，\\r\\n[10:48.35]或者你生个儿子对你也很不好，\\r\\n[10:49.79]这个对也不听话，那个也不听话，\\r\\n[10:51.46]投资这个也失利，投资那个也失利。\\r\\n[10:53.06]也没车，也没房，\\r\\n[10:55.49]你会不会说我这一辈子在干嘛。\\r\\n[10:58.73]你会不会许多哀伤，\\r\\n[11:01.73]你会不会有许多埋怨。\\r\\n[11:07.37]这就是穷而困，\\r\\n[11:09.91]你的心被困住了，你喘不过气，\\r\\n[11:12.05]你一点点轻松的空间都没有。\\r\\n[11:14.92]孔夫子被众兵包围，都可以拉胡琴，\\r\\n[11:17.87]唱歌，还可以读诗书，还可以习礼。\\r\\n[11:21.66]你看他轻不轻松，你该说怎么这么轻松，\\r\\n[11:26.57]各位你要知道不仅是有重兵，\\r\\n[11:29.97]还有徒弟都快叛变了喔。\\r\\n[11:31.95]一个一个来指责他，\\r\\n[11:33.73]他还是一派轻松，\\r\\n[11:35.45]因为他一命交天，\\r\\n[11:37.86]无可改变嘛。\\r\\n[11:41.38]是为苍生奔波，做的事都是对的，\\r\\n[11:43.58]他教给学生正确的\\r\\n[11:45.64]事情，我还是碰到这些，\\r\\n[11:47.81]老天爷要我的命那就拿去吧。\\r\\n[11:51.18]孔子的心路历程，\\r\\n[11:55.38]但他并没有一已的得失而担忧过。\\r\\n[11:58.96]外界的境遇挑战，\\r\\n[12:00.14]境遇非常窘迫，\\r\\n[12:01.37]但是他的内心没有一已的得失而担忧。\\r\\n[12:06.67]忧而志不衰也，\\r\\n[12:09.75]求学是为什么，为穷而不困，\\r\\n[12:11.66]为忧而志不衰也。\\r\\n[12:13.64]你看这么忧患的情况，他的志气，\\r\\n[12:15.94]他要行遍天下，他要去施行他的仁道，\\r\\n[12:23.91]他要去教育他的徒弟，\\r\\n[12:25.37]他那种志气从不衰败。\\r\\n[12:28.59]外面重兵包围，\\r\\n[12:29.94]他还在读书，还在教书，还在习礼。\\r\\n[12:33.00]这个学问对孔子有没有用？\\r\\n[12:40.08]那真是有用，如果我们在那种境遇之下，\\r\\n[12:41.65]如果你是孔子的弟子，\\r\\n[12:45.72]应该叛变了，如果没有叛变也应该偷溜了吧。\\r\\n[12:50.35]然后又有子路赶在前面说，\\r\\n[12:52.36]你不是说善有善报，恶有恶报吗？\\r\\n[12:54.53]怎么今天搞成这样？\\r\\n[12:56.30]下面的人就鼓掌鼓掌，\\r\\n[12:58.05]想我们会不会跟错人了，\\r\\n[13:02.52]结果你的境遇一窘迫，\\r\\n[13:04.14]你的内心所学的全部没用。\\r\\n[13:07.64]外面只要忧患一发生，\\r\\n[13:09.57]你就开始气馁，\\r\\n[13:11.35]你看孔子越是忧患发生了，\\r\\n[13:13.96]就在眼前就是这么危急，\\r\\n[13:16.76]他还是一样一派轻松。\\r\\n[14:12.97]碰到困境就叛变的人，\\r\\n[14:15.27]碰到命运就叛变的人，多不多？\\r\\n[14:17.91]到处都是，一箩筐，随便抓都一大把。\\r\\n[14:20.26]不是吗？\\r\\n[14:22.04]学问是为什么？\\r\\n[14:26.09]是为了坚定你的看法。\\r\\n[14:29.51]做对的事不能改变，\\r\\n[14:31.70]古人说尽已之谓忠。\\r\\n[14:34.08]这个孔子对老天爷真是忠心耿耿，\\r\\n[14:37.45]对自己的命运忠心耿耿，\\r\\n[14:39.33]对自己的人格忠心耿耿，\\r\\n[14:41.37]尽已，尽上自己的努力不求回报。\\r\\n[14:47.27]我们今天做任何事情都是为了回报，\\r\\n[14:49.94]一没有回报就开始生气，\\r\\n[14:52.02]就开始动念，就开始叛变。对不对？\\r\\n[14:56.20]孔子是我能做的我就做，\\r\\n[15:02.06]我能努力的我就努力，\\r\\n[15:03.52]我不能努力的呢就一派轻松。\\r\\n[15:06.47]有没有改变他的志向呢？\\r\\n[15:08.04]没有，我觉得这很重要，\\r\\n[15:10.89]我这一辈子钦佩孔子。\\r\\n[15:12.58]不是没有原因的，\\r\\n[15:13.89]随便一个章节拿来看，\\r\\n[15:15.43]好好的揣摩，都会深深感动。\\r\\n[15:18.62]真正有学问的人，\\r\\n[15:20.45]真正有志气人表现出来的就是这样。\\r\\n[15:33.95]你的境遇一窘迫，你就容易气馁。\\r\\n";
        //lyric = lyric.replaceAll("\\r", "");
        String[] split = lyric.split("\\\\r\\\\n");
        System.out.println("huasong test123 split length:"+ split.length);
    }

    @Test
    public void createData() throws Exception {
        for(int i = 0; i < 1; i++) {
            createMapping(i);
        }
    }

    @Test
    public void deleteDataById() throws Exception {
        for (int i = 0; i < 250; i++) {
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_LYRIC, "test_item_id_" + i);
            DeleteResponse deleteResponse = highLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            System.out.println(deleteResponse);
        }
    }

    @Test
    public void deleteDataByQuery() throws Exception {
        DeleteByQueryRequest request = new DeleteByQueryRequest(INDEX_LYRIC);
        // 更新时版本冲突
        request.setConflicts("proceed");
        // 设置查询条件，第一个参数是字段名，第二个参数是字段的值
        request.setQuery(new TermQueryBuilder("itemId", "test_item_id"));
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

    @Test
    public void createMapping(int index) throws Exception {
        String lyric = "[00:00.28]幸福内心禅\\r\\n\\r\\n[00:10.28]各位听众朋友大家好！\\r\\n[00:20.28]欢迎收听《幸福内心禅》第441集。\\r\\n[00:50.28]有些事情是我们可以决定的，\\r\\n[00:58.93]老天爷把很多的决定权交在我们手上，\\r\\n[01:03.29]比如说只要我们多努力，\\r\\n[01:06.71]你收获一定会多一点。\\r\\n[01:10.32]你只要对人更好，\\r\\n[01:12.64]然后你得到的回馈会更多，\\r\\n[01:15.49]你交到朋友也会更多。\\r\\n[01:17.74]这些是我们可以努力做的事，\\r\\n[01:21.86]人和比地利还强，\\r\\n[01:24.48]地利比天时还强，\\r\\n[01:26.80]当然天时地利人和都很重要，\\r\\n[01:29.20]但是最重要的是什么呢？\\r\\n[01:31.61]是人和。\\r\\n[01:34.22]好比说这样你是可以努力的，\\r\\n[01:36.75]好比天时（春夏秋冬一定会来的呀），\\r\\n[01:39.57]你不找它它也会来，这就是命哪。\\r\\n[01:43.73]天定的是无可改变的，\\r\\n[01:47.70]地理上的事情也不是你能改变的，\\r\\n[01:51.64]你生长在江南或江北的人是不一样的，\\r\\n[01:54.49]你生长在沙漠的人也是不一样的。\\r\\n[01:56.42]但是如果你的人和很好。\\r\\n[01:59.27]你在春季、夏季、秋季、\\r\\n[02:02.33]冬季，都有人帮你。\\r\\n[02:05.85]不管你在沙漠地带也好，\\r\\n[02:07.84]还是在江南地带富庶的地方也好，\\r\\n[02:12.25]也是会有许多人帮你的。\\r\\n[02:14.58]这就是人和比天时、地利强的地方。\\r\\n[02:19.49]常常有人说那命运是不是定的，\\r\\n[02:23.43]那很多人会告诉你不是。\\r\\n[02:25.08]说是人定胜天，\\r\\n[02:27.46]要不就是另外一边人告诉你，\\r\\n[02:30.07]那个是早八辈子前就定了的。\\r\\n[02:36.63]会走入两个极端，\\r\\n[02:38.35]任何一辈子人都有一半以上事情是可以由自己决定的，\\r\\n[02:43.16]但是也有一些事情是你无法决定的。\\r\\n[02:45.56]我们不得不承认这个事实，\\r\\n[02:47.52]就像春夏秋冬来找你，\\r\\n[02:50.16]哪天下了一场暴风雪，\\r\\n[02:52.33]结果你努力了很久的农作物泡汤了，\\r\\n[02:55.17]这不是你努力就有结果，\\r\\n[02:59.54]这个叫作命，你努力都没有用。\\r\\n[03:02.93]但是常态来说努力有没有用？\\r\\n[03:05.10]有用啊，你比隔壁农夫更努力、更细心，\\r\\n[03:10.53]你的收成一定比较好。\\r\\n[03:14.03]那命可不可以改呢？\\r\\n[03:16.80]可以努力的部分，当然可以改，\\r\\n[03:19.65]不能努力的部分任你怎么努力都没用啦。\\r\\n[03:22.97]比如说：突然来了一个台风，\\r\\n[03:26.26]这怎么改呀?我请问你，\\r\\n[03:28.43]气象专家来了怎么改，\\r\\n[03:30.23]只能预报而已，没得改。\\r\\n[03:32.66]似类像这样的例子，\\r\\n[03:34.54]你生长在谁家，\\r\\n[03:35.71]被谁生出来，\\r\\n[03:37.41]有办法改吗？\\r\\n[03:39.01]没有办法改法。\\r\\n[03:40.55]我们要说的是，\\r\\n[03:42.43]命运里面，有些可以改，有些不能改。\\r\\n[03:46.22]那不能改的就叫作命。\\r\\n[04:06.38]那圣贤君子和凡夫俗子不一样的是什么？\\r\\n[04:11.22]圣贤君子是能改变的一定要努力，\\r\\n[04:15.24]不能改变的就认命。\\r\\n[04:21.85]而凡夫俗子，是怎样呢？\\r\\n[04:24.54]我们可以改变的，\\r\\n[04:26.24]也许会不努力也许会巧取豪夺。\\r\\n[04:31.20]正人君子，他能够改变的会很努力，\\r\\n[04:35.09]他对得起自己，对得起别人。\\r\\n[04:38.70]他是以很公平，开阔，光明磊落的方式去努力，\\r\\n[04:44.47]他不会想要多得一点，然后就对不起人。\\r\\n[04:48.83]不能努力的是尽力了，\\r\\n[04:51.76]还是会碰上恶运，一笑置之，\\r\\n[04:55.62]坦然接受，然后会历事炼心。\\r\\n[05:00.12]这个就是圣贤君子跟我们不一样的地方。\\r\\n[05:03.38]讲到这里，我举一段例子孔子与师徒对答：\\r\\n[05:12.18]孔子和师徒被重兵包围，七日不食，\\r\\n[05:26.21]弟子们都饿的头晕眼花，\\r\\n[05:37.73]然而孔子却读书习礼不倦，\\r\\n[05:42.83]孔子还在那里读诗、拉胡琴、唱歌。\\r\\n[05:54.48]弟子就开始有怨言了，\\r\\n[05:57.30]弟子服侍师夫一辈子很恭敬，\\r\\n[06:01.24]结果饿了七天的时候，\\r\\n[06:02.94]个个都想叛变，\\r\\n[06:05.66]怒目相视，可孔子还在那边拉胡琴，唱歌，读书，\\r\\n[06:11.56]你看气不气人，\\r\\n[06:14.54]子路进谏曰：为善者天报之以福，\\r\\n[06:26.40]为不善者天报之以祸。\\r\\n[06:32.17]善有善报，恶有恶报。\\r\\n[06:35.65]今夫子积德怀义，行之久矣，奚居之穷也？\\r\\n[06:41.55]他说孔夫子你累积仁德这么久了，\\r\\n[06:44.63]你告诉我们说善有善报，恶有恶报，\\r\\n[06:47.09]也都是您平常所说的。\\r\\n[06:48.76]我看您一辈子都在积善，\\r\\n[06:50.56]心地这么善良，\\r\\n[06:52.65]我已经跟您几十年了，\\r\\n[06:57.17]意思说如果这是个宇宙定律的话，\\r\\n[07:02.26]我们不应该得到善报吗？\\r\\n[07:03.83]为什么我们现在快饿死了呢？\\r\\n[07:05.48]还被重兵包围着，他们还拿着武器要我们的性命。\\r\\n[07:16.68]我在想，现在恍然大悟，\\r\\n[07:27.60]孔夫子你是不是还有什么没料到、没想好，没做的，\\r\\n[07:33.77]除了善有善报，恶有恶报，\\r\\n[07:36.62]你不是一直说这是宇宙定侓吗？\\r\\n[07:38.24]为什么我们到现在没有善报，\\r\\n[07:39.96]到哪里都被排挤，\\r\\n[07:42.73]现在还被重兵包围，饿个半死。\\r\\n[07:48.37]还是你私下有做什么不对的，\\r\\n[07:51.35]不然怎么会弄到这般田地呢？\\r\\n[07:58.11]为什么这个困穷总是离不开我们呢？\\r\\n[08:02.92]我跟你求学问，\\r\\n[08:04.46]我到哪都被欺负，到哪都被歧视，\\r\\n[08:08.04]到哪都被酸言酸语。\\r\\n[08:10.71]各位你要知道，\\r\\n[08:12.22]孔子在战国时代，\\r\\n[08:13.71]他是个大学问家，\\r\\n[08:15.41]当时大学问家很多，\\r\\n[08:17.11]然后大多数都因为时代乱而隐居了。\\r\\n[08:21.60]只有孔子周游列国，\\r\\n[08:22.93]想要找到一个救众生天下的办法，\\r\\n[08:26.35]而大家都酸言酸笑，\\r\\n[08:29.23]就笑他是为了求名求利。\\r\\n[08:31.86]然后到处都碰到困难。\\r\\n[08:35.50]善有善报，\\r\\n[08:36.54]恶有恶报，是铁的定侓，\\r\\n[08:37.87]为什么你行善这么久，\\r\\n[08:39.21]积善这么久，读这么多书，我们总被欺负呢？\\r\\n[08:43.36]是不是还有什么德性欠缺的地方？\\r\\n[08:53.86]孔子说：求学的目的不是为了要名利遂顺，\\r\\n[09:06.79]不是为了要四通八达。\\r\\n[09:13.61]各位你现在读大学是为什么？\\r\\n[09:19.98]是为了充实知识吗？\\r\\n[09:21.97]有，但并不多，大部分是为什么？\\r\\n[09:25.05]为了找个好工作，\\r\\n[09:26.64]以后好养家糊口、买车买房，\\r\\n[09:29.05]为什么你很努力，\\r\\n[09:30.74]这边送红包，那边送红包。\\r\\n[09:33.20]就是想买一条路，到处亨通嘛。\\r\\n[09:36.62]你做什么事不是想到处亨通？\\r\\n[09:40.98]各位一定会想，那到底是为什么 ？\\r\\n[09:47.20]那我学学问这么多，\\r\\n[09:48.80]如果不是为了更通达，\\r\\n[09:50.65]那我是为什么？\\r\\n[09:52.22]孔子说：\\r\\n[09:53.97]“为穷而不困，忧而志不衰也。”\\r\\n[10:12.15]靠山山倒，靠水水干，\\r\\n[10:16.17]才叫做穷。\\r\\n[10:19.49]买个股票也被吞掉，\\r\\n[10:23.80]你不买没事，你一买就下跌，\\r\\n[10:25.63]连房子都卖了，\\r\\n[10:29.36]这么样窘迫的境遇之下，\\r\\n[10:32.92]它的内心怡然自得、轻轻松松。\\r\\n[10:38.30]该怎么做就怎么做。各位如果你现在境遇很窘迫，\\r\\n[10:43.89]你嫁个先生或讨个老婆对你很不好，\\r\\n[10:48.35]或者你生个儿子对你也很不好，\\r\\n[10:49.79]这个对也不听话，那个也不听话，\\r\\n[10:51.46]投资这个也失利，投资那个也失利。\\r\\n[10:53.06]也没车，也没房，\\r\\n[10:55.49]你会不会说我这一辈子在干嘛。\\r\\n[10:58.73]你会不会许多哀伤，\\r\\n[11:01.73]你会不会有许多埋怨。\\r\\n[11:07.37]这就是穷而困，\\r\\n[11:09.91]你的心被困住了，你喘不过气，\\r\\n[11:12.05]你一点点轻松的空间都没有。\\r\\n[11:14.92]孔夫子被众兵包围，都可以拉胡琴，\\r\\n[11:17.87]唱歌，还可以读诗书，还可以习礼。\\r\\n[11:21.66]你看他轻不轻松，你该说怎么这么轻松，\\r\\n[11:26.57]各位你要知道不仅是有重兵，\\r\\n[11:29.97]还有徒弟都快叛变了喔。\\r\\n[11:31.95]一个一个来指责他，\\r\\n[11:33.73]他还是一派轻松，\\r\\n[11:35.45]因为他一命交天，\\r\\n[11:37.86]无可改变嘛。\\r\\n[11:41.38]是为苍生奔波，做的事都是对的，\\r\\n[11:43.58]他教给学生正确的\\r\\n[11:45.64]事情，我还是碰到这些，\\r\\n[11:47.81]老天爷要我的命那就拿去吧。\\r\\n[11:51.18]孔子的心路历程，\\r\\n[11:55.38]但他并没有一已的得失而担忧过。\\r\\n[11:58.96]外界的境遇挑战，\\r\\n[12:00.14]境遇非常窘迫，\\r\\n[12:01.37]但是他的内心没有一已的得失而担忧。\\r\\n[12:06.67]忧而志不衰也，\\r\\n[12:09.75]求学是为什么，为穷而不困，\\r\\n[12:11.66]为忧而志不衰也。\\r\\n[12:13.64]你看这么忧患的情况，他的志气，\\r\\n[12:15.94]他要行遍天下，他要去施行他的仁道，\\r\\n[12:23.91]他要去教育他的徒弟，\\r\\n[12:25.37]他那种志气从不衰败。\\r\\n[12:28.59]外面重兵包围，\\r\\n[12:29.94]他还在读书，还在教书，还在习礼。\\r\\n[12:33.00]这个学问对孔子有没有用？\\r\\n[12:40.08]那真是有用，如果我们在那种境遇之下，\\r\\n[12:41.65]如果你是孔子的弟子，\\r\\n[12:45.72]应该叛变了，如果没有叛变也应该偷溜了吧。\\r\\n[12:50.35]然后又有子路赶在前面说，\\r\\n[12:52.36]你不是说善有善报，恶有恶报吗？\\r\\n[12:54.53]怎么今天搞成这样？\\r\\n[12:56.30]下面的人就鼓掌鼓掌，\\r\\n[12:58.05]想我们会不会跟错人了，\\r\\n[13:02.52]结果你的境遇一窘迫，\\r\\n[13:04.14]你的内心所学的全部没用。\\r\\n[13:07.64]外面只要忧患一发生，\\r\\n[13:09.57]你就开始气馁，\\r\\n[13:11.35]你看孔子越是忧患发生了，\\r\\n[13:13.96]就在眼前就是这么危急，\\r\\n[13:16.76]他还是一样一派轻松。\\r\\n[14:12.97]碰到困境就叛变的人，\\r\\n[14:15.27]碰到命运就叛变的人，多不多？\\r\\n[14:17.91]到处都是，一箩筐，随便抓都一大把。\\r\\n[14:20.26]不是吗？\\r\\n[14:22.04]学问是为什么？\\r\\n[14:26.09]是为了坚定你的看法。\\r\\n[14:29.51]做对的事不能改变，\\r\\n[14:31.70]古人说尽已之谓忠。\\r\\n[14:34.08]这个孔子对老天爷真是忠心耿耿，\\r\\n[14:37.45]对自己的命运忠心耿耿，\\r\\n[14:39.33]对自己的人格忠心耿耿，\\r\\n[14:41.37]尽已，尽上自己的努力不求回报。\\r\\n[14:47.27]我们今天做任何事情都是为了回报，\\r\\n[14:49.94]一没有回报就开始生气，\\r\\n[14:52.02]就开始动念，就开始叛变。对不对？\\r\\n[14:56.20]孔子是我能做的我就做，\\r\\n[15:02.06]我能努力的我就努力，\\r\\n[15:03.52]我不能努力的呢就一派轻松。\\r\\n[15:06.47]有没有改变他的志向呢？\\r\\n[15:08.04]没有，我觉得这很重要，\\r\\n[15:10.89]我这一辈子钦佩孔子。\\r\\n[15:12.58]不是没有原因的，\\r\\n[15:13.89]随便一个章节拿来看，\\r\\n[15:15.43]好好的揣摩，都会深深感动。\\r\\n[15:18.62]真正有学问的人，\\r\\n[15:20.45]真正有志气人表现出来的就是这样。\\r\\n[15:33.95]你的境遇一窘迫，你就容易气馁。\\r\\n";
        lyric = lyric.replaceAll("\\\\r\\\\n", "\\\\n");
        String[] split = lyric.split("\\\\n");
        for (int i = 0; i < split.length; i++) {
            if (TextUtils.isEmpty(split[i].trim())) continue;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("itemId", "test_item_id");
            jsonMap.put("sutraId", "test_sutra_id");
            jsonMap.put("title", "幸福内心禅第61集");
            jsonMap.put("time", split[i].substring(1, split[i].indexOf("]")));
            jsonMap.put("content", split[i].substring(split[i].indexOf("]") + 1));

            IndexRequest indexRequest = new IndexRequest(INDEX_LYRIC)
                    .id("test_item_id_" + i).source(jsonMap); //以Map形式提供的文档源，可自动转换为JSON格式


            IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
        System.out.println("huasong createMapping index:" + index + " done.... length:"+ split.length);
        //highLevelClient.close();
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
        System.out.println("huasong length:"+searchHits.getHits().length);
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
