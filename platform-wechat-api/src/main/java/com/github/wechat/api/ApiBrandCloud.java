package com.github.wechat.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "品牌")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/brand")
public interface ApiBrandCloud {

	@ApiOperation(notes = "品牌首页", value = "品牌首页")
    @IgnoreAuth
    @GetMapping("index")
    public Result index();
	
	
	@ApiOperation(notes = "分页获取品牌", value = "分页获取品牌")
    @IgnoreAuth
    @PostMapping("list")
    public Result list(@ApiParam(value = "页数",required=true) @RequestParam(value = "page", defaultValue = "1") Integer page,
    		@ApiParam(value = "页面大小",required=true)  @RequestParam(value = "size", defaultValue = "10") Integer size); 
	
    
	
	@ApiOperation(notes = "品牌详情", value = "品牌详情")
    @IgnoreAuth
    @PostMapping("detail")
    public Result detail(@ApiParam(value = "品牌ID",required=true) @RequestParam Integer id); 
}