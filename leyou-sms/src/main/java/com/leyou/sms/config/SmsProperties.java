package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author
 * @description
 */
@ConfigurationProperties(prefix = "leyou.sms")
@Data
public class SmsProperties {
    String accessKeyId;

    String accessKeySecret;

    String signName;

    String verifyCodeTemplate;
}
