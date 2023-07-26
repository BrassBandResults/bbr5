package uk.co.bbr;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.map.LocationService;
import uk.co.bbr.mocks.TestLocationService;

@Profile("test")
@Configuration
public class TestConfiguration {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        RestTemplate restTemplate = templateBuilder.build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        return restTemplate;
    }

    @Bean
    public LocationService locationService() {
        return new TestLocationService();
    }
}
