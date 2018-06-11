package com.github.wechat.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.CategoryVo;
import com.github.wechat.api.ApiCatalogCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.cons.CateGoryConstant;
import com.github.wechat.cloud.service.ApiCategoryService;
import com.github.wechat.cloud.utils.RedisUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiCatalogController extends ApiBaseAction implements ApiCatalogCloud {
	
    @Autowired
    private ApiCategoryService categoryService;

    @Override
    public Result index(Integer id, Integer page, Integer size) {
    	
    	boolean isexist = RedisUtil.hasKey(CateGoryConstant.CATEGORY_ALL);
    	
    	Map<String, Object> resultObj = new HashMap<>();
    	Map<String, Object> params = new HashMap<>();
		params.put("page", page);
		params.put("limit", size);
		params.put("sidx", "sort_order");
		params.put("order", "asc");
		params.put("parent_id", 0);
		
    	//总分类
    	List<CategoryVo> data = null;
    	//缓存是否存在
    	if (isexist) {
			data = RedisUtil.get(CateGoryConstant.CATEGORY_ALL);
		}else {
			//查询列表数据
			data = categoryService.queryList(params);
			RedisUtil.set(CateGoryConstant.CATEGORY_ALL, data);
		}
    	
        CategoryVo currentCategory = null;
        if (null != id) {
        	if (RedisUtil.hasKey(CateGoryConstant.CATEGORY_PREFIX + id)) {
				currentCategory = RedisUtil.get(CateGoryConstant.CATEGORY_PREFIX + id);
			}else {
				currentCategory = categoryService.queryObject(id);
				
				params.put("parent_id", currentCategory.getId());
	            currentCategory.setSubCategoryList(categoryService.queryList(params));
	            
	            //放入缓存
	            RedisUtil.set(CateGoryConstant.CATEGORY_PREFIX + id, currentCategory);
			}
        }else {
			if (data != null && data.size() > 0) {
				id = data.get(0).getId();
				
				if (RedisUtil.hasKey(CateGoryConstant.CATEGORY_PREFIX + id)) {
					currentCategory = RedisUtil.get(CateGoryConstant.CATEGORY_PREFIX + id);
				}else {
					currentCategory = categoryService.queryObject(id);
					
					params.put("parent_id", currentCategory.getId());
		            currentCategory.setSubCategoryList(categoryService.queryList(params));
		            
		            //放入缓存
		            RedisUtil.set(CateGoryConstant.CATEGORY_PREFIX + id, currentCategory);
				}
			}else {
				currentCategory = new CategoryVo();
			}
		}
        
        resultObj.put("categoryList", data);
        resultObj.put("currentCategory", currentCategory);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result current(Integer id) {
    	
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
    	CategoryVo currentCategory = null;
    	if (id != null) {
    		//取缓存数据
    		if (RedisUtil.hasKey(CateGoryConstant.CATEGORY_PREFIX + id)) {
				currentCategory = RedisUtil.get(CateGoryConstant.CATEGORY_PREFIX + id);
			}else {
				currentCategory = categoryService.queryObject(id);
				if (null != currentCategory && null != currentCategory.getId()) {
					params.put("parent_id", currentCategory.getId());
					currentCategory.setSubCategoryList(categoryService.queryList(params));
					
					//放入缓存
					RedisUtil.set(CateGoryConstant.CATEGORY_PREFIX + id, currentCategory);
				}
			}
		}
       
        resultObj.put("currentCategory", currentCategory);
        return ResultGenerator.genSuccessResult(resultObj);
    }
}