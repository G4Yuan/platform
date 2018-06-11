package com.github.wechat.cloud.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.StringUtils;
import com.github.common.result.Result;
import com.github.common.result.ResultCode;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.AddressVo;
import com.github.model.entity.wechat.CartVo;
import com.github.model.entity.wechat.CouponVo;
import com.github.model.entity.wechat.GoodsSpecificationVo;
import com.github.model.entity.wechat.GoodsVo;
import com.github.model.entity.wechat.ProductVo;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiCartCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.dto.CouponInfoVo;
import com.github.wechat.cloud.service.ApiAddressService;
import com.github.wechat.cloud.service.ApiCartService;
import com.github.wechat.cloud.service.ApiCouponService;
import com.github.wechat.cloud.service.ApiGoodsService;
import com.github.wechat.cloud.service.ApiGoodsSpecificationService;
import com.github.wechat.cloud.service.ApiProductService;

@RestController
public class ApiCartController extends ApiBaseAction implements ApiCartCloud {

	@Autowired
	private ApiCartService cartService;
	@Autowired
	private ApiGoodsService goodsService;
	@Autowired
	private ApiProductService productService;
	@Autowired
	private ApiGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private ApiAddressService addressService;
	@Autowired
	private ApiCouponService apiCouponService;

	@Override
	public Result getCart(Object object) {
		UserVo loginUser = (UserVo) object;
		Map<String, Object> resultObj = new HashMap<>();
		// 查询列表数据
		Map<String, Object> param = new HashMap<>();
		param.put("user_id", loginUser.getUserId());
		List<CartVo> cartList = cartService.queryList(param);
		// 获取购物车统计信息
		Integer goodsCount = 0;
		BigDecimal goodsAmount = new BigDecimal(0.00);
		Integer checkedGoodsCount = 0;
		BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
		for (CartVo cartItem : cartList) {
			goodsCount += cartItem.getNumber();
			goodsAmount = goodsAmount.add(cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
			if (null != cartItem.getChecked() && 1 == cartItem.getChecked()) {
				checkedGoodsCount += cartItem.getNumber();
				checkedGoodsAmount = checkedGoodsAmount
						.add(cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
			}
		}
		// 获取优惠信息提示
		Map<String, Object> couponParam = new HashMap<>();
		couponParam.put("enabled", true);
		Integer[] send_types = new Integer[] { 0, 7 };
		couponParam.put("send_types", send_types);
		List<CouponInfoVo> couponInfoList = new ArrayList<>();
		List<CouponVo> couponVos = apiCouponService.queryList(couponParam);
		if (null != couponVos && couponVos.size() > 0) {
			CouponInfoVo fullCutVo = new CouponInfoVo();
			BigDecimal fullCutDec = new BigDecimal(0);
			BigDecimal minAmount = new BigDecimal(100000);
			for (CouponVo couponVo : couponVos) {
				BigDecimal difDec = couponVo.getMin_goods_amount().subtract(checkedGoodsAmount).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				if (couponVo.getSend_type() == 0 && difDec.doubleValue() > 0.0
						&& minAmount.compareTo(couponVo.getMin_goods_amount()) > 0) {
					fullCutDec = couponVo.getType_money();
					minAmount = couponVo.getMin_goods_amount();
					fullCutVo.setType(1);
					fullCutVo.setMsg(couponVo.getName() + "，还差" + difDec + "元");
				} else if (couponVo.getSend_type() == 0 && difDec.doubleValue() < 0.0
						&& fullCutDec.compareTo(couponVo.getType_money()) < 0) {
					fullCutDec = couponVo.getType_money();
					fullCutVo.setType(0);
					fullCutVo.setMsg("可使用满减券" + couponVo.getName());
				}
				if (couponVo.getSend_type() == 7 && difDec.doubleValue() > 0.0) {
					CouponInfoVo cpVo = new CouponInfoVo();
					cpVo.setMsg("满￥" + couponVo.getMin_amount() + "元免配送费，还差" + difDec + "元");
					cpVo.setType(1);
					couponInfoList.add(cpVo);
				} else if (couponVo.getSend_type() == 7) {
					CouponInfoVo cpVo = new CouponInfoVo();
					cpVo.setMsg("满￥" + couponVo.getMin_amount() + "元免配送费");
					couponInfoList.add(cpVo);
				}
			}
			if (!StringUtils.isNullOrEmpty(fullCutVo.getMsg())) {
				couponInfoList.add(fullCutVo);
			}
		}
		resultObj.put("couponInfoList", couponInfoList);
		resultObj.put("cartList", cartList);
		//
		Map<String, Object> cartTotal = new HashMap<>();
		cartTotal.put("goodsCount", goodsCount);
		cartTotal.put("goodsAmount", goodsAmount);
		cartTotal.put("checkedGoodsCount", checkedGoodsCount);
		cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);
		//
		resultObj.put("cartTotal", cartTotal);
		return ResultGenerator.genSuccessResult(resultObj);
	}

	@Override
	public Result index(Object object) {
		UserVo loginUser = (UserVo) object;
		return ResultGenerator.genSuccessResult(getCart(loginUser));
	}

	// private String[] getSpecificationIdsArray(String ids){
	// String[] idsArray = null;
	// if (org.apache.commons.lang.StringUtils.isNotEmpty(ids)){
	// String[] tempArray = ids.split("_");
	// if (null != tempArray && tempArray.length > 0){
	// idsArray = tempArray;
	// }
	// }
	// return idsArray;
	// }
	@Override
	public Result add(Object object) {
		UserVo loginUser = (UserVo) object;
		JSONObject jsonParam = getJsonRequest();
		Integer goodsId = jsonParam.getInteger("goodsId");
		Integer productId = jsonParam.getInteger("productId");
		Integer number = jsonParam.getInteger("number");
		// 判断商品是否可以购买
		GoodsVo goodsInfo = goodsService.queryObject(goodsId);
		if (null == goodsInfo || goodsInfo.getIs_delete() == 1 || goodsInfo.getIs_on_sale() != 1) {
			return ResultGenerator.genFailResult(ResultCode.FAIL.code, "商品已下架", "");
		}
		// 取得规格的信息,判断规格库存
		ProductVo productInfo = productService.queryObject(productId);
		if (null == productInfo || productInfo.getGoods_number() < number) {
			return ResultGenerator.genFailResult(ResultCode.FAIL.code, "库存不足", "");
		}

		// 判断购物车中是否存在此规格商品
		Map<String, Object> cartParam = new HashMap<>();
		cartParam.put("goods_id", goodsId);
		cartParam.put("product_id", productId);
		cartParam.put("user_id", loginUser.getUserId());
		List<CartVo> cartInfoList = cartService.queryList(cartParam);
		CartVo cartInfo = null != cartInfoList && cartInfoList.size() > 0 ? cartInfoList.get(0) : null;
		if (null == cartInfo) {
			// 添加操作
			// 添加规格名和值
			String[] goodsSepcifitionValue = null;
			if (null != productInfo.getGoods_specification_ids()
					&& productInfo.getGoods_specification_ids().length() > 0) {
				Map<String, Object> specificationParam = new HashMap<>();
				String[] idsArray = getSpecificationIdsArray(productInfo.getGoods_specification_ids());
				specificationParam.put("ids", idsArray);
				specificationParam.put("goods_id", goodsId);
				List<GoodsSpecificationVo> specificationEntities = goodsSpecificationService
						.queryList(specificationParam);
				goodsSepcifitionValue = new String[specificationEntities.size()];
				for (int i = 0; i < specificationEntities.size(); i++) {
					goodsSepcifitionValue[i] = specificationEntities.get(i).getValue();
				}
			}
			cartInfo = new CartVo();

			cartInfo.setGoods_id(goodsId);
			cartInfo.setProduct_id(productId);
			cartInfo.setGoods_sn(productInfo.getGoods_sn());
			cartInfo.setGoods_name(goodsInfo.getName());
			cartInfo.setList_pic_url(goodsInfo.getList_pic_url());
			cartInfo.setNumber(number);
			cartInfo.setSession_id("1");
			cartInfo.setUser_id(loginUser.getUserId());
			cartInfo.setRetail_price(productInfo.getRetail_price());
			cartInfo.setMarket_price(productInfo.getMarket_price());
			if (null != goodsSepcifitionValue) {
				cartInfo.setGoods_specifition_name_value(com.qiniu.util.StringUtils.join(goodsSepcifitionValue, ";"));
			}
			cartInfo.setGoods_specifition_ids(productInfo.getGoods_specification_ids());
			cartInfo.setChecked(1);
			cartService.save(cartInfo);
		} else {
			// 如果已经存在购物车中，则数量增加
			if (productInfo.getGoods_number() < (number + cartInfo.getNumber())) {
				return ResultGenerator.genFailResult(ResultCode.FAIL.code, "库存不足", "");
			}
			cartInfo.setNumber(cartInfo.getNumber() + number);
			cartService.update(cartInfo);
		}
		return ResultGenerator.genSuccessResult(getCart(loginUser));
	}

	@Override
	public Result minus(Object object) {
		UserVo loginUser = (UserVo) object;
		JSONObject jsonParam = getJsonRequest();
		Integer goodsId = jsonParam.getInteger("goodsId");
		Integer productId = jsonParam.getInteger("productId");
		Integer number = jsonParam.getInteger("number");
		// 判断购物车中是否存在此规格商品
		Map<String, Object> cartParam = new HashMap<>();
		cartParam.put("goods_id", goodsId);
		cartParam.put("product_id", productId);
		cartParam.put("user_id", loginUser.getUserId());
		List<CartVo> cartInfoList = cartService.queryList(cartParam);
		CartVo cartInfo = null != cartInfoList && cartInfoList.size() > 0 ? cartInfoList.get(0) : null;
		int cart_num = 0;
		if (null != cartInfo) {
			if (cartInfo.getNumber() > number) {
				cartInfo.setNumber(cartInfo.getNumber() - number);
				cartService.update(cartInfo);
				cart_num = cartInfo.getNumber();
			} else if (cartInfo.getNumber() == 1) {
				cartService.delete(cartInfo.getId());
				cart_num = 0;
			}
		}
		return ResultGenerator.genSuccessResult(cart_num);
	}

	@Override
	public Result update(Object object) {
		UserVo loginUser = (UserVo) object;
		JSONObject jsonParam = getJsonRequest();
		Integer goodsId = jsonParam.getInteger("goodsId");
		Integer productId = jsonParam.getInteger("productId");
		Integer number = jsonParam.getInteger("number");
		Integer id = jsonParam.getInteger("id");
		// 取得规格的信息,判断规格库存
		ProductVo productInfo = productService.queryObject(productId);
		if (null == productInfo || productInfo.getGoods_number() < number) {
			return ResultGenerator.genFailResult(ResultCode.FAIL.code, "库存不足", "");
		}
		// 判断是否已经存在product_id购物车商品
		CartVo cartInfo = cartService.queryObject(id);
		// 只是更新number
		if (cartInfo.getProduct_id().equals(productId)) {
			cartInfo.setNumber(number);
			cartService.update(cartInfo);
			return ResultGenerator.genSuccessResult(getCart(loginUser));
		}

		Map<String, Object> cartParam = new HashMap<>();
		cartParam.put("goodsId", goodsId);
		cartParam.put("productId", productId);
		List<CartVo> cartInfoList = cartService.queryList(cartParam);
		CartVo newcartInfo = null != cartInfoList && cartInfoList.size() > 0 ? cartInfoList.get(0) : null;
		if (null == newcartInfo) {
			// 添加操作
			// 添加规格名和值
			String[] goodsSepcifitionValue = null;
			if (null != productInfo.getGoods_specification_ids()) {
				Map<String, Object> specificationParam = new HashMap<>();
				specificationParam.put("ids", productInfo.getGoods_specification_ids());
				specificationParam.put("goodsId", goodsId);
				List<GoodsSpecificationVo> specificationEntities = goodsSpecificationService
						.queryList(specificationParam);
				goodsSepcifitionValue = new String[specificationEntities.size()];
				for (int i = 0; i < specificationEntities.size(); i++) {
					goodsSepcifitionValue[i] = specificationEntities.get(i).getValue();
				}
			}
			cartInfo.setProduct_id(productId);
			cartInfo.setGoods_sn(productInfo.getGoods_sn());
			cartInfo.setNumber(number);
			cartInfo.setRetail_price(productInfo.getRetail_price());
			cartInfo.setMarket_price(productInfo.getRetail_price());
			if (null != goodsSepcifitionValue) {
				cartInfo.setGoods_specifition_name_value(com.qiniu.util.StringUtils.join(goodsSepcifitionValue, ";"));
			}
			cartInfo.setGoods_specifition_ids(productInfo.getGoods_specification_ids());
			cartService.update(cartInfo);
		} else {
			// 合并购物车已有的product信息，删除已有的数据
			Integer newNumber = number + newcartInfo.getNumber();
			if (null == productInfo || productInfo.getGoods_number() < newNumber) {
				return ResultGenerator.genFailResult(ResultCode.FAIL.code, "库存不足", "");
			}
			cartService.delete(newcartInfo.getId());
			// 添加规格名和值
			String[] goodsSepcifitionValue = null;
			if (null != productInfo.getGoods_specification_ids()) {
				Map<String, Object> specificationParam = new HashMap<>();
				specificationParam.put("ids", productInfo.getGoods_specification_ids());
				specificationParam.put("goodsId", goodsId);
				List<GoodsSpecificationVo> specificationEntities = goodsSpecificationService
						.queryList(specificationParam);
				goodsSepcifitionValue = new String[specificationEntities.size()];
				for (int i = 0; i < specificationEntities.size(); i++) {
					goodsSepcifitionValue[i] = specificationEntities.get(i).getValue();
				}
			}
			cartInfo.setProduct_id(productId);
			cartInfo.setGoods_sn(productInfo.getGoods_sn());
			cartInfo.setNumber(number);
			cartInfo.setRetail_price(productInfo.getRetail_price());
			cartInfo.setMarket_price(productInfo.getRetail_price());
			if (null != goodsSepcifitionValue) {
				cartInfo.setGoods_specifition_name_value(com.qiniu.util.StringUtils.join(goodsSepcifitionValue, ";"));
			}
			cartInfo.setGoods_specifition_ids(productInfo.getGoods_specification_ids());
			cartService.update(cartInfo);
		}
		return ResultGenerator.genSuccessResult(getCart(loginUser));
	}

	@Override
	public Result checked(Object object) {
		UserVo loginUser = (UserVo) object;
		JSONObject jsonParam = getJsonRequest();
		String productIds = jsonParam.getString("productIds");
		Integer isChecked = jsonParam.getInteger("isChecked");
		if (StringUtils.isNullOrEmpty(productIds)) {
			return ResultGenerator.genFailResult(1, "删除出错", null);
		}
		String[] productIdArray = productIds.split(",");
		cartService.updateCheck(productIdArray, isChecked, loginUser.getUserId());
		return ResultGenerator.genSuccessResult(getCart(loginUser));
	}

	@Override
	public Result delete(Object object) {
		UserVo loginUser = (UserVo) object;
		Long userId = loginUser.getUserId();

		JSONObject jsonObject = getJsonRequest();
		String productIds = jsonObject.getString("productIds");

		if (StringUtils.isNullOrEmpty(productIds)) {
			return ResultGenerator.genFailResult(1, "删除出错", null);
		}
		String[] productIdsArray = productIds.split(",");
		cartService.deleteByUserAndProductIds(userId, productIdsArray);

		return ResultGenerator.genSuccessResult(getCart(loginUser));
	}

	@Override
	public Result goodscount(Object object) {
		UserVo loginUser = (UserVo) object;
		if (null == loginUser || null == loginUser.getUserId()) {
			return ResultGenerator.genFailResult(1, "未登录", null);
		}
		Map<String, Object> resultObj = new HashMap<>();
		// 查询列表数据
		Map<String, Object> param = new HashMap<>();
		param.put("user_id", loginUser.getUserId());
		List<CartVo> cartList = cartService.queryList(param);
		// 获取购物车统计信息
		Integer goodsCount = 0;
		for (CartVo cartItem : cartList) {
			goodsCount += cartItem.getNumber();
		}
		resultObj.put("cartList", cartList);
		//
		Map<String, Object> cartTotal = new HashMap<>();
		cartTotal.put("goodsCount", goodsCount);
		//
		resultObj.put("cartTotal", cartTotal);
		return ResultGenerator.genSuccessResult(resultObj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result checkout(Object object, Integer couponId) {
		UserVo loginUser = (UserVo) object;
		Map<String, Object> resultObj = new HashMap<>();
		// 根据收货地址计算运费
		BigDecimal freightPrice = new BigDecimal(10.00);
		// 默认收货地址
		Map<String, Object> param = new HashMap<>();
		param.put("user_id", loginUser.getUserId());
		List<AddressVo> addressEntities = addressService.queryList(param);

		resultObj.put("checkedAddress", addressEntities.get(0));
		// 获取要购买的商品
		Map<String, Object> cartData = (Map<String, Object>) this.getCart(loginUser).getData();

		List<CartVo> checkedGoodsList = new ArrayList<>();
		for (CartVo cartEntity : (List<CartVo>) cartData.get("cartList")) {
			if (cartEntity.getChecked() == 1) {
				checkedGoodsList.add(cartEntity);
			}
		}
		// 计算订单的费用
		// 商品总价
		BigDecimal goodsTotalPrice = (BigDecimal) ((HashMap) cartData.get("cartTotal")).get("checkedGoodsAmount");

		// 获取可用的优惠券信息
		Map usercouponMap = new HashMap();
		usercouponMap.put("user_id", loginUser.getUserId());
		List<CouponVo> couponList = apiCouponService.queryUserCouponList(usercouponMap);
		CouponVo checkedCoupon = null;
		BigDecimal couponPrice = new BigDecimal(0.00); // 使用优惠券减免的金额
		if (null != couponList && couponList.size() > 0) {
			for (CouponVo couponVo : couponList) {
				if (null != couponId && couponId.equals(couponVo.getId())) {
					couponPrice = couponVo.getType_money();
					checkedCoupon = couponVo;
				}
			}
		}
		// 获取优惠信息提示
		Map couponParam = new HashMap();
		couponParam.put("enabled", true);
		Integer[] send_types = new Integer[] { 0, 7 };
		couponParam.put("send_types", send_types);
		List<CouponVo> couponVos = apiCouponService.queryList(couponParam);
		BigDecimal fullCutCouponDec = new BigDecimal(0);
		if (null != couponVos && couponVos.size() > 0) {
			for (CouponVo couponVo : couponVos) {
				if (couponVo.getSend_type() == 0 && couponVo.getMin_amount().compareTo(goodsTotalPrice) <= 0
						&& fullCutCouponDec.compareTo(couponVo.getType_money()) < 0) {
					fullCutCouponDec = couponVo.getType_money();
				}
				// 是否免运费
				if (couponVo.getSend_type() == 7 && couponVo.getMin_amount().compareTo(goodsTotalPrice) <= 0) {
					freightPrice = new BigDecimal(0);
				}
			}
		}
		resultObj.put("fullCutCouponDec", fullCutCouponDec);
		// 订单的总价
		BigDecimal orderTotalPrice = goodsTotalPrice.add(freightPrice);

		//
		BigDecimal actualPrice = orderTotalPrice.subtract(fullCutCouponDec).subtract(couponPrice); // 减去其它支付的金额后，要实际支付的金额

		resultObj.put("freightPrice", freightPrice);
		resultObj.put("checkedCoupon", checkedCoupon);
		resultObj.put("couponList", couponList);

		resultObj.put("couponPrice", couponPrice);
		resultObj.put("checkedGoodsList", checkedGoodsList);
		resultObj.put("goodsTotalPrice", goodsTotalPrice);
		resultObj.put("orderTotalPrice", orderTotalPrice);
		resultObj.put("actualPrice", actualPrice);
		return ResultGenerator.genSuccessResult(resultObj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object checkedCouponList(Object object) {
		UserVo loginUser = (UserVo) object;
		//
		Map<String, Object> param = new HashMap<>();
		param.put("user_id", loginUser.getUserId());
		List<CouponVo> couponVos = apiCouponService.queryUserCouponList(param);
		if (null != couponVos && couponVos.size() > 0) {
			// 获取要购买的商品
			Map<String, Object> cartData = (Map<String, Object>) this.getCart(loginUser).getData();
			List<CartVo> checkedGoodsList = new ArrayList<>();
			List<Integer> checkedGoodsIds = new ArrayList<>();
			for (CartVo cartEntity : (List<CartVo>) cartData.get("cartList")) {
				if (cartEntity.getChecked() == 1) {
					checkedGoodsList.add(cartEntity);
					checkedGoodsIds.add(cartEntity.getId());
				}
			}
			// 计算订单的费用
			BigDecimal goodsTotalPrice = (BigDecimal) ((HashMap) cartData.get("cartTotal")).get("checkedGoodsAmount"); // 商品总价
			// 如果没有用户优惠券直接返回新用户优惠券
			for (CouponVo couponVo : couponVos) {
				if (couponVo.getMin_amount().compareTo(goodsTotalPrice) <= 0) {
					couponVo.setEnabled(1);
				}
			}
		}
		return toResponsSuccess(couponVos);
	}

	private String[] getSpecificationIdsArray(String ids) {
		String[] idsArray = null;
		if (org.apache.commons.lang.StringUtils.isNotEmpty(ids)) {
			String[] tempArray = ids.split("_");
			if (null != tempArray && tempArray.length > 0) {
				idsArray = tempArray;
			}
		}
		return idsArray;
	}
}
