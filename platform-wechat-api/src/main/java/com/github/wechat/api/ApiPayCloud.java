package com.github.wechat.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.common.annotation.LoginUser;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "支付")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/pay")
public interface ApiPayCloud {
    

	@ApiOperation(notes = "支付首页", value = "支付首页")
	@RequestMapping("index")
    public Result index(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

	
	@ApiOperation(notes = "获取支付的请求参数", value = "获取支付的请求参数")
    @RequestMapping("prepay")
    public Result payPrepay(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@ApiParam(value = "订单ID",required=true) @RequestParam Integer orderId);

   
	@ApiOperation(notes = "获取支付的请求参数", value = "获取支付的请求参数")
    @RequestMapping(value = "/notify", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public void notify(HttpServletRequest request, HttpServletResponse response);

	
	@ApiOperation(notes = "订单退款请求", value = "订单退款请求")
    @RequestMapping("refund")
    public Result refund(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
	@ApiParam(value = "订单ID",required=true) @RequestParam Integer orderId);


}