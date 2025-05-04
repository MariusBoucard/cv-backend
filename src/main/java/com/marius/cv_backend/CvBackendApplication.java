package com.marius.cv_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CvBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvBackendApplication.class, args);
    }
}

@RestController
class RootController {

    @GetMapping("/")
    public String root() {
        return "OK";
    }
}