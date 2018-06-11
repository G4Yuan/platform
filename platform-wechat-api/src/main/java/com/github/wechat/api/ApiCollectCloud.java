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
 * 描述: ApiIndexController <br>
 */
@Api(tags = "用户收藏")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/collect")
public interface ApiCollectCloud {

	@ApiOperation(notes = "获取用户收藏", value = "获取用户收藏")
	@PostMapping("list")
    public Result list(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, @RequestParam Integer typeId);

	
	@ApiOperation(notes = "新增或删除用户收藏", value = "新增或删除用户收藏")
	@PostMapping("addordelete")
    public Result addordelete(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
}