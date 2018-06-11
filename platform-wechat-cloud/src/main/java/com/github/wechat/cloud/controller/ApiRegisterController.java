package com.github.wechat.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.common.validator.Assert;
import com.github.wechat.api.ApiRegisterCloud;
import com.github.wechat.cloud.service.ApiUserService;

@RestController
public class ApiRegisterController implements ApiRegisterCloud{
    @Autowired
    private ApiUserService userService;

    @Override
    public Result register(String mobile, String password) {
        Assert.isBlank(mobile, "手机号不能为空");
        Assert.isBlank(password, "密码不能为空");

        userService.save(mobile, password);

        return ResultGenerator.genSuccessResult();
    }
}
