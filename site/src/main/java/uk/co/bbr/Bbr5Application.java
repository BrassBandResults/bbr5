package uk.co.bbr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;
import uk.co.bbr.services.map.repo.LocationRepository;

@SpringBootApplication
@EnableJpaRepositories(excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {LocationRepository.class})})
@IgnoreCoverage
public class Bbr5Application {

    public static void main(String[] args) {
        SpringApplication.run(Bbr5Application.class, args);
    }

}
