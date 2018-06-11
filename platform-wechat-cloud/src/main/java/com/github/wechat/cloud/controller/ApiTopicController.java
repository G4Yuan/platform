package com.github.wechat.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.TopicVo;
import com.github.model.page.Query;
import com.github.wechat.api.ApiTopicCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiTopicService;
import com.github.wechat.cloud.utils.ApiPageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@RestController
public class ApiTopicController extends ApiBaseAction implements ApiTopicCloud{
    @Autowired
    private ApiTopicService topicService;

    @Override
    public Result list(Integer page, Integer size) {
    	
    	if (page == null) {
			page = 1;
		}
    	if (size == null) {
    		size = 10;
		}
        Map<String, Object> param = new HashMap<>();
        param.put("page", page);
        param.put("limit", size);
        param.put("sidx", "id");
        param.put("order", "desc");
        param.put("fields", "id, title, price_info, scene_pic_url,subtitle");
        //查询列表数据
        Query query = new Query(param);
        List<TopicVo> topicEntities = topicService.queryList(query);
        int total = topicService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(topicEntities, total, query.getLimit(), query.getPage());
        return ResultGenerator.genSuccessResult(pageUtil);
    }

    @Override
    public Result detail(Object object, Integer id) {
        TopicVo topicEntity = topicService.queryObject(id);
        return ResultGenerator.genSuccessResult(topicEntity);
    }

    @Override
    public Result related(Object object, Integer id) {
        Map<String, Object> param = new HashMap<>();
        param.put("limit", 4);
        List<TopicVo> topicEntities = topicService.queryList(param);
        return ResultGenerator.genSuccessResult(topicEntities);
    }
}