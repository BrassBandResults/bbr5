package uk.co.bbr;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfiguration {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        RestTemplate restTemplate = templateBuilder.build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        return restTemplate;
    }
}
