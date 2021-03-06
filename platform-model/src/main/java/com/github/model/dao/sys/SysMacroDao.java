package com.github.model.dao.sys;

import org.apache.ibatis.annotations.Param;

import com.github.model.dao.BaseDao;
import com.github.model.entity.sys.SysMacroEntity;

import java.util.List;

/**
 * 通用字典表Dao
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-22 11:48:16
 */
public interface SysMacroDao extends BaseDao<SysMacroEntity> {

    /**
     * 查询数据字段
     * @param value
     * @return
     */
    List<SysMacroEntity> queryMacrosByValue(@Param("value") String value);
}
