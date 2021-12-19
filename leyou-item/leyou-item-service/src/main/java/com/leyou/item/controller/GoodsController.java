package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author
 * @description
 */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询商品信息spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key) {
        PageResult<Spu> spus = goodsService.querySpuByPage(page, rows, saleable, key);
        return ResponseEntity.ok(spus);
    }

    /**
     * 新增商品信息
     * @param spu
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        this.goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 通过spu_id查询spu的详细信息
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail spuDetail = this.goodsService.querySpuDetailBySpuId(spuId);
        if(spuDetail == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 通过spu_id查询sku的详细信息
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus = this.goodsService.querySkusBySpuId(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 更新goods商品信息
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        this.goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除商品(逻辑删除)（不足：暂时通过前端控制已删除商品不显示， 改进：通过查询mysql控制已删除商品不查询出来）
     * @param spuId
     * @return
     */
    @DeleteMapping("goods/spu/{spuId}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("spuId")Long spuId){
        this.goodsService.deleteGoods(spuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 上下架商品
     * @param spuId
     * @return
     */
    @PutMapping("goods/spu/out/{spuId}")
    public ResponseEntity<Void> goodsSoldOut(@PathVariable("spuId")Long spuId){
        this.goodsService.goodsSoldOut(spuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     *通过spuID查询spu
     * @param spuId
     * @return
     */
    @GetMapping("{spuId}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("spuId")Long spuId){
        Spu spu = this.goodsService.querySpuById(spuId);
        if(spu == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }
}
