package com.github.wechat.api;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * API登录授权
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-23 15:31
 */
@Api(tags = "API登录授权")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/auth")
public interface ApiAuthCloud {

	@ApiOperation(notes = "手机登录", value = "手机登录")
    @IgnoreAuth
    @PostMapping("login")
    public Result login(@ApiParam(value = "手机号码",required=true) @RequestParam String mobile, 
    		@ApiParam(value = "密码",required=true) @RequestParam String password);


	@ApiOperation(notes = "微信登录", value = "微信登录")
    @IgnoreAuth
    @RequestMapping("login_by_weixin")
    public Object loginByWeixin();
}
