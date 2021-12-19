package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**商品规格service
 * @author
 * @description
 */
@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }

    /**
     * 根据group_ID条件查询规格参数
     *
     * @param cid
     * @param gid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);

        return this.specParamMapper.select(specParam);
    }

    /**
     * 保存一个规格参数模板
     * @param specGroup
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSpecGroup(SpecGroup specGroup) {
        this.specGroupMapper.insert(specGroup);
    }

    /**
     * 保存一个商品组的sku属性
     * @param specParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSpecParam(SpecParam specParam) {
        this.specParamMapper.insert(specParam);
    }

    /**
     * 修改一个规格参数spu属性
     * @param specGroup
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSpecGroup(SpecGroup specGroup) {
        this.specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    /**
     * 修改一个商品组的sku属性
     * @param specParam
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSpecParam(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    /**
     * 删除一个商品组的sku属性
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpecParam(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);
    }

    /**
     * 删除一个商品组spu
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpecGroup(Long id) {
        //1、维护tb_spec_param中的数据
        this.specParamMapper.deleteSpecParamByCidAndGroupID(id);

        //2、删除tb_spec_group中的数据
        this.specGroupMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据cid查询SpecGroup集合
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        groups.forEach(group ->{
            List<SpecParam> params = this.queryParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
