package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hyw
 * @description 短信微服务的配置信息，从yml文件注入
 */
@ConfigurationProperties(prefix = "leyou.sms")
@Data
public class SmsProperties {
    String accessKeyId;

    String accessKeySecret;

    String signName;

    String verifyCodeTemplate;
}
