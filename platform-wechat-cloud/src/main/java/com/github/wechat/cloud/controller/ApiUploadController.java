package com.github.wechat.cloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.common.exception.RRException;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.wechat.api.ApiUploadCloud;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-09-08 13:20<br>
 * 描述: ApiUploadController <br>
 */
@RestController
@RequestMapping("/api/upload")
public class ApiUploadController implements ApiUploadCloud {

    @Override
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
//        if (file.isEmpty()) {
//            throw new RRException("上传文件不能为空");
//        }
//        //上传文件
//        String url = OSSFactory.build().upload(file);
//        return ResultGenerator.genSuccessResult(url);
    	return null;
    }
}