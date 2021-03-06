package pro.guoyi.blog;

import com.pnikosis.html2markdown.FilesUtil;
import com.pnikosis.html2markdown.HTML2Md;
import pro.guoyi.blog.bean.BlogList;
import pro.guoyi.blog.config.Config;
import pro.guoyi.blog.utils.request.RestClientUtils;

import java.io.IOException;
import java.net.URL;

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
                                continue;
                            }
                            //私密文章，私密文章暂时也无法导出
                            if (blog.getStatus().equals("64")) {
                                continue;
                            }

                            //开始导出解析HTML，并转换成Markdown字符串
                            System.out.println(">>>开始导出：" + blog.getTitle());
                            String uri = "https://blog.csdn.net/" + Config.userName + "/article/details/" + blog.getArticleId();
                            url = new URL(uri);
                            String convert = "# " + blog.getTitle() + "\n\n" + HTML2Md.convertById(url, 2000, "content_views");

                            //输出到文件
                            //ID命名
                            //FilesUtil.newFile("blog/md/" + uri.split("details/")[1] + ".md", convert);
                            //文章名命名
                            FilesUtil.newFile("blog/md/" + blog.getTitle() + ".md", convert);

                            count++;
                            System.out.println(">>>完成导出：" + blog.getTitle() + "<<<");
                            System.out.println("现已导出文章" + count + "篇\n");
                        }
                    }
                } else {
                    System.out.println("文章导出完毕，本次共导出：" + count + "篇文章");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
