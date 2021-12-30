package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;







import java.util.List;

/**
 * @author sku和spu信息
 * @description
 */
@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = this.specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据下列参数条件查询规格参数
     * @param gid 分组gid
     * @param cid 分类cid
     * @param generic 是否为通用参数
     * @param searching 是否可检索
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid", required = false)Long gid,
            @RequestParam(value = "cid", required = false)Long cid,
            @RequestParam(value = "generic", required = false)Boolean generic,
            @RequestParam(value = "searching", required = false)Boolean searching){
        List<SpecParam> specParam = this.specificationService.queryParams(gid, cid, generic, searching);
        if(CollectionUtils.isEmpty(specParam)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParam);
    }

    /**
     * 保存一个规格参数spu属性
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> saveSpecGroup(SpecGroup specGroup){
        this.specificationService.saveSpecGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    /**
     * 保存一个商品组的sku属性
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> saveSpecParam(SpecParam specParam){
        this.specificationService.saveSpecParam(specParam);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改一个规格参数spu属性
     * @param id
     * @param cid
     * @param name
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateSpecGroup(@RequestParam("id")Long id,
                                                @RequestParam("cid")Long cid,
                                                @RequestParam("name")String name){
        SpecGroup specGroup = new SpecGroup();
        specGroup.setId(id);
        specGroup.setCid(cid);
        specGroup.setName(name);
        this.specificationService.updateSpecGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    /**
     * 修改一个商品组的sku属性
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateSpecParam(SpecParam specParam){
        this.specificationService.updateSpecParam(specParam);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除一个商品组的sku属性
     * @param id
     * @return
     */
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id")Long id){
        this.specificationService.deleteSpecParam(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除一个商品组spu
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id")Long id){
        this.specificationService.deleteSpecGroup(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据cid查询SpecGroup集合
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long cid){
        List<SpecGroup> groups = this.specificationService.queryGroupsWithParam(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
}
