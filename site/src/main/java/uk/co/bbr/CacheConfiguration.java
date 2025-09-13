package uk.co.bbr;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager caffeineCacheManager() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
            .maximumSize(30) // max entries in each cache
            .expireAfterWrite(Duration.ofSeconds(30));

        CaffeineCacheManager manager = new CaffeineCacheManager(
            "todayInHistory",
            "thisWeekInHistory",
            "nextWeekendEvents",
            "thisWeekendEvents",
            "lastWeekendEvents",
            "resultsForBand",
            "resultsForConductor",
            "peopleStartingWith",
            "venuesStartingWith"
        );

        // Add this annotation at service layer to enable caching
        // @Cacheable(cacheNames = "userByCode", key = "#usercode", cacheManager = "caffeineCacheManager")

        manager.setCaffeine(builder);
        return manager;
    }

}
