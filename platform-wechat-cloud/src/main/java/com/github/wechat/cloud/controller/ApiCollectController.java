package com.github.wechat.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.CollectVo;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiCollectCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiCollectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiCollectController extends ApiBaseAction implements ApiCollectCloud{
    @Autowired
    private ApiCollectService collectService;

    @Override
    public Result list(Object object, Integer typeId) {
    	UserVo loginUser = (UserVo) object;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", loginUser.getUserId());
        param.put("type_id", typeId);
        List<CollectVo> collectEntities = collectService.queryList(param);

        return ResultGenerator.genSuccessResult(collectEntities);
    }

    @Override
    public Result addordelete(Object object) {
    	UserVo loginUser = (UserVo) object;
        JSONObject jsonParam = getJsonRequest();
        Integer typeId = jsonParam.getInteger("typeId");
        Integer valueId = jsonParam.getInteger("valueId");

        Map<String, Object> param = new HashMap<>();
        param.put("user_id", loginUser.getUserId());
        param.put("type_id", typeId);
        param.put("value_id", valueId);
        List<CollectVo> collectEntities = collectService.queryList(param);
        //
        Integer collectRes = null;
        String handleType = "add";
        if (null == collectEntities || collectEntities.size() < 1) {
            CollectVo collectEntity = new CollectVo();
            collectEntity.setAdd_time(System.currentTimeMillis() / 1000);
            collectEntity.setType_id(typeId);
            collectEntity.setValue_id(valueId);
            collectEntity.setIs_attention(0);
            collectEntity.setUser_id(loginUser.getUserId());
            //添加收藏
            collectRes = collectService.save(collectEntity);
        } else {
            //取消收藏
            collectRes = collectService.delete(collectEntities.get(0).getId());
            handleType = "delete";
        }

        if (collectRes > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("type", handleType);
            return ResultGenerator.genSuccessResult(data);
        }
        return ResultGenerator.genFailResult("操作失败");
    }
}