package com.github.wechat.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.AdVo;
import com.github.model.entity.wechat.BrandVo;
import com.github.model.entity.wechat.CartVo;
import com.github.model.entity.wechat.ChannelVo;
import com.github.model.entity.wechat.GoodsVo;
import com.github.model.entity.wechat.TopicVo;
import com.github.wechat.api.ApiIndexCloud;
import com.github.wechat.cloud.annotation.IgnoreAuth;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.cons.IndexConstant;
import com.github.wechat.cloud.service.ApiAdService;
import com.github.wechat.cloud.service.ApiBrandService;
import com.github.wechat.cloud.service.ApiCartService;
import com.github.wechat.cloud.service.ApiChannelService;
import com.github.wechat.cloud.service.ApiGoodsService;
import com.github.wechat.cloud.service.ApiTopicService;
import com.github.wechat.cloud.utils.RedisUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiIndexController extends ApiBaseAction implements ApiIndexCloud{
    @Autowired
    private ApiAdService adService;
    @Autowired
    private ApiChannelService channelService;
    @Autowired
    private ApiGoodsService goodsService;
    @Autowired
    private ApiCartService cartService;
    @Autowired
    private ApiBrandService brandService;
    @Autowired
    private ApiTopicService topicService;

    @IgnoreAuth
    @Override
    public Result index() {
        Map<String, Object> resultObj = new HashMap<>();
        
        Map<String, Object> param = new HashMap<>();
        
        //首页轮播图
        List<AdVo> banner = null;
        if (RedisUtil.hasKey(IndexConstant.INDEX_BANNER_ALL)) {
			banner = RedisUtil.get(IndexConstant.INDEX_BANNER_ALL);
		}else {
			param.put("ad_position_id", 1);
	        banner = adService.queryList(param);
	        //放入缓存
	        if (banner != null && banner.size() > 0) {
				RedisUtil.set(IndexConstant.INDEX_BANNER_ALL, banner);
			}
		}
        resultObj.put("banner", banner);
        
        //首页分类按钮
        List<ChannelVo> channel = null;
        if (RedisUtil.hasKey(IndexConstant.INDEX_CATEGORIES_SMALL)) {
        	channel = RedisUtil.get(IndexConstant.INDEX_CATEGORIES_SMALL);
		}else {
			param = new HashMap<>();
	        param.put("sidx", "sort_order ");
	        param.put("order", "asc ");
	        channel = channelService.queryList(param);
	        //放入缓存
	        if (channel != null && channel.size() > 0) {
				RedisUtil.set(IndexConstant.INDEX_CATEGORIES_SMALL, channel);
			}
		}
        resultObj.put("channel", channel);
        
        //品牌精选
        List<BrandVo> brandList = null;
        if (RedisUtil.hasKey(IndexConstant.INDEX_BRAND_ALL)) {
        	brandList = RedisUtil.get(IndexConstant.INDEX_BRAND_ALL);
		}else {
			param = new HashMap<>();
	      param.put("is_new", 1);
	      param.put("sidx", "new_sort_order ");
	      param.put("order", "asc ");
	      param.put("offset", 0);
	      param.put("limit", 4);
	      brandList = brandService.queryList(param);
	      
	        //放入缓存
	        if (brandList != null && brandList.size() > 0) {
				RedisUtil.set(IndexConstant.INDEX_BRAND_ALL, brandList, 60 * 60 * 24);
			}
		}
        resultObj.put("brandList", brandList);
        
        //新品首发
        List<GoodsVo> newGoods = null;
        if (RedisUtil.hasKey(IndexConstant.INDEX_NEWGOODS_ALL)) {
        	newGoods = RedisUtil.get(IndexConstant.INDEX_NEWGOODS_ALL);
		}else {
			 param = new HashMap<>();
		        param.put("is_new", 1);
		        param.put("offset", 0);
		        param.put("limit", 4);
		        param.put("is_delete", 0);
		        param.put("fields", "id, name, list_pic_url, retail_price");
		        newGoods = goodsService.queryList(param);
	      
	        //放入缓存
	        if (newGoods != null && newGoods.size() > 0) {
				RedisUtil.set(IndexConstant.INDEX_NEWGOODS_ALL, newGoods, 60 * 60 * 24);
			}
		}
        resultObj.put("newGoodsList", newGoods);
        
        //人气推选
        List<GoodsVo> hotGoods = null;
        if (RedisUtil.hasKey(IndexConstant.INDEX_HOTGOODS_ALL)) {
        	hotGoods = RedisUtil.get(IndexConstant.INDEX_HOTGOODS_ALL);
		}else {
			 param = new HashMap<>();
			 param.put("is_hot", "1");
		      param.put("offset", 0);
		      param.put("limit", 3);
		      param.put("is_delete", 0);
		      hotGoods = goodsService.queryHotGoodsList(param);
	      
	        //放入缓存
	        if (hotGoods != null && hotGoods.size() > 0) {
				RedisUtil.set(IndexConstant.INDEX_HOTGOODS_ALL, hotGoods, 60 * 60 * 24);
			}
		}
        resultObj.put("hotGoodsList", hotGoods);

        //专题精选
        List<TopicVo> topicList = null;
        if (RedisUtil.hasKey(IndexConstant.INDEX_TOPIC_ALL)) {
        	topicList = RedisUtil.get(IndexConstant.INDEX_TOPIC_ALL);
		}else {
			 param = new HashMap<>();
			  param.put("offset", 0);
		      param.put("limit", 3);
		      topicList = topicService.queryList(param);
	      
	        //放入缓存
	        if (topicList != null && topicList.size() > 0) {
				RedisUtil.set(IndexConstant.INDEX_TOPIC_ALL, topicList, 60 * 60 * 24);
			}
		}
        resultObj.put("topicList", topicList);
        
//      param = new HashMap();
//      param.put("parent_id", 0);
//      param.put("notName", "推荐");//<>
//      List<CategoryVo> categoryList = categoryService.queryList(param);
//      List<Map> newCategoryList = new ArrayList<>();
//
//      for (CategoryVo categoryItem : categoryList) {
//          param.remove("fields");
//          param.put("parent_id", categoryItem.getId());
//          List<CategoryVo> categoryEntityList = categoryService.queryList(param);
//          List<Integer> childCategoryIds = new ArrayList<>();
//          for (CategoryVo categoryEntity : categoryEntityList) {
//              childCategoryIds.add(categoryEntity.getId());
//          }
//          //
//          param = new HashMap();
//          param.put("categoryIds", childCategoryIds);
//          param.put("fields", "id as id, name as name, list_pic_url as list_pic_url, retail_price as retail_price");
//          List<GoodsVo> categoryGoods = goodsService.queryList(param);
//          Map newCategory = new HashMap();
//          newCategory.put("id", categoryItem.getId());
//          newCategory.put("name", categoryItem.getName());
//          newCategory.put("goodsList", categoryGoods);
//          newCategoryList.add(newCategory);
//      }
//      resultObj.put("categoryList", newCategoryList);
      
        
        // 当前购物车中
        List<CartVo> cartList = new ArrayList<>();
        if (null != getUserId()) {
            //查询列表数据
            Map<String, Object> cartParam = new HashMap<>();
            cartParam.put("user_id", getUserId());
//            cartParam.put("user_id", 15);
            cartList = cartService.queryList(cartParam);
        }
        if (null != cartList && cartList.size() > 0 && null != hotGoods && hotGoods.size() > 0) {
            for (GoodsVo goodsVo : hotGoods) {
                for (CartVo cartVo : cartList) {
                    if (goodsVo.getId().equals(cartVo.getGoods_id())) {
                        goodsVo.setCart_num(cartVo.getNumber());
                    }
                }
            }
        }

        return ResultGenerator.genSuccessResult(resultObj);
    }

    
    
}