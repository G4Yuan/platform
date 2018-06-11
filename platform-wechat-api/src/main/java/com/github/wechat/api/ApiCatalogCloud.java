package com.github.wechat.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.result.Result;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "分类栏目")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/catalog")
public interface ApiCatalogCloud {

    @ApiOperation(notes = "获取分类栏目数据", value = "获取分类栏目数据")
    @IgnoreAuth
    @PostMapping("index")
    public Result index(@ApiParam(value = "分类ID",required=false) @RequestParam Integer id,
    		@ApiParam(value = "页数",required=true) @RequestParam(value = "page", defaultValue = "1") Integer page,
    		@ApiParam(value = "页面大小",required=true) @RequestParam(value = "size", defaultValue = "10") Integer size);
    
    
    @ApiOperation(notes = "分类目录当前分类数据接口", value = "分类目录当前分类数据接口")
    @IgnoreAuth
    @GetMapping("current")
    public Result current(@ApiParam(value = "分类ID",required=true) @RequestParam Integer id);
}