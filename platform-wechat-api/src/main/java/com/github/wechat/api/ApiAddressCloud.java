package com.github.wechat.api;

import com.github.common.annotation.IgnoreAuth;
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
@Api(tags = "收获地址")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/address")
public interface ApiAddressCloud {
    
    @ApiOperation(notes = "获取用户的收货地址", value = "获取用户的收货地址")
    @PostMapping("list")
    public Result list(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object); 

    @ApiOperation(notes = "获取收货地址的详情", value = "获取收货地址的详情")
    @IgnoreAuth
    @PostMapping("detail")
    public Object detail(@ApiParam(value = "地址ID",required=true) @RequestParam Integer id); 
    
    @ApiOperation(notes = "添加或更新收货地址", value = "添加或更新收货地址")
    @PostMapping("save")
    public Result save(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);
    
    @ApiOperation(notes = "删除指定的收货地址", value = "删除指定的收货地址")
    @IgnoreAuth
    @RequestMapping("delete")
    public Result delete();
    
}