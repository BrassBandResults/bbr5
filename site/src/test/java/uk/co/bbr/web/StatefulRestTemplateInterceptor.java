package uk.co.bbr.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class StatefulRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private String cookie;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (cookie != null) {
            request.getHeaders().add(HttpHeaders.COOKIE, cookie);
        }
        ClientHttpResponse response = execution.execute(request, body);

        if (cookie == null) {
            cookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        }
        return response;
    }
}
