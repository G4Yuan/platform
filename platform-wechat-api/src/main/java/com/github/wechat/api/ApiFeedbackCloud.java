package com.github.wechat.api;

import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiFeedbackController <br>
 */
@Api(tags = "反馈")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/feedback")
public interface ApiFeedbackCloud {

    /**
     * 添加反馈
     */
	 @ApiOperation(notes = "添加反馈", value = "添加反馈")
    @PostMapping("save")
    public Result save(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
}