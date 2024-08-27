package com.webank.wsas.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class FrontApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontApplication.class, args);
        log.info("main run success...");
    }
}
