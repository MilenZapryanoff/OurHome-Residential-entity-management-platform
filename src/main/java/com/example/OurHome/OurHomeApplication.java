package com.example.OurHome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class OurHomeApplication {

    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "classpath:log4j2.xml");

        SpringApplication.run(OurHomeApplication.class, args);
    }

}
