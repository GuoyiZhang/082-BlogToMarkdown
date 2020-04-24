package pro.guoyi.blog.utils.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * 发起请求工具类
 */
public class RestClientUtils {

    /**
     * POST JSON请求
     *
     * @param reqUrl          "http://www.baidu.com"
     * @param reqJsonStrParam "{\"name\":\"BeJson\",\"url\"}"
     * @return return
     */
    public static String postJson(String reqUrl, String reqJsonStrParam) {
        //设置 Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        //设置参数
        HttpEntity<String> requestEntity = new HttpEntity<>(reqJsonStrParam, httpHeaders);
        //执行请求
        ResponseEntity<String> resp = RestEnum.SINGLE_INSTANCE.getRestTemplate()
                .exchange(reqUrl, HttpMethod.POST, requestEntity, String.class);
        //返回请求返回值
        return resp.getBody();
    }

    /**
     * POST form请求
     *
     * @param reqUrl      请求URL
     * @param reqFormPara Object 转string
     * @return return
     */
    public static String postForm(String reqUrl, String reqFormPara) {
        //设置 Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //设置参数
        HttpEntity<String> requestEntity = new HttpEntity<>(reqFormPara, httpHeaders);
        //执行请求
        ResponseEntity<String> resp = RestEnum.SINGLE_INSTANCE.getRestTemplate()
                .exchange(reqUrl, HttpMethod.POST, requestEntity, String.class);
        //返回请求返回值
        return resp.getBody();
    }

    public static String getForm(String reqUrl, String reqFormPara) {
        //设置 Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //设置参数
        HttpEntity<String> requestEntity = new HttpEntity<>(reqFormPara, httpHeaders);
        //执行请求
        ResponseEntity<String> resp = RestEnum.SINGLE_INSTANCE.getRestTemplate()
                .exchange(reqUrl, HttpMethod.GET, requestEntity, String.class);
        //返回请求返回值
        return resp.getBody();
    }

    /**
     * 发起get请求
     *
     * @param reqUrl
     * @param oClass
     * @param <O>
     * @return
     */
    public static <O> O get(String reqUrl, String cookie, Class<O> oClass) {
        //执行请求
        //返回请求返回值
        //设置 Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("accept-encoding", "gzip, deflate");
        if (!cookie.isEmpty()) httpHeaders.set("cookie", cookie);
        httpHeaders.set("upgrade-insecure-requests", "1");
        httpHeaders.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        httpHeaders.set("accept-encoding", "gzip, deflate, br");
        httpHeaders.set("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        //设置参数
        HttpEntity<String> requestEntity = new HttpEntity<>(new JsonObject().toString(), httpHeaders);

        ResponseEntity<String> resp = RestEnum.SINGLE_INSTANCE.getRestTemplate()
                .exchange(reqUrl, HttpMethod.GET, requestEntity, String.class);
        String body = resp.getBody();
        Gson gson = new Gson();
        return gson.fromJson(body, oClass);
    }

    /**
     * 执行curl
     *
     * @param cmds 命令
     * @return 执行结果
     */
    public static String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }
        return null;
    }


    public static String unzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = compressedStr.getBytes(StandardCharsets.ISO_8859_1);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            out.flush();
            decompressed = out.toString();
            if (ginzip != null)
                ginzip.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decompressed;
    }


}
