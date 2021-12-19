package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**商品规格Mapper
 * @author
 * @description
 */
public interface SpecParamMapper extends Mapper<SpecParam> {
    /**
     * 通过group_id删除tb_spec_param中对应的类别中的specParam参数
     * @param id
     */
    @Delete("DELETE FROM tb_spec_param WHERE cid IN (SELECT cid FROM tb_spec_group WHERE id=#{id}) and group_id=#{id}")
    void deleteSpecParamByCidAndGroupID(@Param("id") Long id);
}
