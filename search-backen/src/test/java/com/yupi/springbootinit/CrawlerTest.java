package com.yupi.springbootinit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author： xingzhi
 * @create： 2023-03-12 20:29
 * @Description：
 */

@SpringBootTest
public class CrawlerTest {


    @Resource
    private PostService postService;

    @Test
    public void testFetchPicture() throws IOException {
        int current = 1;
        String url = "https://cn.bing.com/images/search?q=lol%E7%82%B9%E8%B5%9E&qs=n&form=QBIR&sp=-1&lq=0&pq=lol%E7%82%B9%E8%B5%9E&sc=1-5&cvid=799FC90653A04366AD2149B71E7DF3A2&ghsh=0&ghacc=0&first=" + current;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            String title = element.select(".inflnk").get(0).attr("aria-label");
//            System.out.println(title + murl);
            // 封装到picture
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
        }
        System.out.println(pictures);

    }

    @Test
    public void tesrFetchPassage(){

        // 1、获取数据
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
//        System.out.println(result);

        // 2、json 转成post对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            // set有空会报错，这个没有加判断
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            post.setCreateTime(new Date());
            post.setUpdateTime(new Date());
            postList.add(post);
        }
        // 3、批量插入到数据库，打个断言，保证一定能插入
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
//        System.out.println(postList);
//        System.out.println(records);
    }
}

