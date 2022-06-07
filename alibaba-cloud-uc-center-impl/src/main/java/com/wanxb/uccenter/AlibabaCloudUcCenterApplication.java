package com.wanxb.uccenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author wanxianbo
 * @description
 * @date 创建于 2022/6/7
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.wanxb"})
@MapperScan(value = {"com.wanxb.**.mapper"})
public class AlibabaCloudUcCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlibabaCloudUcCenterApplication.class, args);
    }
}
