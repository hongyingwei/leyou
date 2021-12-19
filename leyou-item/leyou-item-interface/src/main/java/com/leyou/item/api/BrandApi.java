package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
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
@RequestMapping("brand")
public interface BrandApi {
    /**
     * 根据查询条件分页并排序查询品牌信息
     * （不足之处）：
     * 当前：key作为模糊匹配搜索
     * 改进：可以用Elasticsearch全文检索
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public PageResult<Brand> queryBrandsByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc
    );

    /**
     * 通过分类cid查询对应的品牌集合
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public List<Brand> queryBrandsByCid(@PathVariable("cid")Long cid);

    /**
     * 通过bid查询品牌信息
     * @param bid
     * @return
     */
    @GetMapping("{bid}")
    public Brand queryBrandByBid(@PathVariable("bid")Long bid);
}
