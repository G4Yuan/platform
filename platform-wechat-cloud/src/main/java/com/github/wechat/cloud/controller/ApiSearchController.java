package com.github.wechat.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.KeywordsVo;
import com.github.model.entity.wechat.SearchHistoryVo;
import com.github.model.entity.wechat.UserVo;
import com.github.model.page.Query;
import com.github.wechat.api.ApiSearchCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiKeywordsService;
import com.github.wechat.cloud.service.ApiSearchHistoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiSearchController extends ApiBaseAction implements ApiSearchCloud{
    @Autowired
    private ApiKeywordsService keywordsService;
    @Autowired
    private ApiSearchHistoryService searchHistoryService;

    @SuppressWarnings("rawtypes")
	@Override
    public Result index(Object object) {
    	UserVo loginUser = (UserVo) object;
    	
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("is_default", 1);
        param.put("page", 1);
        param.put("limit", 1);
        param.put("sidx", "id");
        param.put("order", "asc");
        List<KeywordsVo> keywordsEntityList = keywordsService.queryList(param);
        //取出输入框默认的关键词
        KeywordsVo defaultKeyword = null != keywordsEntityList && keywordsEntityList.size() > 0 ? keywordsEntityList.get(0) : null;
        //取出热闹关键词
        param = new HashMap<>();
        param.put("fields", "distinct keyword,is_hot");
        param.put("page", 1);
        param.put("limit", 10);
        param.put("sidx", "id");
        param.put("order", "asc");
        Query query = new Query(param);
        List<Map> hotKeywordList = keywordsService.hotKeywordList(query);
        //
        param = new HashMap<>();
        param.put("user_id", loginUser.getUserId());
        param.put("fields", "distinct keyword");
        param.put("page", 1);
        param.put("limit", 10);
        param.put("sidx", "id");
        param.put("order", "asc");
        List<SearchHistoryVo> historyVoList = searchHistoryService.queryList(param);
        String[] historyKeywordList = new String[historyVoList.size()];
        if (null != historyVoList) {
            int i = 0;
            for (SearchHistoryVo historyVo : historyVoList) {
                historyKeywordList[i] = historyVo.getKeyword();
                i++;
            }
        }
        //
        resultObj.put("defaultKeyword", defaultKeyword);
        resultObj.put("historyKeywordList", historyKeywordList);
        resultObj.put("hotKeywordList", hotKeywordList);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result helper(Object object, String keyword) {
    	
        Map<String, Object> param = new HashMap<>();
        param.put("fields", "distinct keyword");
        param.put("keyword", keyword);
        param.put("limit", 10);
        param.put("offset", 0);
        List<KeywordsVo> keywords = keywordsService.queryList(param);
        String[] keys = new String[keywords.size()];
        if (null != keywords) {
            int i = 0;
            for (KeywordsVo keywordsVo : keywords) {
                keys[i] = keywordsVo.getKeyword();
                i++;
            }
        }
        //
        return ResultGenerator.genSuccessResult(keys);
    }

    /**
     * 　　clearhistory
     */
    @RequestMapping("clearhistory")
    public Result clearhistory(Object object) {
    	UserVo loginUser = (UserVo) object;
        searchHistoryService.deleteByUserId(loginUser.getUserId());
        //
        return ResultGenerator.genSuccessResult("");
    }
}
