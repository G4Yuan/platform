package com.github.wechat.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.common.result.Result;


/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@Api(tags = "首页接口文档")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/index")
public interface ApiIndexCloud {

    
    @ApiOperation(notes = "app首页", value = "首页")
    @GetMapping("index")
    public Result index();
    
}