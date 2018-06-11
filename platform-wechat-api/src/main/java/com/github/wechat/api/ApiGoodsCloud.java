package com.github.wechat.api;

import com.github.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static org.junit.Assert.fail;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "商品管理")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/goods")
public interface ApiGoodsCloud {
   

	@ApiOperation(notes = "商品首页", value = "商品首页")
    @IgnoreAuth
    @GetMapping("index")
    public Result index(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

	
    @ApiOperation(notes = "获取商品规格信息", value = " 获取商品规格信息")
    @IgnoreAuth
    @GetMapping("sku")
    public Result sku(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "商品ID",required=true) @RequestParam Integer id);

    
    @ApiOperation(notes = "商品详情页数据", value = " 商品详情页数据")
    @GetMapping("detail")
    public Result detail(@ApiParam(value = "商品ID",required=true) @RequestParam Integer id,
    		@ApiParam(value = "商品referrer",required=true) @RequestParam Long referrer);

    
    @ApiOperation(notes = "获取分类下的商品", value = " 获取分类下的商品")
    @GetMapping("category")
    public Result category(@ApiParam(value = "分类ID",required=true) @RequestParam Integer id);
    
    
    @ApiOperation(notes = "获取商品列表", value = "获取商品列表")
    @PostMapping("list")
    public Result list(@ApiParam(value = "用户登入信息",required=false) @RequestParam Object object,
    		@ApiParam(value = "分类ID",required=true) @RequestParam Integer categoryId,
    		@ApiParam(value = "品牌ID",required=false) @RequestParam Integer brandId, 
    		@RequestParam String keyword, 
    		@ApiParam(value = "新商品",required=false) @RequestParam Integer isNew, 
    		@ApiParam(value = "热卖商品",required=false) @RequestParam Integer isHot,
    		@ApiParam(value = "页数",required=false) @RequestParam Integer page, 
    		@ApiParam(value = "页面大小",required=false) @RequestParam Integer size,
    		@RequestParam String sort, 
    		@RequestParam String order);

    
    @ApiOperation(notes = "商品列表筛选的分类列表", value = "商品列表筛选的分类列表")
    @IgnoreAuth
    @GetMapping("filter")
    public Result filter(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "分类ID",required=true) @RequestParam  Integer categoryId,
    		@RequestParam String keyword, 
    		@ApiParam(value = "新商品",required=true) @RequestParam Integer isNew, 
    		@ApiParam(value = "热卖商品",required=true) @RequestParam Integer isHot);

    
    @ApiOperation(notes = "新品首发", value = "新品首发")
    @IgnoreAuth
    @GetMapping("new")
    public Result newAction(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

    
    @ApiOperation(notes = "人气推荐", value = "人气推荐")
    @IgnoreAuth
    @GetMapping("hot")
    public Result hot(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

    
    @ApiOperation(notes = "商品详情页的大家都在看的商品", value = "商品详情页的大家都在看的商品")
    @IgnoreAuth
    @GetMapping("related")
    public Result related(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object, 
    		@RequestParam Integer id);


    @ApiOperation(notes = "在售的商品总数", value = "在售的商品总数")
    @IgnoreAuth
    @GetMapping("count")
    public Result count(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);

    
    @ApiOperation(notes = "获取商品列表", value = "获取商品列表")
    @IgnoreAuth
    @GetMapping("productlist")
    public Result productlist(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@ApiParam(value = "分类ID",required=true) @RequestParam  Integer categoryId,
    		@ApiParam(value = "新商品",required=true) @RequestParam Integer isNew, 
    		@RequestParam Integer discount,
    		@ApiParam(value = "页数",required=false) @RequestParam(value = "page", defaultValue = "1") Integer page, 
    		@ApiParam(value = "页面大小",required=false) @RequestParam(value = "size", defaultValue = "10") Integer size,
    		@RequestParam String sort, 
    		@RequestParam String order);
}