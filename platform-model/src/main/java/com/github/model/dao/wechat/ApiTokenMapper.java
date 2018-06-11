package com.github.model.dao.wechat;

import com.github.model.dao.BaseDao;
import com.github.model.entity.wechat.TokenEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Token
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-23 15:22:07
 */
public interface ApiTokenMapper extends BaseDao<TokenEntity> {

    TokenEntity queryByUserId(@Param("userId") Long userId);

    TokenEntity queryByToken(@Param("token") String token);

}
