package com.github.wechat.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "足迹")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/footprint")
public interface ApiFootprintCloud {

    @ApiOperation(notes = "删除足迹", value = "删除足迹")
    @GetMapping("delete")
    public Result delete(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "足迹ID",required=true) @RequestParam Integer footprintId);

    
    @ApiOperation(notes = "获取足迹列表", value = "获取足迹列表")
    @GetMapping("list")
    public Result list(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "页数",required=true) @RequestParam(value = "page", defaultValue = "1") Integer page,
    		@ApiParam(value = "页面大小",required=true) @RequestParam(value = "size", defaultValue = "10") Integer size);
   
    
    @ApiOperation(notes = "分享列表", value = "分享列表")
    @PostMapping("sharelist")
    public Result sharelist(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "页数",required=true) @RequestParam(value = "page", defaultValue = "1") Integer page,
    		@ApiParam(value = "页数",required=true) @RequestParam(value = "size", defaultValue = "10") Integer size);
}