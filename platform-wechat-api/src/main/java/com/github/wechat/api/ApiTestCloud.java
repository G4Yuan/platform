package com.github.wechat.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * API测试接口
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-23 15:47
 */
@Api(tags = "API测试接口")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/test")
public interface ApiTestCloud {

    
	@ApiOperation(notes = "获取用户信息", value = "获取用户信息")
    @GetMapping("userInfo")
    public Result userInfo(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
	
	
	@ApiOperation(notes = "忽略Token验证测试", value = "忽略Token验证测试")
    @IgnoreAuth
    @GetMapping("notToken")
    public Result notToken();



	@ApiOperation(notes = "根据手机号查询用户信息接口测试方法", value = "根据手机号查询用户信息接口测试方法")
    @IgnoreAuth
    @GetMapping("userListByMobile")
    public Result userList(@ApiParam(value = "手机号",required=true) @RequestParam String mobile);
}
