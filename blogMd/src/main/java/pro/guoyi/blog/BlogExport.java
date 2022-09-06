package pro.guoyi.blog;

import com.pnikosis.html2markdown.FilesUtil;
import com.pnikosis.html2markdown.HTML2Md;
import pro.guoyi.blog.bean.BlogList;
import pro.guoyi.blog.config.Config;
import pro.guoyi.blog.utils.request.RestClientUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author guoyi
 */
public class BlogExport {

    public static void main(String[] args) {
        URL url;
        int count = 0;
        try {
            for (int i = 1; true; i++) {
                BlogList blogList = RestClientUtils.get("https://blog-console-api.csdn.net/v1/article/list?pageSize=20&page=" + i, Config.cookie, BlogList.class);
                if (i == 1) System.out.println(blogList.getData().getCount().getAll() + "篇文章需要导出");
                if (blogList.getData().getList().size() > 0) {
                    System.out.println("-----" + blogList.getData().getList().size() + "篇文章开始导出-----");
                    if (blogList.getData().getCount().getAll() > 0) {
                        System.out.println("文章导出开始.......");
                        for (BlogList.DataBean.ListBean blog : blogList.getData().getList()) {
                            //审核失败文章，审核失败是无法导出的
                            if (blog.getStatus().equals("6")) {
                                System.out.println("1111111");
//                                continue;
                            }
                            //私密文章，私密文章暂时也无法导出
                            if (blog.getStatus().equals("64")) {
                                System.out.println("22222222");
//                                continue;
                            }

                            //开始导出解析HTML，并转换成Markdown字符串
                            System.out.println(">>>开始导出：" + blog.getTitle());
                            String uri = "https://blog.csdn.net/" + Config.userName + "/article/details/" + blog.getArticleId();
                            url = new URL(uri);
                            HashMap<String, Object> map = HTML2Md.convertById(url, blog.getStatus(), Config.cookie, 2000, "content_views");
                            String convert = "";
                            // ArticleInfo articleInfo = RestClientUtils.get("https://bizapi.csdn.net/blog-console-api/v1/editor/getArticle?id=" + blog.getArticleId(), Config.cookie, ArticleInfo.class);
                            ArrayList<String> tags = (ArrayList<String>) map.get("tags");
                            convert = "---\n" + "layout:     post\n" + "title:      " + blog.getTitle() + "\n" + "subtitle:   " + blog.getTitle() + "\n" + "date:       " + blog.getPostTime() + "\n" + "author:     Sunny day\n" + "header-img: img/post-bg-ios9-web.jpg\n" + "catalog: true\n" + "tags:\n";
                            for (String s : tags) {
                                convert = convert + "    - " + s + "\n";
                            }
                            convert = convert + "---\n\n>" + blog.getTitle() + "\n\n";
                            convert = convert + "# " + blog.getTitle() + "\n\n" + map.get("document");

                            String postTime = blog.getPostTime();
                            String year = null;
                            String mouth = null;
                            String day = null;
                            try {
                                year = postTime.split("-")[0];
                                mouth = postTime.split("-")[1];
                                day = postTime.split("-")[2].split(" ")[0];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            blog.setTitle(blog.getTitle().replace("/", " "));

                            //输出到文件
                            //ID命名
                            //FilesUtil.newFile("blog/md/" + uri.split("details/")[1] + ".md", convert);
                            if (year != null && mouth != null && day != null) {
                                //文章名命名
                                FilesUtil.newFile("blog/md/" + year + "-" + mouth + "-" + day + "-" + blog.getTitle() + ".md", convert);
                            } else {
                                FilesUtil.newFile("blog/md/" + blog.getTitle() + ".md", convert);
                            }
                            count++;
                            System.out.println(">>>完成导出：" + blog.getTitle() + "<<<");
                            System.out.println("共" + blogList.getData().getCount().getEnable() + "篇文章，现已导出文章" + count + "篇\n");
                        }
                    }
                } else {
                    System.out.println("文章导出完毕，共" + blogList.getData().getCount().getEnable() + "篇文章，本次共导出：" + count + "篇文章");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
