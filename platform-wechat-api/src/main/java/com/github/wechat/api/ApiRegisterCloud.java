package com.github.wechat.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 注册
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-26 17:27
 */
@Api(tags = "注册")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/register")
public interface ApiRegisterCloud {

	@ApiOperation(notes = "注册", value = "注册")
    @IgnoreAuth
    @RequestMapping("register")
    public Result register(@ApiParam(value = "手机号",required=true) @RequestParam String mobile, 
    		@ApiParam(value = "密码",required=true) @RequestParam  String password);
}
