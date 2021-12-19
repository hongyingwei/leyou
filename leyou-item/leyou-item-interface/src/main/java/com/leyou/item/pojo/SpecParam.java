package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.*;

@Table(name = "tb_spec_param")
@Data
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;

    //实体类和表中的字段不一致时，需要添加column来绑定两者之间的映射关系
    //mysql中的特有名称，也需要绑定
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
    
    // 省略getter和setter ...

}