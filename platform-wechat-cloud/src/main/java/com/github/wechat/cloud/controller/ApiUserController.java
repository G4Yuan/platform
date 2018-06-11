package com.github.wechat.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.SmsLogVo;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiUserCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ApiUserController extends ApiBaseAction implements ApiUserCloud{
    @Autowired
    private ApiUserService userService;
//    @Autowired
//    private SysConfigService sysConfigService;

    @Override
    public Result smscode(Object object) {
//    	UserVo loginUser = (UserVo) object;
//    	
//        JSONObject jsonParams = getJsonRequest();
//        String phone = jsonParams.getString("phone");
//        // 一分钟之内不能重复发送短信
//        SmsLogVo smsLogVo = userService.querySmsCodeByUserId(loginUser.getUserId());
//        if (null != smsLogVo && (System.currentTimeMillis() / 1000 - smsLogVo.getLog_date()) < 1 * 60) {
//            return ResultGenerator.genSuccessResult("短信已发送");
//        }
//        //生成验证码
//        String sms_code = CharUtil.getRandomNum(4);
//        String msgContent = "您的验证码是：" + sms_code + "，请在页面中提交验证码完成验证。";
//        // 发送短信
//        String result = "";
//        //获取云存储配置信息
//        SmsConfig config = sysConfigService.getConfigObject(ConfigConstant.SMS_CONFIG_KEY, SmsConfig.class);
//        if (StringUtils.isNullOrEmpty(config)) {
//            throw new RRException("请先配置短信平台信息");
//        }
//        if (StringUtils.isNullOrEmpty(config.getName())) {
//            throw new RRException("请先配置短信平台用户名");
//        }
//        if (StringUtils.isNullOrEmpty(config.getPwd())) {
//            throw new RRException("请先配置短信平台密钥");
//        }
//        if (StringUtils.isNullOrEmpty(config.getSign())) {
//            throw new RRException("请先配置短信平台签名");
//        }
//        try {
//            /**
//             * 状态,发送编号,无效号码数,成功提交数,黑名单数和消息，无论发送的号码是多少，一个发送请求只返回一个sendid，如果响应的状态不是“0”，则只有状态和消息
//             */
//            result = SmsUtil.crSendSms(config.getName(), config.getPwd(), phone, msgContent, config.getSign(),
//                    DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), "");
//        } catch (Exception e) {
//
//        }
//        String arr[] = result.split(",");
//
//        if ("0".equals(arr[0])) {
//            smsLogVo = new SmsLogVo();
//            smsLogVo.setLog_date(System.currentTimeMillis() / 1000);
//            smsLogVo.setUser_id(loginUser.getUserId());
//            smsLogVo.setPhone(phone);
//            smsLogVo.setSms_code(sms_code);
//            smsLogVo.setSms_text(msgContent);
//            userService.saveSmsCodeLog(smsLogVo);
//            return ResultGenerator.genSuccessResult("短信发送成功");
//        } else {
//            return ResultGenerator.genFailResult("短信发送失败");
//        }
    	return null;
    }

    @Override
    public Result getUserLevel(Object object) {
    	UserVo loginUser = (UserVo) object;
        String userLevel = userService.getUserLevel(loginUser);
        return ResultGenerator.genSuccessResult(userLevel);
    }

    @Override
    public Result bindMobile(Object object) {
    	UserVo loginUser = (UserVo) object;
        JSONObject jsonParams = getJsonRequest();
        SmsLogVo smsLogVo = userService.querySmsCodeByUserId(loginUser.getUserId());

        String mobile_code = jsonParams.getString("mobile_code");
        if (!mobile_code.equals(smsLogVo.getSms_code())) {
            return ResultGenerator.genFailResult("手机绑定失败");
        }
        return ResultGenerator.genSuccessResult("手机绑定成功");
    }
}