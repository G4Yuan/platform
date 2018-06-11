package com.github.wechat.cloud.controller;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultCode;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.OrderGoodsVo;
import com.github.model.entity.wechat.OrderVo;
import com.github.model.entity.wechat.UserVo;
import com.github.model.page.Query;
import com.github.wechat.api.ApiOrderCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiKdniaoService;
import com.github.wechat.cloud.service.ApiOrderGoodsService;
import com.github.wechat.cloud.service.ApiOrderService;
import com.github.wechat.cloud.utils.ApiPageUtils;
import com.github.wechat.cloud.utils.WechatRefundApiResult;
import com.github.wechat.cloud.utils.WechatUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiOrderController extends ApiBaseAction implements ApiOrderCloud{
    @Autowired
    private ApiOrderService orderService;
    @Autowired
    private ApiOrderGoodsService orderGoodsService;
    @Autowired
    private ApiKdniaoService apiKdniaoService;

    @Override
    public Result index(Object object) {
        //
    	return ResultGenerator.genSuccessResult("");
    }

    @Override
    public Result list(Object object, Integer page, Integer size) {
    	UserVo loginUser = (UserVo) object;
        //
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", loginUser.getUserId());
        params.put("page", page);
        params.put("limit", size);
        params.put("sidx", "id");
        params.put("order", "asc");
        //查询列表数据
        Query query = new Query(params);
        List<OrderVo> orderEntityList = orderService.queryList(query);
        int total = orderService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(orderEntityList, total, query.getLimit(), query.getPage());
        //
        for (OrderVo item : orderEntityList) {
        	Map<String, Object> orderGoodsParam = new HashMap<>();
            orderGoodsParam.put("order_id", item.getId());
            //订单的商品
            List<OrderGoodsVo> goodsList = orderGoodsService.queryList(orderGoodsParam);
            Integer goodsCount = 0;
            for (OrderGoodsVo orderGoodsEntity : goodsList) {
                goodsCount += orderGoodsEntity.getNumber();
                item.setGoodsCount(goodsCount);
            }
        }
        
        return ResultGenerator.genSuccessResult(pageUtil);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Result detail(Object object, Integer orderId) {
    	
    	Map<String, Object>  resultObj = new HashMap<>();
        //
        OrderVo orderInfo = orderService.queryObject(orderId);
        if (null == orderInfo) {
            return ResultGenerator.genFailResult(ResultCode.FAIL.code, "订单不存在", "");
        }
        Map<String, Object> orderGoodsParam = new HashMap<>();
        orderGoodsParam.put("order_id", orderId);
        //订单的商品
        List<OrderGoodsVo> orderGoods = orderGoodsService.queryList(orderGoodsParam);
        //订单最后支付时间
        if (orderInfo.getOrder_status() == 0) {
            // if (moment().subtract(60, 'minutes') < moment(orderInfo.add_time)) {
//            orderInfo.final_pay_time = moment("001234", "Hmmss").format("mm:ss")
            // } else {
            //     //超过时间不支付，更新订单状态为取消
            // }
        }

        //订单可操作的选择,删除，支付，收货，评论，退换货
        Map<String, Object> handleOption = orderInfo.getHandleOption();
        //
        resultObj.put("orderInfo", orderInfo);
        resultObj.put("orderGoods", orderGoods);
        resultObj.put("handleOption", handleOption);
        if (!StringUtils.isEmpty(orderInfo.getShipping_code()) && !StringUtils.isEmpty(orderInfo.getShipping_no())) {
            // 快递
            List Traces = apiKdniaoService.getOrderTracesByJson(orderInfo.getShipping_code(), orderInfo.getShipping_no());
            resultObj.put("shippingList", Traces);
        }
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Result submit(Object object) {
    	UserVo loginUser = (UserVo) object;
    	
        Map<String, Object> resultObj = null;
        try {
            resultObj = orderService.submit(getJsonRequest(), loginUser);
            if (null != resultObj) {
                return ResultGenerator.genFailResult(MapUtils.getInteger(resultObj, "errno"), MapUtils.getString(resultObj, "errmsg"), resultObj.get("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultGenerator.genFailResult("提交失败");
    }

    @Override
    public Result cancelOrder(Object object, Integer orderId) {
        try {
            OrderVo orderVo = orderService.queryObject(orderId);
            if (orderVo.getOrder_status() == 300) {
            	return ResultGenerator.genFailResult("已发货，不能取消");
            } else if (orderVo.getOrder_status() == 301) {
            	return ResultGenerator.genFailResult("已发货，不能取消");
            }
            // 需要退款
            if (orderVo.getPay_status() == 2) {
                WechatRefundApiResult result = WechatUtil.wxRefund(orderVo.getId().toString(),
                        0.01, 0.01);
                if (result.getResult_code().equals("SUCCESS")) {
                    if (orderVo.getOrder_status() == 201) {
                        orderVo.setOrder_status(401);
                    } else if (orderVo.getOrder_status() == 300) {
                        orderVo.setOrder_status(402);
                    }
                    orderVo.setPay_status(4);
                    orderService.update(orderVo);
                    return ResultGenerator.genFailResult("取消成功");
                } else {
                    return ResultGenerator.genFailResult(ResultCode.FAIL.code, "取消成失败", "");
                }
            } else {
                orderVo.setOrder_status(101);
                orderService.update(orderVo);
                return ResultGenerator.genSuccessResult("","取消成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultGenerator.genFailResult("提交失败");
    }

    @Override
    public Result confirmOrder(Object object, Integer orderId) {
        try {
            OrderVo orderVo = orderService.queryObject(orderId);
            orderVo.setOrder_status(301);
            orderVo.setShipping_status(2);
            orderVo.setConfirm_time(new Date());
            orderService.update(orderVo);
            return ResultGenerator.genSuccessResult("","取消成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultGenerator.genFailResult("提交失败");
    }
}