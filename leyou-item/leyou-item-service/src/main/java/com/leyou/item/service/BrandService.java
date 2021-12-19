package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.zip.ZipFile;

/**
 * @author
 * @description Brand的Service层
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name", "%"+key+"%").orEqualTo("letter", key);
        }
        //添加分页条件
        PageHelper.startPage(page, rows);
        //添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);

        //包装成pageInfo
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
        return new PageResult<>(brandPageInfo.getTotal(), brandPageInfo.getList());
    }

    /**
     * 新增加品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        // 新增品牌信息
        brand.setId(null);
        int count = this.brandMapper.insert(brand);
        if(count != 1){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);  //品牌新建失败
        }
        // 新增品牌和分类中间表
        for(Long cid: cids){
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
            if(count != 1){
                throw new LyException(ExceptionEnum.CATEGROY_BRAND_SAVE_ERROR);
            }
        }
    }


    /**
     * 修改品牌信息
     * @param brand
     * @param cids
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(Brand brand, List<Long> cids) {
        //1、先更新tb_brand表中的信息
        this.brandMapper.updateByPrimaryKey(brand);
        //2、然后删除中间表tb_category_brand对应的brand_id中的数据
        deleteByBrandIdInCategoryBrand(brand.getId());

        //3、添加分类和品牌之间的关系
        for(Long cid: cids){
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
        }
    }

    /**
     * 删除中间表tb_category_brand对应的brand_id中的数据
     * @param bid
     */
    @Transactional(rollbackFor = Exception.class)
    private void deleteByBrandIdInCategoryBrand(Long bid) {
        this.brandMapper.deleteByBrandIdInCategoryBrand(bid);
    }

    /**
     * 删除品牌信息
     * @param bid
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrandByBrandID(Long bid) {
        //1、删除中间表tb_category_brand中的数据
        this.brandMapper.deleteByBrandIdInCategoryBrand(bid);

        //2、删除表tb_brand中的品牌信息
        this.brandMapper.deleteByPrimaryKey(bid);
    }

    /**
     * 通过id查询品牌信息
     * @param id
     * @return
     */
    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    /**
     * 通过分类cid查询对应的品牌集合
     * @param cid
     * @return
     */
    public List<Brand> queryBrandsByCid(Long cid) {
        return this.brandMapper.queryBrandsByCid(cid);
    }

    /**
     * 通过bid查询品牌
     * @param bid
     * @return
     */
    public Brand queryBrandByBid(Long bid) {
        return this.brandMapper.selectByPrimaryKey(bid);
    }
}
