package com.yupi.springbootinit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * @author： xingzhi
 * @create： 2023-03-12 20:29
 * @Description：
 */

@SpringBootTest
public class CrawlerTest {

    @Test
    public void tesrFetchPassage(){
        // 1、获取数据
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String json = "\"current\": 1, \"pageSize\": 8, \"sortField\": \"createTime\", sortOrder: \"descend\", category: \"文章\",…}";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
        System.out.println(result);

        // 2、json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
    }
}
