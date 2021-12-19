package com.leyou.item.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HYW
 * @description
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点的id查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid){
        try {
            if(pid==null || pid<0){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();//400：参数错误
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories = this.categoryService.queryCategoriesByPid(pid);
            if(CollectionUtils.isEmpty(categories)){
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); //404：资源未找到
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categories); //200：响应成功
        } catch (Exception e) {
            e.printStackTrace();
        }
        //500：服务器内部异常
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 更新category信息
     * @param category
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCategory(Category category){
        this.categoryService.updateCategory(category);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * 增加分类(保存分类)
     * @param category
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveCategory(Category category){
        this.categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除分类信息
     * @param cid
     * @return
     */
//    @DeleteMapping("cid/{cid}")
    @RequestMapping(method = RequestMethod.DELETE, path = "cid/{cid}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("cid") Long cid){
        this.categoryService.deleteCategory(cid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 用于修改品牌信息时，商品分类信息的回显
     * @param bid
     * @return
             */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoriesByBid(@PathVariable("bid") Long bid){
        List<Category> categories = this.categoryService.queryCategoriesByBid(bid);
        if(categories == null || categories.size() < 1)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据分类id查询分类名称
     * @param ids
     * @return
     */
    @GetMapping("names")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
    /**
     * 根据3级分类id，查询1~3级的分类
     * @param cid3
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long cid3){
        List<Category> categories = this.categoryService.queryAllByCid3(cid3);
        if(categories==null || categories.size()<1){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }
}
