package com.mine.obsession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ObsessionApplication {

    public static Logger log = LoggerFactory.getLogger(ObsessionApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ObsessionApplication.class, args);
    }

}
