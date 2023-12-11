package com.example.OurHome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OurHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurHomeApplication.class, args);
    }

}
