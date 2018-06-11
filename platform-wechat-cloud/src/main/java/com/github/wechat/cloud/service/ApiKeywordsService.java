package com.github.wechat.cloud.service;

import com.github.model.dao.wechat.ApiKeywordsMapper;
import com.github.model.entity.wechat.KeywordsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ApiKeywordsService {
    @Autowired
    private ApiKeywordsMapper keywordsDao;


    public KeywordsVo queryObject(Integer id) {
        return keywordsDao.queryObject(id);
    }


    public List<KeywordsVo> queryList(Map<String, Object> map) {
        return keywordsDao.queryList(map);
    }


    public int queryTotal(Map<String, Object> map) {
        return keywordsDao.queryTotal(map);
    }


    public void save(KeywordsVo goods) {
        keywordsDao.save(goods);
    }


    public void update(KeywordsVo goods) {
        keywordsDao.update(goods);
    }


    public void delete(Integer id) {
        keywordsDao.delete(id);
    }


    public void deleteBatch(Integer[] ids) {
        keywordsDao.deleteBatch(ids);
    }

    @SuppressWarnings("rawtypes")
	public List<Map> hotKeywordList(Map<String, Object> map) {
        return keywordsDao.hotKeywordList(map);
    }
}
