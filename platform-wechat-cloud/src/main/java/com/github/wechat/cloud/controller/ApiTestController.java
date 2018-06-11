package com.github.wechat.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiTestCloud;
import com.github.wechat.cloud.service.ApiUserService;

@RestController
public class ApiTestController implements ApiTestCloud{

    @Autowired
    private ApiUserService userService;

    @Override
    public Result userInfo(Object object) {
    	UserVo loginUser = (UserVo) object;
        return ResultGenerator.genSuccessResult(loginUser);
    }

    @Override
    public Result notToken() {
        return ResultGenerator.genSuccessResult("无需token也能访问。。。");
    }

    @Override
    public Result userList(String mobile) {
        UserVo userEntity = userService.queryByMobile(mobile);
        return ResultGenerator.genSuccessResult(userEntity);
    }
}
