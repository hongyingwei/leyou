package com.leyou.config;

import com.leyou.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@Data
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {

    private String secret; // 密钥

    private String pubKeyPath; // 公钥

    private String priKeyPath; // 私钥

    private int expire; // token过期时间

    private PublicKey publicKey; // 公钥

    private PrivateKey privateKey; // 私钥

    private String cookieName; //cookie名称

    private Integer cookieMaxAge; //cookie最长时效

    //对象一旦实例化后，就应该读取公钥和私钥
    @PostConstruct
    public void init() throws Exception{
        try{
            //公钥私钥如果不存在，先生成
            File pubPath = new File(pubKeyPath);
            File priPath = new File(priKeyPath);

            if(!pubPath.exists() || !priPath.exists()){
                //生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            }
            //读取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        }catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }

    public Integer getCookieMaxAge() {
        return this.getCookieMaxAge();
    }
}