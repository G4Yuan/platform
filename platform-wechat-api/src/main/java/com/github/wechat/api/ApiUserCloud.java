package com.github.wechat.api;

import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "用户")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/user")
public interface ApiUserCloud {
    
	@ApiOperation(notes = "发送短信", value = "发送短信")
    @RequestMapping("smscode")
    public Result smscode(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
    
    
    
	@ApiOperation(notes = "获取当前会员等级", value = "获取当前会员等级")
    @RequestMapping("getUserLevel")
    public Result getUserLevel(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

	
	
	@ApiOperation(notes = "绑定手机", value = "绑定手机")
    @RequestMapping("bindMobile")
    public Result bindMobile(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
}