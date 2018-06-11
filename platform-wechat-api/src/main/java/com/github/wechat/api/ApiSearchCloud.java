package com.github.wechat.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;


/**
 * API登录授权
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-23 15:31
 */
@Api(tags = "搜索")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/search")
public interface ApiSearchCloud {

    @ApiOperation(notes = "搜索商品列表", value = "搜索商品列表")
    @RequestMapping("index")
    public Result index(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
   
    
    @ApiOperation(notes = "搜索商品", value = "搜索商品")
    @IgnoreAuth
    @GetMapping("helper")
    public Result helper(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "关键字",required=true) @RequestParam String keyword);
    
    
    @ApiOperation(notes = "清除历史", value = "清除历史")
    @RequestMapping("clearhistory")
    public Result clearhistory(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
}
