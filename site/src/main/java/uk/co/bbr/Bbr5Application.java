package uk.co.bbr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.co.bbr.services.framework.annotations.IgnoreCoverage;

@SpringBootApplication
@IgnoreCoverage
public class Bbr5Application {

    public static void main(String[] args) {
        SpringApplication.run(Bbr5Application.class, args);
    }

}
