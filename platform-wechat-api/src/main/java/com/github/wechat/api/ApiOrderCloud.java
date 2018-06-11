package com.github.wechat.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "订单")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/order")
public interface ApiOrderCloud {
   

	@ApiOperation(notes = "订单首页", value = "订单首页")
    @IgnoreAuth
    @PostMapping("index")
    public Result index(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
	
    
	@ApiOperation(notes = "获取订单列表", value = "获取订单列表")
	@PostMapping("list")
    public Result list(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "页数",required=false) @RequestParam(value = "page", defaultValue = "1") Integer page,
    		@ApiParam(value = "页面大小",required=false) @RequestParam(value = "size", defaultValue = "10") Integer size);
    
	
	@ApiOperation(notes = "获取订单详情", value = "获取订单详情")
	@PostMapping("detail")
    public Result detail(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "订单ID",required=false) @RequestParam Integer orderId);
    
	
	@ApiOperation(notes = "获取订单列表", value = "获取订单列表")
	@PostMapping("submit")
    public Result submit(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
    
	
	@ApiOperation(notes = "取消订单", value = "取消订单")
	@PostMapping("cancelOrder")
    public Result cancelOrder(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "订单ID",required=false) @RequestParam Integer orderId);

    
	@ApiOperation(notes = "确认收货", value = "确认收货")
	@PostMapping("confirmOrder")
    public Result confirmOrder(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "订单ID",required=false) @RequestParam Integer orderId);
}