package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @description
 */
@Service
public class GoodsService {
    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 分页查询商品信息spu
     *
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);

        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");

        //查询
        List<Spu> spus = this.spuMapper.selectByExample(example);


        //判断
//        if (CollectionUtils.isEmpty(spus)) {
//            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
//        }

        //解析分类和品牌名称
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(), spus);
    }

    /**
     * 解析分类和品牌名称
     * @param spus
     */
    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称
            List<String> names = this.categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());  //提取Category集合中的name作为一个集合
            spu.setCname(StringUtils.join(names, "/"));

            //处理品牌名称
            spu.setBname(this.brandService.queryById(spu.getBrandId()).getName());
        }
    }

    /**
     * 新增商品信息
     * @param spu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);//自增
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = spuMapper.insert(spu);
        if(count!=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROE);
        }
        //新增detail
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        this.spuDetailMapper.insert(detail);

        //新增sku和库存
        saveSkuAndStock(spu);

        sendMsg("insert", spu.getId());
    }

    /**
     * rabbitmq发送消息
     * @param type
     * @param id
     */
    private void sendMsg(String type, Long id) {
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增sku和库存
     * @param spu
     */
    private void saveSkuAndStock(Spu spu){
        int count_stock;
        //定义库存集合
        List<Stock> stockList= new ArrayList<>();
        //新增sku
        List<Sku> skus = spu.getSkus();

        skus.forEach(sku ->{
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            int count_sku = this.skuMapper.insert(sku);
            if(count_sku != 1){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROE);
            }

            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockList.add(stock);

        });
        //批量新增库存
        count_stock = this.stockMapper.insertList(stockList);
        if(count_stock < 1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROE);
        }
    }

    /**
     * 通过spu_id查询spu的详细信息
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 通过spu_id查询sku的详细信息
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        skus.forEach(sku1 -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku1.getId());
            if(stock == null){
                throw new LyException(ExceptionEnum.STOCK_NOT_FOUND);
            }
            sku1.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新goods商品信息
     * @param spu
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(Spu spu) {
        if(spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());

        //查询sku
        List<Sku> skuList = this.skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(skuList)){
            //删除sku
            this.skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            this.stockMapper.deleteByIdList(ids);
        }

        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);

        int count = this.spuMapper.updateByPrimaryKeySelective(spu);
        if(count != 1){
            System.out.println("//修改spu");
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROE);
        }

        //修改detail
        count = this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if(count != 1){
            System.out.println("//修改detail");
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROE);
        }

        //新增sku和stock
        saveSkuAndStock(spu);

        sendMsg("update", spu.getId());
    }

    /**
     * 删除商品(逻辑删除)
     * @param spuId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoods(Long spuId) {
        //1、逻辑删除spu中的记录
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setValid(false);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        //2、逻辑删除sku中的记录
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", spuId);
        List<Sku> skus = this.skuMapper.selectByExample(example);
        skus.forEach(sku -> {
            sku.setEnable(false);
            this.skuMapper.updateByPrimaryKeySelective(sku);
        });

        sendMsg("delete", spu.getId());
    }

    /**
     * 上下架商品
     * @param spuId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void goodsSoldOut(Long spuId) {
        Spu oldSpu = this.spuMapper.selectByPrimaryKey(spuId);
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", spuId);
        List<Sku> skus = this.skuMapper.selectByExample(example);

        //1、下架商品
        if(oldSpu.getSaleable()){
            oldSpu.setSaleable(false);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            //下架sku中的具体商品
            skus.forEach(sku -> {
                sku.setEnable(false);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            });
        }else{ //2、上架商品
            oldSpu.setSaleable(true);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            //上架sku中的具体商品
            skus.forEach(sku -> {
                sku.setEnable(true);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            });
        }
    }

    /**
     * 通过spuID查询spu
     * @param spuId
     * @return
     */
    public Spu querySpuById(Long spuId) {
        return this.spuMapper.selectByPrimaryKey(spuId);
    }
}
