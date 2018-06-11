package com.github.wechat.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.common.utils.CharUtil;
import com.github.common.utils.StringUtils;
import com.github.model.entity.wechat.CouponVo;
import com.github.model.entity.wechat.SmsLogVo;
import com.github.model.entity.wechat.UserCouponVo;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiCouponCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiCouponService;
import com.github.wechat.cloud.service.ApiUserCouponService;
import com.github.wechat.cloud.service.ApiUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ApiCouponController extends ApiBaseAction implements ApiCouponCloud{
    @Autowired
    private ApiUserService apiUserService;
    @Autowired
    private ApiCouponService apiCouponService;
    @Autowired
    private ApiUserCouponService apiUserCouponService;

    @Override
    public Result list(Object object) {
    	UserVo loginUser = (UserVo) object;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", loginUser.getUserId());
        List<CouponVo> couponVos = apiCouponService.queryUserCoupons(param);
        return ResultGenerator.genSuccessResult(couponVos);
    }

    @Override
    public Result exchange(Object object) {
    	UserVo loginUser = (UserVo) object;
        JSONObject jsonParam = getJsonRequest();
        String coupon_number = jsonParam.getString("coupon_number");
        if (StringUtils.isNullOrEmpty(coupon_number)) {
            return ResultGenerator.genFailResult("当前优惠码无效");
        }
        //
        Map<String, Object> param = new HashMap<>();
        param.put("coupon_number", coupon_number);
        List<UserCouponVo> couponVos = apiUserCouponService.queryList(param);
        UserCouponVo userCouponVo = null;
        if (null == couponVos || couponVos.size() == 0) {
        	return ResultGenerator.genFailResult("当前优惠码无效");
        }
        userCouponVo = couponVos.get(0);
        if (null != userCouponVo.getUser_id() && !userCouponVo.getUser_id().equals(0L)) {
            return ResultGenerator.genFailResult("当前优惠码已经兑换");
        }
        CouponVo couponVo = apiCouponService.queryObject(userCouponVo.getCoupon_id());
        if (null == couponVo || null == couponVo.getUse_end_date() || couponVo.getUse_end_date().before(new Date())) {
            return ResultGenerator.genFailResult("当前优惠码已经过期");
        }
        userCouponVo.setUser_id(loginUser.getUserId());
        userCouponVo.setAdd_time(new Date());
        apiUserCouponService.update(userCouponVo);
        return ResultGenerator.genSuccessResult(userCouponVo);
    }

    @Override
    public Result newuser(Object object) {
    	UserVo loginUser = (UserVo) object;
        JSONObject jsonParam = getJsonRequest();
        //
        String phone = jsonParam.getString("phone");
        String smscode = jsonParam.getString("smscode");
        // 校验短信码
        SmsLogVo smsLogVo = apiUserService.querySmsCodeByUserId(loginUser.getUserId());
        if (null != smsLogVo && !smsLogVo.getSms_code().equals(smscode)) {
            return ResultGenerator.genFailResult("短信校验失败");
        }
        // 更新手机号码
        if (!StringUtils.isNullOrEmpty(phone)) {
            if (phone.equals(loginUser.getMobile())) {
                loginUser.setMobile(phone);
                apiUserService.update(loginUser);
            }
        }
        // 判断是否是新用户
        if (!StringUtils.isNullOrEmpty(loginUser.getMobile())) {
            return ResultGenerator.genFailResult("当前优惠券只能新用户领取");
        }
        // 是否领取过了
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", loginUser.getUserId());
        params.put("send_type", 4);
        List<CouponVo> couponVos = apiCouponService.queryUserCoupons(params);
        if (null != couponVos && couponVos.size() > 0) {
            return ResultGenerator.genFailResult("已经领取过，不能重复领取");
        }
        // 领取
        Map<String, Object> couponParam = new HashMap<>();
        couponParam.put("send_type", 4);
        CouponVo newCouponConfig = apiCouponService.queryMaxUserEnableCoupon(couponParam);
        if (null != newCouponConfig) {
            UserCouponVo userCouponVo = new UserCouponVo();
            userCouponVo.setAdd_time(new Date());
            userCouponVo.setCoupon_id(newCouponConfig.getId());
            userCouponVo.setCoupon_number(CharUtil.getRandomString(12));
            userCouponVo.setUser_id(loginUser.getUserId());
            apiUserCouponService.save(userCouponVo);
            return ResultGenerator.genSuccessResult(userCouponVo);
        } else {
            return ResultGenerator.genFailResult("领取失败");
        }
    }

    /**
     * 　　转发领取红包
     */
    @RequestMapping("transActivit")
    public Result transActivit(Object object, String sourceKey, Long referrer) {
    	UserVo loginUser = (UserVo) object;
        // 是否领取过了
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", loginUser.getUserId());
        params.put("send_type", 2);
        params.put("source_key", sourceKey);
        List<CouponVo> couponVos = apiCouponService.queryUserCoupons(params);
        if (null != couponVos && couponVos.size() > 0) {
            return ResultGenerator.genFailResult(2, "已经领取过", couponVos);
        }
        // 领取
        Map<String, Object> couponParam = new HashMap<>();
        couponParam.put("send_type", 2);
        CouponVo newCouponConfig = apiCouponService.queryMaxUserEnableCoupon(couponParam);
        if (null != newCouponConfig) {
            UserCouponVo userCouponVo = new UserCouponVo();
            userCouponVo.setAdd_time(new Date());
            userCouponVo.setCoupon_id(newCouponConfig.getId());
            userCouponVo.setCoupon_number(CharUtil.getRandomString(12));
            userCouponVo.setUser_id(loginUser.getUserId());
            userCouponVo.setSource_key(sourceKey);
            userCouponVo.setReferrer(referrer);
            apiUserCouponService.save(userCouponVo);
            //
            List<UserCouponVo> userCouponVos = new ArrayList<>();
            userCouponVos.add(userCouponVo);
            //
            params = new HashMap<>();
            params.put("user_id", loginUser.getUserId());
            params.put("send_type", 2);
            params.put("source_key", sourceKey);
            couponVos = apiCouponService.queryUserCoupons(params);
            return ResultGenerator.genSuccessResult(couponVos);
        } else {
            return ResultGenerator.genFailResult("领取失败");
        }
    }
}
