package com.vincent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class MatchingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchingEngineApplication.class, args);
    }

}
