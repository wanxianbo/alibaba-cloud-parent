package com.wanxb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * <p>
 *
 * </p>
 *
 * @author wanxinabo
 * @date 2021/9/16
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.wanxb"})
public class AlibabaCloudOauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlibabaCloudOauthApplication.class, args);
    }
}
