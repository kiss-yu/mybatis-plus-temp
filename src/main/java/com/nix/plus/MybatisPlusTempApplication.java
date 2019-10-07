package com.nix.plus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 11723
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value = {
        "com.nix.plus.mapper"
})
@EnableScheduling
public class MybatisPlusTempApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusTempApplication.class, args);
    }

}
