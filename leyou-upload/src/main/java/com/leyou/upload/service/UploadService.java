package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author HYW
 * @description上传文件到远端的FastDFS
 */
@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    //fastDFS的文件存储操作（fastDFS小型文件系统）
    @Autowired
    private FastFileStorageClient storageClient;

    //效验文件类型
    @Autowired
    private UploadProperties prop;
//    private static final List<String> FILE_TYPE = Arrays.asList("image/gif", "image/jpeg", "image/jpeg", "application/x-jpg","image/jpeg", "image/png");
//    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    /**
     * 图片文件上传到192.168.217.128服务器的fastDFS文件系统
     * @param file
     * @return String
     */
    public String uploadImage(MultipartFile file) {
        try {
            //1、获取文件类型
            String contentType = file.getContentType();
            if(!prop.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //2、获取文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //3、上传文件到文件服务器
            //上传到FastDFS
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");  //文件后缀名
            StorePath storePath = storageClient.uploadFile(
                    file.getInputStream(), file.getSize(), extension, null);

            //返回路径
            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            log.error("[文件上传] 上传文件失败!",e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
