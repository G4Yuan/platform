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
@Api(tags = "购物车")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/cart")
public interface ApiCartCloud {
    
	@ApiOperation(notes = "获取购物车中的数据", value = "获取购物车中的数据")
    @PostMapping("getCart")
    public Result getCart(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);

	
	@ApiOperation(notes = "获取购物车信息，所有对购物车的增删改操作，都要重新返回购物车的信息", value = "获取购物车信息，所有对购物车的增删改操作，都要重新返回购物车的信息")
	@PostMapping("index")
    public Result index(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object); 

	
	@ApiOperation(notes = "添加商品到购物车", value = "添加商品到购物车")
	@PostMapping("add")
    public Result add(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);
	
	
	@ApiOperation(notes = "减少商品到购物车", value = "减少商品到购物车")
	@PostMapping("minus")
    public Result minus(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);
	
	
	@ApiOperation(notes = "更新指定的购物车信息", value = "更新指定的购物车信息")
	@PostMapping("update")
    public Result update(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);

	
	@ApiOperation(notes = "是否选择商品，如果已经选择，则取消选择，批量操作", value = "是否选择商品，如果已经选择，则取消选择，批量操作")
	@PostMapping("checked")
    public Result checked(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);

	
	@ApiOperation(notes = "删除选中的购物车商品，批量删除", value = "删除选中的购物车商品，批量删除")
	@PostMapping("delete")
    public Result delete(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);

	
	@ApiOperation(notes = "获取购物车商品的总件件数", value = "获取购物车商品的总件件数")
	@PostMapping("goodscount")
    public Object goodscount(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);
	
	
	@ApiOperation(notes = "订单提交前的检验和填写相关订单信息", value = "订单提交前的检验和填写相关订单信息")
	@PostMapping("checkout")
    public Object checkout(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object, 
    		@ApiParam(value = "优惠券ID",required=false) @RequestParam Integer couponId);

	
	@ApiOperation(notes = "选择优惠券列表", value = "选择优惠券列表")
    @RequestMapping("checkedCouponList")
    public Object checkedCouponList(@ApiParam(value = "用户登入信息",required=true) @RequestParam @LoginUser Object object);
}
