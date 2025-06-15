package me.soldesk.katteproject_backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KatteProjectBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(KatteProjectBackendApplication.class, args);
    }
}
