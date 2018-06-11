package com.github.wechat.api;


import io.swagger.annotations.Api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.github.common.annotation.IgnoreAuth;
import com.github.common.result.Result;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-09-08 13:20<br>
 * 描述: ApiUploadController <br>
 */
@Api(tags = "上传")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/upload")
public interface ApiUploadCloud {

    
    @IgnoreAuth
    @RequestMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception;
}