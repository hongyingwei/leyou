package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author
 * @description
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long>{

}
