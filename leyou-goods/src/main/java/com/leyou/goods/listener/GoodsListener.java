package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author
 * @description
 */
@Component
public class GoodsListener {
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    /**
     * 处理insert和update的消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.item.save.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange", ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
            ))
    public void listenerSaveAndUpdate(Long id){
        if(id == null)
            return;
        this.goodsHtmlService.createHtml(id);
    }

    /**
     * 处理delete消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.item.index.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange", ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.TOPIC),
            key = "item.delete"
    ))
    public void listenDelete(Long id){
        if(id == null)
            return;
        this.goodsHtmlService.deleteHtml(id);
    }
}
