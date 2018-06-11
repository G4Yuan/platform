package com.github.model.dao.wechat;

import com.github.model.dao.BaseDao;
import com.github.model.entity.wechat.CommentVo;

import java.util.Map;

/**
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-11 09:14:26
 */
public interface ApiCommentMapper extends BaseDao<CommentVo> {
    int queryhasPicTotal(Map<String, Object> map);
}
