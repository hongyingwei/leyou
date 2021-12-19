package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author
 * @description
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
