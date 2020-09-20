package com.htz.searchserver;

import org.apache.http.util.TextUtils;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    @Test
    public void test() {
        String str = "word:love property:v meaning:爱";
        String[] strs = str.split("[ ]?\\w+:");
        System.out.println(strs[0]);
        System.out.println(strs[1]);
        System.out.println(strs[2]);
        System.out.println(strs[3]);

        System.out.println("=============");

        String str1 = "[00:29.76]欢迎收听幸福内心禅。\n" +
                "[00:32.88]各位亲爱的听众朋友大家好，我是乔安。\n" +
                "[00:36.75]与我们在线上共度幸福时光的是内心禅创办人，\n" +
                "[00:41.65]也是财团法人中岭山内心教育基金会的董事长张庆祥张讲师。\n" +
                "[00:48.15]张讲师您好！\n" +
                "[00:49.86]乔安你好，各位听众大家好！\n" +
                "[00:52.14]张讲师，\n" +
                "[00:53.29]您今天要告诉我们听众朋友的主题是幸福的诀窍，\n" +
                "[00:57.37]从喜马拉雅的不丹王国到美国等全球有超过100多个国家，\n" +
                "[01:02.64]他们在衡量经济成长的时候，也逐渐的将生活品质，\n" +
                "[01:07.29]也就是将幸福的指标纳入统计。\n" +
                "[01:10.55]也就是因为这样，";
        str1.replaceAll("\n", "");
        String[] strs1 = str1.split("\\[");
        for (int i = 0; i < strs1.length; i++) {
            if (TextUtils.isEmpty(strs1[i])) continue;
            System.out.println(strs1[i].substring(0, strs1[i].indexOf("]")));
            System.out.println(strs1[i].substring(strs1[i].indexOf("]") + 1));
        }
    }
}
