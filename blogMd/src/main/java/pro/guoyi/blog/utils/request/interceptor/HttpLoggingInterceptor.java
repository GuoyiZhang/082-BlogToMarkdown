package pro.guoyi.blog.utils.request.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

@Slf4j
public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final String REQ_BEG = "=========================== req beg >>>>>>>>>>>>>>>>>>>>>>>>>>";
    private static final String REQ_END = "=========================== req end <<<<<<<<<<<<<<<<<<<<<<<<<<";
    private static final String RES_BEG = "=========================== res beg >>>>>>>>>>>>>>>>>>>>>>>>>>";
    private static final String RES_END = "=========================== res end <<<<<<<<<<<<<<<<<<<<<<<<<<";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, body);
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
        } catch (SocketTimeoutException e) {
            // throw new BusinessException(EnumResult.CODE_800004, "数据请求超时");
        }

        logResponse(response);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {

        if (log.isInfoEnabled()) {
            log.info(REQ_BEG);
            log.info("URI         : {}", request.getURI());
            log.info("Method      : {}", request.getMethod());
            log.info("Headers     : {}", request.getHeaders());
            log.info("Request body: {}", new String(body, "UTF-8"));
            log.info(REQ_END);
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {

        if (log.isInfoEnabled()) {
            log.info(RES_BEG);
            log.info("Status code  : {}", response.getStatusCode());
            log.info("Status text  : {}", response.getStatusText());
            log.info("Headers      : {}", response.getHeaders());
            log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
            log.info(RES_END);
        }
    }
}
