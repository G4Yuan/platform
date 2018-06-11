package com.github.wechat.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.AddressVo;
import com.github.model.entity.wechat.UserVo;
import com.github.wechat.api.ApiAddressCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.service.ApiAddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiAddressController extends ApiBaseAction implements ApiAddressCloud {

	@Autowired
	private ApiAddressService addressService;

	@Override
	public Result list(Object object) {
		UserVo loginUser = (UserVo) object;
		Map<String, Object> param = new HashMap<>();
		param.put("user_id", loginUser.getUserId());
		List<AddressVo> addressEntities = addressService.queryList(param);
		return ResultGenerator.genSuccessResult(addressEntities);
	}

	@Override
	public Result detail(Integer id) {
		AddressVo entity = addressService.queryObject(id);
		return ResultGenerator.genSuccessResult(entity);
	}

	@Override
	public Result save(Object object) {
		UserVo loginUser = (UserVo) object;
		JSONObject addressJson = this.getJsonRequest();
		AddressVo entity = new AddressVo();
		if (null != addressJson) {
			entity.setId(addressJson.getLong("id"));
			entity.setUserId(loginUser.getUserId());
			entity.setUserName(addressJson.getString("userName"));
			entity.setPostalCode(addressJson.getString("postalCode"));
			entity.setProvinceName(addressJson.getString("provinceName"));
			entity.setCityName(addressJson.getString("cityName"));
			entity.setCountyName(addressJson.getString("countyName"));
			entity.setDetailInfo(addressJson.getString("detailInfo"));
			entity.setNationalCode(addressJson.getString("nationalCode"));
			entity.setTelNumber(addressJson.getString("telNumber"));
			entity.setIs_default(addressJson.getInteger("is_default"));
		}
		if (null == entity.getId() || entity.getId() == 0) {
			entity.setId(null);
			addressService.save(entity);
		} else {
			addressService.update(entity);
		}
		return ResultGenerator.genSuccessResult(entity);
	}

	@Override
	public Result delete() {
		JSONObject jsonParam = this.getJsonRequest();
		addressService.delete(jsonParam.getIntValue("id"));
		return ResultGenerator.genSuccessResult("");
	}

}
