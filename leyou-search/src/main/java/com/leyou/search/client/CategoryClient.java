package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author
 * @description
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
