package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author
 * @description category的数据库操作模块
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {

    /**
     * 根据category_id删除中间表相关数据
     * @param id
     */
    @Delete("DELETE FROM tb_category_brand WHERE category_id = #{cid}")
    void deleteByCategoryIdInCategoryBrand(@Param("cid") Long id);

    /**
     *根据brand_id查询中间表中的category_id
     * @param bid
     * @return
     */
    @Select("SELECT * FROM tb_category WHERE id IN(\n" +
            "SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> selectCategoryIDByBid(@Param("bid") Long bid);
}
