package com.catcoffee.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.catcoffee.backend.mapper")
@SpringBootApplication
public class CatCoffeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatCoffeeApplication.class, args);
    }
}
