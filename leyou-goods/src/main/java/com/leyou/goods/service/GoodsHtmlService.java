package com.leyou.goods.service;

import com.leyou.goods.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author
 * @description
 */
@Service
public class GoodsHtmlService {

    @Autowired
    private GoodsWebService goodsWebService;

    //用来解析模版的引擎
    @Autowired
    private TemplateEngine templateEngine;

    //输出该类日志
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    /**
     * 输出静态页面，然后存在nginx的HTML文件夹，便于快速访问thymeleaf加载的页面
     *
     * @param spuId
     */
    public void createHtml(Long spuId){
        PrintWriter printWriter = null;
        //获取页面数据
        try{
            Map<String, Object> map = goodsWebService.loadData(spuId);

            //1、创建thymeleaf上下文
            Context context = new Context();
            //2、把数据放入context中
            context.setVariables(map);

            //3、创建输出流
            File file = new File("D:\\JavaTools\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);

            // 4、执行页面静态化方法
            templateEngine.process("item", context, printWriter);
        }catch (Exception e){
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        }finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId){
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }
}
