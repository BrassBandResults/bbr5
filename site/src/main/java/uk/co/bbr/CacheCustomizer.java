package uk.co.bbr;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        List<String> cacheNames = new ArrayList<>();
        cacheNames.add("event-list-for-contest");
        cacheManager.setCacheNames(cacheNames);
    }
}
