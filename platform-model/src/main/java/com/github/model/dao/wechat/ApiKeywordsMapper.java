package com.github.model.dao.wechat;

import com.github.model.dao.BaseDao;
import com.github.model.entity.wechat.KeywordsVo;

import java.util.List;
import java.util.Map;

/**
 * 热闹关键词表
 * 
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-11 09:16:46
 */
@SuppressWarnings("rawtypes")
public interface ApiKeywordsMapper extends BaseDao<KeywordsVo> {
	List<Map> hotKeywordList(Map param);
}
