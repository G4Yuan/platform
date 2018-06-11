package com.github.wechat.api;

import com.github.common.annotation.IgnoreAuth;
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
@Api(tags = "评论")
@FeignClient(value="wechat-cloud")
@RequestMapping("/api/comment")
public interface ApiCommentCloud {
   
	@ApiOperation(notes = "发表评论", value = "发表评论")
    @PostMapping("post")
    public Result post(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object);
	
	
	@ApiOperation(notes = "评论条数", value = "评论条数")
	@PostMapping("count")
    public Result count(@ApiParam(value = "用户登入信息",required=true) @RequestParam  @LoginUser Object object,
    		@RequestParam Integer typeId, 
    		@RequestParam Integer valueId);

	
	@ApiOperation(notes = "评论list", value = "评论list")
    @IgnoreAuth
    @RequestMapping("list")
    public Result list(@RequestParam Integer typeId, @RequestParam Integer valueId, @RequestParam Integer showType,
    		@ApiParam(value = "页数",required=true)  @RequestParam(value = "page", defaultValue = "1") Integer page, 
    		@ApiParam(value = "页面大小",required=true) @RequestParam(value = "size", defaultValue = "10") Integer size,
                       String sort, String order); 
}