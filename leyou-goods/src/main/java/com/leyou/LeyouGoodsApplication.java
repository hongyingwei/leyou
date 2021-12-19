package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients   //访问外部微服务接口
public class LeyouGoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouGoodsApplication.class);
    }
}
