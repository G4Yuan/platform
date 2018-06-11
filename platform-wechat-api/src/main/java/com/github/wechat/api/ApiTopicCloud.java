package com.github.wechat.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "话题")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/topic")
public interface ApiTopicCloud {
   
	@ApiOperation(notes = "话题列表", value = "话题列表")
	@IgnoreAuth
    @PostMapping("list")
    public Result list(@ApiParam(value = "当前页",required=true) @RequestParam Integer page,
    		@ApiParam(value = "页面大小",required=true) @RequestParam Integer size);

	
	@ApiOperation(notes = "话题详情", value = "话题详情")
    @IgnoreAuth
    @RequestMapping("detail")
    public Result detail(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "话题ID",required=false)  @RequestParam Integer id);

	
	
	@ApiOperation(notes = "话题联系", value = "话题联系")
    @IgnoreAuth
    @RequestMapping("related")
    public Result related(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "话题ID",required=false)  @RequestParam Integer id);
}