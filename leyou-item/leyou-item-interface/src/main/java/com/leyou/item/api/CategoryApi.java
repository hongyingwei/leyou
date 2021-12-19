package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author
 * @description
 */
@RequestMapping("category")
public interface CategoryApi {
    /**
     * 根据父节点的id查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    List<Category> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid);

    /**
     * 用于修改品牌信息时，商品分类信息的回显
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    List<Category> queryCategoriesByBid(@PathVariable("bid") Long bid);

    /**
     * 根据分类id查询分类名称
     * @param ids
     * @return
     */
    @GetMapping("names")
    List<String> queryNamesByIds(@RequestParam("ids")List<Long> ids);
}
