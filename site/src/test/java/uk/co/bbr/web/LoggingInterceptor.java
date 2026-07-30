package uk.co.bbr.web;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("[DEBUG_LOG] Request: " + request.getMethod() + " " + request.getURI());
        System.out.println("[DEBUG_LOG] Request Headers: " + request.getHeaders());
        ClientHttpResponse response = execution.execute(request, body);
        System.out.println("[DEBUG_LOG] Response Status: " + response.getStatusCode());
        System.out.println("[DEBUG_LOG] Response Headers: " + response.getHeaders());
        return response;
    }
}
