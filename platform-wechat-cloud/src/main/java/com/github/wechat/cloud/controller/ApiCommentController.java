package com.github.wechat.cloud.controller;

import com.github.common.utils.Base64;
import com.github.common.utils.CharUtil;
import com.github.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.CommentPictureVo;
import com.github.model.entity.wechat.CommentVo;
import com.github.model.entity.wechat.CouponVo;
import com.github.model.entity.wechat.UserCouponVo;
import com.github.model.entity.wechat.UserVo;
import com.github.model.page.Query;
import com.github.wechat.api.ApiCommentCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiCommentPictureService;
import com.github.wechat.cloud.service.ApiCommentService;
import com.github.wechat.cloud.service.ApiCouponService;
import com.github.wechat.cloud.service.ApiUserCouponService;
import com.github.wechat.cloud.service.ApiUserService;
import com.github.wechat.cloud.utils.ApiPageUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ApiCommentController extends ApiBaseAction implements ApiCommentCloud{
    @Autowired
    private ApiCommentService commentService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private ApiCommentPictureService commentPictureService;
    @Autowired
    private ApiCouponService apiCouponService;
    @Autowired
    private ApiUserCouponService apiUserCouponService;

    @Override
    public Result post(Object object) {
    	UserVo loginUser = (UserVo) object;
    	
        Map<String, Object> resultObj = new HashMap<>();
        //
        JSONObject jsonParam = getJsonRequest();
        Integer typeId = jsonParam.getInteger("typeId");
        Integer valueId = jsonParam.getInteger("valueId");
        String content = jsonParam.getString("content");
        JSONArray imagesList = jsonParam.getJSONArray("imagesList");
        CommentVo commentEntity = new CommentVo();
        commentEntity.setType_id(typeId);
        commentEntity.setValue_id(valueId);
        commentEntity.setContent(content);
        commentEntity.setStatus(0);
        //
        commentEntity.setAdd_time(System.currentTimeMillis() / 1000);
        commentEntity.setUser_id(loginUser.getUserId());
        commentEntity.setContent(Base64.encode(commentEntity.getContent()));
        Integer insertId = commentService.save(commentEntity);
        //
        if (insertId > 0 && null != imagesList && imagesList.size() > 0) {
            int i = 0;
            for (Object imgLink : imagesList) {
                i++;
                CommentPictureVo pictureVo = new CommentPictureVo();
                pictureVo.setComment_id(insertId);
                pictureVo.setPic_url(imgLink.toString());
                pictureVo.setSort_order(i);
                commentPictureService.save(pictureVo);
            }
        }
        // 是否领取优惠券
        if (insertId > 0 && typeId == 0) {
            // 当前评价的次数
            Map<String, Object> param = new HashMap<>();
            param.put("value_id", valueId);
            List<CommentVo> commentVos = commentService.queryList(param);
            boolean hasComment = false;
            for (CommentVo commentVo : commentVos) {
                if (commentVo.getUser_id().equals(loginUser.getUserId())
                        && !commentVo.getId().equals(insertId)) {
                    hasComment = true;
                }
            }
            if (!hasComment) {
                Map<String, Object> couponParam = new HashMap<>();
                couponParam.put("send_type", 6);
                CouponVo newCouponConfig = apiCouponService.queryMaxUserEnableCoupon(couponParam);
                if (null != newCouponConfig
                        && newCouponConfig.getMin_transmit_num() >= commentVos.size()) {
                    UserCouponVo userCouponVo = new UserCouponVo();
                    userCouponVo.setAdd_time(new Date());
                    userCouponVo.setCoupon_id(newCouponConfig.getId());
                    userCouponVo.setCoupon_number(CharUtil.getRandomString(12));
                    userCouponVo.setUser_id(loginUser.getUserId());
                    apiUserCouponService.save(userCouponVo);
                    resultObj.put("coupon", newCouponConfig);
                }
            }
        }
        if (insertId > 0) {
        	return ResultGenerator.genSuccessResult("评论添加成功", resultObj);
        } else {
            return ResultGenerator.genFailResult("评论保存失败");
        }
    }

    @Override
    public Result count(Object object, Integer typeId, Integer valueId) {
        Map<String, Object> resultObj = new HashMap<>();
        //
        Map<String, Object> param = new HashMap<>();
        param.put("type_id", typeId);
        param.put("value_id", valueId);
        Integer allCount = commentService.queryTotal(param);
        Integer hasPicCount = commentService.queryhasPicTotal(param);
        //
        resultObj.put("allCount", allCount);
        resultObj.put("hasPicCount", hasPicCount);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result list(Integer typeId, Integer valueId, Integer showType,
                       @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
                       String sort, String order) {
    	
        List<CommentVo> commentList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("type_id", typeId);
        param.put("value_id", valueId);
        param.put("page", page);
        param.put("limit", size);
        if (StringUtils.isNullOrEmpty(sort)) {
            param.put("order", "desc");
        } else {
            param.put("order", sort);
        }
        if (StringUtils.isNullOrEmpty(order)) {
            param.put("sidx", "id");
        } else {
            param.put("sidx", order);
        }
        if (null != showType && showType == 1) {
            param.put("hasPic", 1);
        }
        //查询列表数据
        Query query = new Query(param);
        commentList = commentService.queryList(query);
        int total = commentService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(commentList, total, query.getLimit(), query.getPage());
        //
        for (CommentVo commentItem : commentList) {
            commentItem.setContent(Base64.decode(commentItem.getContent()));
            commentItem.setUser_info(userService.queryObject(commentItem.getUser_id()));

            Map<String, Object> paramPicture = new HashMap<>();
            paramPicture.put("comment_id", commentItem.getId());
            List<CommentPictureVo> commentPictureEntities = commentPictureService.queryList(paramPicture);
            commentItem.setPic_list(commentPictureEntities);
        }
        return ResultGenerator.genSuccessResult(pageUtil);
    }
}