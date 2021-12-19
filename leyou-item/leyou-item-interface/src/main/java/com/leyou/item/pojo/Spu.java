package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @KeySql(useGeneratedKeys=true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    @JsonIgnore
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    @JsonIgnore
    private Date lastUpdateTime;// 最后修改时间
	// 省略getter和setter
    /*
     * https://www.cnblogs.com/stit/p/3954411.html
     *
     * 1 serialization会忽略掉
     * Java的serialization提供了一种持久化对象实例的机制。当持久化对象时，可能有一个特殊的对象数据成员，我们不想用serialization机制来保存它。
     * 为了在一个特定对象的一个域上关闭serialization，可以在这个域前加上关键字transient
     *
     * 2 不跟数据库表做映射 就是表中没有这个字段
     * @Transient表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性.
     * 在项目查了下，mongodb中确实没有此字段，因此也适用于mongodb
     * */

    @Transient
    private String cname;

    @Transient
    private String bname;

    @Transient
    private SpuDetail spuDetail;// 商品详情

    @Transient
    private List<Sku> skus;// sku列表

}