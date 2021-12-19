package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 文件上传
 * @author
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LeyouUploadApplication{
    public static void main(String[] args) {
        SpringApplication.run(LeyouUploadApplication.class);
    }
}
