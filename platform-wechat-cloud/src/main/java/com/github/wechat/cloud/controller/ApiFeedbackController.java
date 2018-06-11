package com.github.wechat.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.FeedbackVo;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiFeedbackCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiFeedbackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiFeedbackController <br>
 */
@RestController
public class ApiFeedbackController extends ApiBaseAction implements ApiFeedbackCloud{
    @Autowired
    private ApiFeedbackService feedbackService;

    @Override
    public Result save(Object object) {
    	UserVo loginUser = (UserVo) object;
        JSONObject feedbackJson = super.getJsonRequest();
        if (null != feedbackJson) {
            FeedbackVo feedbackVo = new FeedbackVo();
            feedbackVo.setUserId(loginUser.getUserId().intValue());
            feedbackVo.setUserName(loginUser.getUsername());
            feedbackVo.setMobile(feedbackJson.getString("mobile"));
            feedbackVo.setFeedType(feedbackJson.getInteger("index"));
            feedbackVo.setStatus(1);
            feedbackVo.setContent(feedbackJson.getString("content"));
            feedbackVo.setAddTime(new Date());
            feedbackService.save(feedbackVo);
            return ResultGenerator.genSuccessResult("","感谢你的反馈");
        }
        return ResultGenerator.genFailResult("反馈失败");
    }
}