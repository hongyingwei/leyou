package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author
 * @description Brand的数据库操作
 */
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 维护分类和品牌中间表
     * @param cid
     * @param brandId
     */
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid}, #{brandId})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("brandId") Long brandId);

    /**
     * 删除中间表tb_category_brand中的bid对应的数据
     * @param bid
     */
    @Delete("DELETE FROM tb_category_brand WHERE brand_id=#{bid}")
    void deleteByBrandIdInCategoryBrand(@Param("bid") Long bid);

    /**
     * 通过分类cid查询对应的品牌集合
     * @param cid
     * @return
     */
    @Select("SELECT * FROM tb_brand WHERE id IN (\n" +
            "SELECT brand_id FROM tb_category_brand WHERE category_id = #{cid})")
    List<Brand> queryBrandsByCid(@Param("cid") Long cid);
}
