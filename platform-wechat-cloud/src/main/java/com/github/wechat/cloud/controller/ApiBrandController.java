package com.github.wechat.cloud.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.BrandVo;
import com.github.model.page.Query;
import com.github.wechat.api.ApiBrandCloud;
import com.github.wechat.cloud.service.ApiBrandService;
import com.github.wechat.cloud.utils.ApiPageUtils;

@RestController
public class ApiBrandController implements ApiBrandCloud {

	@Autowired
	private ApiBrandService brandService;

	@Override
	public Result index() {
		Map<String, Object> param = new HashMap<>();
		List<BrandVo> entityList = brandService.queryList(param);
		return ResultGenerator.genSuccessResult(entityList);
	}

	@Override
	public Result list(Integer page, Integer size) {
		// 查询列表数据
		Map<String, Object> params = new HashMap<>();
		params.put("fields", "id, name, floor_price, app_list_pic_url");
		params.put("page", page);
		params.put("limit", size);
		params.put("sidx", "id");
		params.put("order", "asc");

		Query query = new Query(params);
		List<BrandVo> brandEntityList = brandService.queryList(query);
		int total = brandService.queryTotal(query);
		ApiPageUtils pageUtil = new ApiPageUtils(brandEntityList, total, query.getLimit(), query.getPage());
		//
		return ResultGenerator.genSuccessResult(pageUtil);
	}

	@Override
	public Result detail(Integer id) {
		Map<String, Object> resultObj = new HashMap<>();
		// 查询列表数据
		BrandVo entity = brandService.queryObject(id);
		//
		resultObj.put("brand", entity);
		return ResultGenerator.genSuccessResult(resultObj);
	}
}
