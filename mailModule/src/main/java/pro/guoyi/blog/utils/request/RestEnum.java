package pro.guoyi.blog.utils.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pro.guoyi.blog.utils.request.interceptor.HttpLoggingInterceptor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public enum RestEnum {
    /**
     * RestTemplate 单例
     */
    SINGLE_INSTANCE;
    private RestTemplate restTemplate;

    RestEnum() {
        // 设置超时
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(5000))
                .build();

        // 设置日志拦截
        ClientHttpRequestInterceptor ri = new HttpLoggingInterceptor();
        List<ClientHttpRequestInterceptor> ris = new ArrayList<>();
        ris.add(ri);
        restTemplate.setInterceptors(ris);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}