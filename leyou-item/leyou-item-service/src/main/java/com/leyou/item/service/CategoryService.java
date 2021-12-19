package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HYW
 * @description
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 通过父节点pid查询子节点集
     *
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categories = this.categoryMapper.select(category);
        return categories;
    }

    /**
     * 更新category信息
     *
     * @param category
     */
    public void updateCategory(Category category) {
        this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    /**
     * 增加分类（更新分类）
     *
     * @param category
     */
    public void saveCategory(Category category) {
        //先将新的分类信息插入表中
        category.setId(null);
        this.categoryMapper.insert(category);

        //然后更新该该新增加的节点的父类的信息
        Category parent = new Category();
        parent.setId(category.getParentId());
        parent.setIsParent(true);
        this.categoryMapper.updateByPrimaryKeySelective(parent);
    }

    /**
     * 删除分类
     *
     * @param cid
     */
    public void deleteCategory(Long cid) {
        Category category = this.categoryMapper.selectByPrimaryKey(cid);
        if (category.getIsParent()) { //是否是父节点
            //1、查找该目录下的所有叶子节点
            List<Category> list = new ArrayList<>();
            queryAllLeafNode(category, list);
            //2、查找该目录下的所有子节点
            List<Category> list2 = new ArrayList<>();
            queryAllNode(category, list2);
            //3、删除tb_category表中的节点
            for (Category c : list2) {
                this.categoryMapper.delete(c);
            }
            //4、维护tb_category_brand表的数据
            for (Category c : list) {
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(c.getId());
            }
        } else {
            //1.查询此节点的父亲节点的孩子个数 ===> 查询还有几个兄弟
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("parentId", category.getParentId());
            List<Category> categories = this.categoryMapper.selectByExample(example);
            if (categories.size() != 1) {
                //1、如果直接有兄弟节点，则删除该节点即可
                this.categoryMapper.deleteByPrimaryKey(category.getId());
                //2、维护中间表的数据
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            } else {
                //1、说明没有兄弟节点，先删除该节点，然后更新父节点的信息
                this.categoryMapper.deleteByPrimaryKey(category.getId());

                //2、更新父节点的值
                Category parent = new Category();
                parent.setId(category.getParentId());
                parent.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(parent);

                //3、维护中间表的信息
                this.categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }
        }
    }

    /**
     * 查询本节点下所有子节点(利用递归查询所有子节点)
     *
     * @param category
     * @param node
     */
    public void queryAllNode(Category category, List<Category> node) {
        node.add(category);

        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", category.getId());
        List<Category> categories = this.categoryMapper.selectByExample(example);
        for (Category x : categories) {
            queryAllNode(x, node);
        }
    }

    /**
     * 查询本节点下所包含的所有叶子节点，用于维护tb_category_brand中间表（利用递归查询）
     *
     * @param category
     * @param leafNode
     */
    public void queryAllLeafNode(Category category, List<Category> leafNode) {
        if (!category.getIsParent()) {
            leafNode.add(category);
        }

        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", category.getId());
        List<Category> categories = this.categoryMapper.selectByExample(example);

        for (Category category1 : categories) {
            queryAllLeafNode(category1, leafNode);
        }
    }

    /**
     * 根据品牌id查询商品分类信息
     *
     * @param bid
     * @return
     */
    public List<Category> queryCategoriesByBid(Long bid) {
        List<Category> categories = this.categoryMapper.selectCategoryIDByBid(bid);
        if (categories == null) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    /**
     * 通过分类id查询类别
     * @param ids
     * @return
     */
    public List<Category> queryByIds(List<Long> ids) {
        List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException((ExceptionEnum.CATEGORY_NOT_FOUND));
        }
        return list;
    }


    /**
     * 通过分类id查询类别名称
     * @param ids
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = this.queryByIds(ids);
        List<String> collect = categories.stream().map(Category::getName).collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据cid3查询所有1-3之间的父目录
     * @param cid3
     * @return
     */
    public List<Category> queryAllByCid3(Long cid3) {
        Category category3 = this.categoryMapper.selectByPrimaryKey(cid3);
        Category category2 = this.categoryMapper.selectByPrimaryKey(category3.getParentId());
        Category category1 = this.categoryMapper.selectByPrimaryKey(category2.getParentId());
        return Arrays.asList(category1, category2, category3);
    }
}
