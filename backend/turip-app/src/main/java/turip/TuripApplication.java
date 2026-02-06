package turip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class TuripApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuripApplication.class, args);
    }
}
