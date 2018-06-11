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
 * API优惠券管理
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-23 15:31
 */
@Api(tags = "优惠券")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/coupon")
public interface ApiCouponCloud {

	@ApiOperation(notes = "获取优惠券列表", value = "获取优惠券列表")
    @PostMapping("list")
    public Result list(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

	
	@ApiOperation(notes = "兑换优惠券", value = "兑换优惠券")
	@PostMapping("exchange")
    public Result exchange(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object); 

	
	@ApiOperation(notes = "填写手机号码，领券", value = "填写手机号码，领券")
	@PostMapping("newuser")
    public Result newuser(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
	
	
	@ApiOperation(notes = "转发领取红包", value = "转发领取红包")
	@PostMapping("transActivit")
    public Result transActivit(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@RequestParam String sourceKey, 
    		@RequestParam Long referrer);
}
