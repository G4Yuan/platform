package com.github.wechat.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.result.Result;


@Api(tags = "地区Region")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/region")
public interface ApiRegionCloud {

	@ApiOperation(notes = "区域列表", value = "区域列表")
    @IgnoreAuth
    @RequestMapping("list")
    public Result list(@ApiParam(value = "父ID",required=true) @RequestParam Integer parentId);

	
	@ApiOperation(notes = "省份列表", value = "省份列表")
    @IgnoreAuth
    @RequestMapping("provinceList")
    public Result provinceList();

	
	@ApiOperation(notes = "城市列表", value = "城市列表")
    @IgnoreAuth
    @RequestMapping("cityList")
    public Result provinceList(@ApiParam(value = "省份名称",required=true) @RequestParam String proviceName);
	
	
	@ApiOperation(notes = "区县列表", value = "区县列表")
    @IgnoreAuth
    @RequestMapping("distinctList")
    public Result distinctList(@ApiParam(value = "省份名称",required=true) @RequestParam String proviceName, 
    		@ApiParam(value = "城市名称",required=true) @RequestParam String cityName);

	
	@ApiOperation(notes = "具体信息", value = "具体信息")
    @IgnoreAuth
    @RequestMapping("info")
    public Result info(@ApiParam(value = "区域ID",required=true) @RequestParam Integer regionId);

	
	
	@ApiOperation(notes = "通过名称查找地区", value = "通过名称查找地区")
    @IgnoreAuth
    @RequestMapping("regionIdsByNames")
    public Result regionIdsByNames(@ApiParam(value = "省份名称",required=true) @RequestParam String provinceName, 
    		@ApiParam(value = "城市名称",required=true) @RequestParam String cityName,
    		@ApiParam(value = "区县名称",required=true) @RequestParam String districtName);
}