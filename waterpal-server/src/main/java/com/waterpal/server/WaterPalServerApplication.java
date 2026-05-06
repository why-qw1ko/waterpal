package com.waterpal.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WaterPal 后端启动类
 */
@SpringBootApplication
@MapperScan("com.waterpal.server.repository")
public class WaterPalServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WaterPalServerApplication.class, args);
    }
}
