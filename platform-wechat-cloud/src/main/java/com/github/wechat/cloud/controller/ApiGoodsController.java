package com.github.wechat.cloud.controller;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.model.entity.wechat.AttributeVo;
import com.github.model.entity.wechat.BrandVo;
import com.github.model.entity.wechat.CartVo;
import com.github.model.entity.wechat.CategoryVo;
import com.github.model.entity.wechat.CommentPictureVo;
import com.github.model.entity.wechat.CommentVo;
import com.github.model.entity.wechat.CouponVo;
import com.github.model.entity.wechat.FootprintVo;
import com.github.model.entity.wechat.GoodsGalleryVo;
import com.github.model.entity.wechat.GoodsIssueVo;
import com.github.model.entity.wechat.GoodsSpecificationVo;
import com.github.model.entity.wechat.GoodsVo;
import com.github.model.entity.wechat.ProductVo;
import com.github.model.entity.wechat.RelatedGoodsVo;
import com.github.model.entity.wechat.SearchHistoryVo;
import com.github.model.entity.wechat.UserCouponVo;
import com.github.model.entity.wechat.UserVo;
import com.github.model.page.Query;
import com.github.wechat.api.ApiGoodsCloud;
import com.github.wechat.cloud.annotation.IgnoreAuth;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.cons.CateGoryConstant;
import com.github.wechat.cloud.service.ApiAttributeService;
import com.github.wechat.cloud.service.ApiBrandService;
import com.github.wechat.cloud.service.ApiCartService;
import com.github.wechat.cloud.service.ApiCategoryService;
import com.github.wechat.cloud.service.ApiCollectService;
import com.github.wechat.cloud.service.ApiCommentPictureService;
import com.github.wechat.cloud.service.ApiCommentService;
import com.github.wechat.cloud.service.ApiCouponService;
import com.github.wechat.cloud.service.ApiFootprintService;
import com.github.wechat.cloud.service.ApiGoodsGalleryService;
import com.github.wechat.cloud.service.ApiGoodsIssueService;
import com.github.wechat.cloud.service.ApiGoodsService;
import com.github.wechat.cloud.service.ApiGoodsSpecificationService;
import com.github.wechat.cloud.service.ApiProductService;
import com.github.wechat.cloud.service.ApiRelatedGoodsService;
import com.github.wechat.cloud.service.ApiSearchHistoryService;
import com.github.wechat.cloud.service.ApiUserCouponService;
import com.github.wechat.cloud.service.ApiUserService;
import com.github.wechat.cloud.utils.ApiPageUtils;
import com.github.wechat.cloud.utils.RedisUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import com.github.common.utils.Base64;
import com.github.common.utils.CharUtil;
import com.github.common.utils.DateUtils;

@RestController
public class ApiGoodsController extends ApiBaseAction implements ApiGoodsCloud{
    @Autowired
    private ApiGoodsService goodsService;
    @Autowired
    private ApiGoodsSpecificationService goodsSpecificationService;
    @Autowired
    private ApiProductService productService;
    @Autowired
    private ApiGoodsGalleryService goodsGalleryService;
    @Autowired
    private ApiGoodsIssueService goodsIssueService;
    @Autowired
    private ApiAttributeService attributeService;
    @Autowired
    private ApiBrandService brandService;
    @Autowired
    private ApiCommentService commentService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private ApiCommentPictureService commentPictureService;
    @Autowired
    private ApiCollectService collectService;
    @Autowired
    private ApiFootprintService footprintService;
    @Autowired
    private ApiCategoryService categoryService;
    @Autowired
    private ApiSearchHistoryService searchHistoryService;
    @Autowired
    private ApiRelatedGoodsService relatedGoodsService;
    @Autowired
    private ApiCouponService apiCouponService;
    @Autowired
    private ApiUserCouponService apiUserCouponService;
    @Autowired
    private ApiCartService cartService;

    
    @Override
    public Result index(Object object) {
    	//
        Map<String, Object> param = new HashMap<>();
        param.put("is_delete", 0);
        param.put("is_on_sale", 1);
        List<GoodsVo> goodsList = goodsService.queryList(param);
        //
        return ResultGenerator.genSuccessResult(goodsList);
    }

    @Override
    public Result sku(Object object, Integer id) {
        Map<String, Object> resultObj = new HashMap<>();
        //
        Map<String, Object> param = new HashMap<>();
        param.put("goods_id", id);
        List<GoodsSpecificationVo> goodsSpecificationEntityList = goodsSpecificationService.queryList(param);
        //
        List<ProductVo> productEntityList = productService.queryList(param);
        //
        resultObj.put("specificationList", goodsSpecificationEntityList);
        resultObj.put("productList", productEntityList);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Result detail(Integer id, Long referrer) {
        Map<String, Object> resultObj = new HashMap<>();
        //
        Long userId = getUserId();
        GoodsVo info = goodsService.queryObject(id);
        Map<String, Object> param = new HashMap<>();
        param.put("goods_id", id);
        //
        Map<String, Object> specificationParam = new HashMap<>();
        specificationParam.put("fields", "gs.*, s.name");
        specificationParam.put("goods_id", id);
        specificationParam.put("specification", true);
        specificationParam.put("sidx", "s.sort_order");
        specificationParam.put("order", "asc");
        List<GoodsSpecificationVo> goodsSpecificationEntityList = goodsSpecificationService.queryList(specificationParam);

        List<Map<String, Object>> specificationList = new ArrayList<>();
        //按规格名称分组
        for (int i = 0; i < goodsSpecificationEntityList.size(); i++) {
            GoodsSpecificationVo specItem = goodsSpecificationEntityList.get(i);
            //
            List<GoodsSpecificationVo> tempList = null;
            for (int j = 0; j < specificationList.size(); j++) {
                if (specificationList.get(j).get("specification_id").equals(specItem.getSpecification_id())) {
                    tempList = (List<GoodsSpecificationVo>) specificationList.get(j).get("valueList");
                    break;
                }
            }
            //
            if (null == tempList) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("specification_id", specItem.getSpecification_id());
                temp.put("name", specItem.getName());
                tempList = new ArrayList<>();
                tempList.add(specItem);
                temp.put("valueList", tempList);
                specificationList.add(temp);
            } else {
                for (int j = 0; j < specificationList.size(); j++) {
                    if (specificationList.get(j).get("specification_id").equals(specItem.getSpecification_id())) {
                        tempList = (List<GoodsSpecificationVo>) specificationList.get(j).get("valueList");
                        tempList.add(specItem);
                        break;
                    }
                }
            }
        }
        //
        List<ProductVo> productEntityList = productService.queryList(param);
        //
        List<GoodsGalleryVo> gallery = goodsGalleryService.queryList(param);
        Map<String, Object> ngaParam = new HashMap<>();
        ngaParam.put("fields", "nga.value, na.name");
        ngaParam.put("sidx", "nga.id");
        ngaParam.put("order", "asc");
        ngaParam.put("goods_id", id);
        List<AttributeVo> attribute = attributeService.queryList(ngaParam);
        //
        Map<String, Object> issueParam = new HashMap<>();
//        issueParam.put("goods_id", id);
        List<GoodsIssueVo> issue = goodsIssueService.queryList(issueParam);
        //
        BrandVo brand = brandService.queryObject(info.getBrand_id());
        //
        param.put("value_id", id);
        param.put("type_id", 0);
        Integer commentCount = commentService.queryTotal(param);
        List<CommentVo> hotComment = commentService.queryList(param);
        Map<String, Object> commentInfo = new HashMap<>();
        if (null != hotComment && hotComment.size() > 0) {
            UserVo commentUser = userService.queryObject(hotComment.get(0).getUser_id());
            commentInfo.put("content", Base64.decode(hotComment.get(0).getContent()));
            commentInfo.put("add_time", DateUtils.timeToStr(hotComment.get(0).getAdd_time(), DateUtils.DATE_PATTERN));
            commentInfo.put("nickname", commentUser.getNickname());
            commentInfo.put("avatar", commentUser.getAvatar());
            Map<String, Object> paramPicture = new HashMap<>();
            paramPicture.put("comment_id", hotComment.get(0).getId());
            List<CommentPictureVo> commentPictureEntities = commentPictureService.queryList(paramPicture);
            commentInfo.put("pic_list", commentPictureEntities);
        }
        Map<String, Object> comment = new HashMap<>();
        comment.put("count", commentCount);
        comment.put("data", commentInfo);
        //当前用户是否收藏
        Map<String, Object> collectParam = new HashMap<>();
        collectParam.put("user_id", getUserId());
        collectParam.put("value_id", id);
        collectParam.put("type_id", 0);
        Integer userHasCollect = collectService.queryTotal(collectParam);
        if (userHasCollect > 0) {
            userHasCollect = 1;
        }
        //记录用户的足迹
        FootprintVo footprintEntity = new FootprintVo();
        footprintEntity.setAdd_time(System.currentTimeMillis() / 1000);
        footprintEntity.setGoods_brief(info.getGoods_brief());
        footprintEntity.setList_pic_url(info.getList_pic_url());
        footprintEntity.setGoods_id(info.getId());
        footprintEntity.setName(info.getName());
        footprintEntity.setRetail_price(info.getRetail_price());
        footprintEntity.setUser_id(userId);
        if (null != referrer) {
            footprintEntity.setReferrer(referrer);
        } else {
            footprintEntity.setReferrer(0L);
        }
        footprintService.save(footprintEntity);
        //
        resultObj.put("info", info);
        resultObj.put("gallery", gallery);
        resultObj.put("attribute", attribute);
        resultObj.put("userHasCollect", userHasCollect);
        resultObj.put("issue", issue);
        resultObj.put("comment", comment);
        resultObj.put("brand", brand);
        resultObj.put("specificationList", specificationList);
        resultObj.put("productList", productEntityList);
        // 记录推荐人是否可以领取红包，用户登录时校验
        try {
            // 是否已经有可用的转发红包
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("send_type", 2);
            params.put("unUsed", true);
            List<CouponVo> enabledCouponVos = apiCouponService.queryUserCoupons(params);
            if ((null == enabledCouponVos || enabledCouponVos.size() == 0)
                    && null != referrer && null != userId) {
                // 获取优惠信息提示
                Map<String, Object> couponParam = new HashMap<>();
                couponParam.put("enabled", true);
                Integer[] send_types = new Integer[]{2};
                couponParam.put("send_types", send_types);
                List<CouponVo> couponVos = apiCouponService.queryList(couponParam);
                if (null != couponVos && couponVos.size() > 0) {
                    CouponVo couponVo = couponVos.get(0);
                    Map<String, Object> footprintParam = new HashMap<>();
                    footprintParam.put("goods_id", id);
                    footprintParam.put("referrer", referrer);
                    Integer footprintNum = footprintService.queryTotal(footprintParam);
                    if (null != footprintNum && null != couponVo.getMin_transmit_num()
                            && footprintNum > couponVo.getMin_transmit_num()) {
                        UserCouponVo userCouponVo = new UserCouponVo();
                        userCouponVo.setAdd_time(new Date());
                        userCouponVo.setCoupon_id(couponVo.getId());
                        userCouponVo.setCoupon_number(CharUtil.getRandomString(12));
                        userCouponVo.setUser_id(getUserId());
                        apiUserCouponService.save(userCouponVo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultGenerator.genSuccessResult(resultObj);
    }

    @IgnoreAuth
    @Override
    public Result category(Integer id) {
        Map<String, Object> resultObj = new HashMap<>();
        //缓存是否存在
        CategoryVo currentCategory = null;
        if (RedisUtil.hasKey(CateGoryConstant.CATEGORY_PREFIX + id)) {
			currentCategory = RedisUtil.get(CateGoryConstant.CATEGORY_PREFIX + id);
		}else {
			currentCategory = categoryService.queryObject(id);
			
			//放入缓存
            RedisUtil.set(CateGoryConstant.CATEGORY_PREFIX + id, currentCategory);
		}
        
        //父分类
        Map<String, Object> params = new HashMap<>();
        params.put("parent_id", currentCategory.getParent_id());
        List<CategoryVo> brotherCategory = categoryService.queryList(params);
        resultObj.put("currentCategory", currentCategory);
        resultObj.put("brotherCategory", brotherCategory);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @IgnoreAuth
    @Override
	public Result list(Object object, Integer categoryId, Integer brandId, String keyword, Integer isNew,
			Integer isHot, Integer page, Integer size, String sort, String order) {
    	
    	if (page == null) {
			page = 1;
		}
    	if (size == null) {
    		size = 10;
		}
    	UserVo loginUser = new UserVo();
    	if (object instanceof UserVo) {
			System.out.println("可以");
			loginUser = (UserVo) object;
		}
    	
        Map<String, Object> params = new HashMap<>();
        params.put("is_delete", 0);
        params.put("is_on_sale", 1);
        params.put("brand_id", brandId);
        params.put("keyword", keyword);
        params.put("is_new", isNew);
        params.put("is_hot", isHot);
        params.put("page", page);
        params.put("limit", size);
        params.put("order", sort);
        params.put("sidx", order);
        //
        if (null != sort && sort.equals("price")) {
            params.put("sidx", "retail_price");
            params.put("order", order);
        } else {
            params.put("sidx", "id");
            params.put("order", "desc");
        }
        //添加到搜索历史
        if (!StringUtils.isNullOrEmpty(keyword)) {
            SearchHistoryVo searchHistoryVo = new SearchHistoryVo();
            searchHistoryVo.setAdd_time(System.currentTimeMillis() / 1000);
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUser_id(null != loginUser ? loginUser.getUserId().toString() : "");
            searchHistoryVo.setFrom("");
            searchHistoryService.save(searchHistoryVo);

        }
        //筛选的分类
        List<CategoryVo> filterCategory = new ArrayList<>();
        CategoryVo rootCategory = new CategoryVo();
        rootCategory.setId(0);
        rootCategory.setName("全部");
        rootCategory.setChecked(false);
        filterCategory.add(rootCategory);
        //
        params.put("fields", "category_id");
        List<GoodsVo> categoryEntityList = goodsService.queryList(params);
        params.remove("fields");
        if (null != categoryEntityList && categoryEntityList.size() > 0) {
            List<Integer> categoryIds = new ArrayList<>();
            for (GoodsVo goodsVo : categoryEntityList) {
                categoryIds.add(goodsVo.getCategory_id());
            }
            //查找二级分类的parent_id
            Map<String, Object> categoryParam = new HashMap<>();
            categoryParam.put("ids", categoryIds);
            categoryParam.put("fields", "parent_id");
            List<CategoryVo> parentCategoryList = categoryService.queryList(categoryParam);
            //
            List<Integer> parentIds = new ArrayList<>();
            for (CategoryVo categoryEntity : parentCategoryList) {
                parentIds.add(categoryEntity.getParent_id());
            }
            //一级分类
            categoryParam = new HashMap<>();
            categoryParam.put("fields", "id,name");
            categoryParam.put("order", "asc");
            categoryParam.put("sidx", "sort_order");
            categoryParam.put("ids", parentIds);
            List<CategoryVo> parentCategory = categoryService.queryList(categoryParam);
            if (null != parentCategory) {
                filterCategory.addAll(parentCategory);
            }
        }
        //加入分类条件
        if (null != categoryId && categoryId > 0) {
            List<Integer> categoryIds = new ArrayList<>();
            Map<String, Object> categoryParam = new HashMap<>();
            categoryParam.put("parent_id", categoryId);
            categoryParam.put("fields", "id");
            List<CategoryVo> childIds = categoryService.queryList(categoryParam);
            for (CategoryVo categoryEntity : childIds) {
                categoryIds.add(categoryEntity.getId());
            }
            categoryIds.add(categoryId);
            params.put("categoryIds", categoryIds);
        }
        //查询列表数据
        params.put("fields", "id, name, list_pic_url, market_price, retail_price, goods_brief");
        Query query = new Query(params);
        List<GoodsVo> goodsList = goodsService.queryList(query);
        int total = goodsService.queryTotal(query);
        ApiPageUtils goodsData = new ApiPageUtils(goodsList, total, query.getLimit(), query.getPage());
        //搜索到的商品
        for (CategoryVo categoryEntity : filterCategory) {
            if (null != categoryId && categoryEntity.getId() == 0 || categoryEntity.getId() == categoryId) {
                categoryEntity.setChecked(true);
            } else {
                categoryEntity.setChecked(false);
            }
        }
        goodsData.setFilterCategory(filterCategory);
        goodsData.setGoodsList(goodsData.getData());
        return ResultGenerator.genSuccessResult(goodsData);
    }

    @Override
    public Result filter(Object object, Integer categoryId,
                         String keyword, Integer isNew, Integer isHot) {
    	
        Map<String, Object> params = new HashMap<>();
        params.put("is_delete", 0);
        params.put("is_on_sale", 1);
        params.put("categoryId", categoryId);
        params.put("keyword", keyword);
        params.put("isNew", isNew);
        params.put("isHot", isHot);
        if (null != categoryId) {
            Map<String, Object> categoryParams = new HashMap<>();
            categoryParams.put("categoryId", categoryId);
            List<CategoryVo> categoryEntityList = categoryService.queryList(categoryParams);
            List<Integer> category_ids = new ArrayList<>();
            for (CategoryVo categoryEntity : categoryEntityList) {
                category_ids.add(categoryEntity.getId());
            }
            params.put("category_id", category_ids);
        }
        //筛选的分类
        List<CategoryVo> filterCategory = new ArrayList<>();
        CategoryVo rootCategory = new CategoryVo();
        rootCategory.setId(0);
        rootCategory.setName("全部");
        // 二级分类id
        List<GoodsVo> goodsEntityList = goodsService.queryList(params);
        if (null != goodsEntityList && goodsEntityList.size() > 0) {
            List<Integer> categoryIds = new ArrayList<>();
            for (GoodsVo goodsEntity : goodsEntityList) {
                categoryIds.add(goodsEntity.getCategory_id());
            }
            //查找二级分类的parent_id
            Map<String, Object> categoryParam = new HashMap<>();
            categoryParam.put("categoryIds", categoryIds);
            List<CategoryVo> parentCategoryList = categoryService.queryList(categoryParam);
            //
            List<Integer> parentIds = new ArrayList<>();
            for (CategoryVo categoryEntity : parentCategoryList) {
                parentIds.add(categoryEntity.getId());
            }
            //一级分类
            categoryParam.put("categoryIds", parentIds);
            List<CategoryVo> parentCategory = categoryService.queryList(categoryParam);
            if (null != parentCategory) {
                filterCategory.addAll(parentCategory);
            }
        }
        return ResultGenerator.genSuccessResult(filterCategory);
    }

    @Override
    public Result newAction(Object object) {
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> bannerInfo = new HashMap<>();
        bannerInfo.put("url", "");
        bannerInfo.put("name", "坚持初心，为你寻觅世间好物");
        bannerInfo.put("img_url", "http://p9kyr79ne.bkt.clouddn.com/1/20180531/1504208321fef4.png");
        resultObj.put("bannerInfo", bannerInfo);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result hot(Object object) {
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> bannerInfo = new HashMap<>();
        bannerInfo.put("url", "");
        bannerInfo.put("name", "大家都在买的严选好物");
        bannerInfo.put("img_url", "http://p9kyr79ne.bkt.clouddn.com/1/20180531/1504208321fef4.png");
        resultObj.put("bannerInfo", bannerInfo);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result related(Object object, Integer id) {
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("goods_id", id);
        param.put("fields", "related_goods_id");
        List<RelatedGoodsVo> relatedGoodsEntityList = relatedGoodsService.queryList(param);

        List<Integer> relatedGoodsIds = new ArrayList<>();
        for (RelatedGoodsVo relatedGoodsEntity : relatedGoodsEntityList) {
            relatedGoodsIds.add(relatedGoodsEntity.getRelated_goods_id());
        }

        List<GoodsVo> relatedGoods = new ArrayList<GoodsVo>();

        if (null == relatedGoodsIds || relatedGoods.size() < 1) {
            //查找同分类下的商品
            GoodsVo goodsCategory = goodsService.queryObject(id);
            Map<String, Object> paramRelated = new HashMap<>();
            paramRelated.put("fields", "id, name, list_pic_url, retail_price");
            paramRelated.put("category_id", goodsCategory.getCategory_id());
            relatedGoods = goodsService.queryList(paramRelated);
        } else {
        	Map<String, Object> paramRelated = new HashMap<>();
            paramRelated.put("goods_ids", relatedGoodsIds);
            paramRelated.put("fields", "id, name, list_pic_url, retail_price");
            relatedGoods = goodsService.queryList(paramRelated);
        }
        resultObj.put("goodsList", relatedGoods);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result count(Object object) {
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("is_delete", 0);
        param.put("is_on_sale", 1);
        Integer goodsCount = goodsService.queryTotal(param);
        resultObj.put("goodsCount", goodsCount);
        return ResultGenerator.genSuccessResult(resultObj);
    }

    @Override
    public Result productlist(Object object, Integer categoryId,Integer isNew, Integer discount, Integer page, Integer size, String sort, String order) {
    	 Map<String, Object> params = new HashMap<>();
        params.put("is_new", isNew);
        params.put("page", page);
        params.put("limit", size);
        params.put("order", sort);
        params.put("sidx", order);
        //
        if (null != sort && sort.equals("price")) {
            params.put("sidx", "retail_price");
            params.put("order", order);
        } else if (null != sort && sort.equals("sell")) {
            params.put("sidx", "orderNum");
            params.put("order", order);
        } else {
            params.put("sidx", "id");
            params.put("order", "desc");
        }
        // 0不限 1特价 2团购
        if (null != discount && discount == 1) {
            params.put("is_hot", 1);
        } else if (null != discount && discount == 2) {
            params.put("is_group", true);
        }
        //加入分类条件
        if (null != categoryId && categoryId > 0) {
            List<Integer> categoryIds = new ArrayList<>();
            Map<String, Object> categoryParam = new HashMap<>();
            categoryParam.put("parent_id", categoryId);
            categoryParam.put("fields", "id");
            List<CategoryVo> childIds = categoryService.queryList(categoryParam);
            for (CategoryVo categoryEntity : childIds) {
                categoryIds.add(categoryEntity.getId());
            }
            categoryIds.add(categoryId);
            params.put("categoryIds", categoryIds);
        }
        //查询列表数据
        Query query = new Query(params);
        List<GoodsVo> goodsList = goodsService.queryCatalogProductList(query);
        int total = goodsService.queryTotal(query);

        // 当前购物车中
        List<CartVo> cartList = new ArrayList<>();
        if (null != getUserId()) {
            //查询列表数据
            Map<String, Object> cartParam = new HashMap<>();
            cartParam.put("user_id", getUserId());
            cartList = cartService.queryList(cartParam);
        }
        if (null != cartList && cartList.size() > 0 && null != goodsList && goodsList.size() > 0) {
            for (GoodsVo goodsVo : goodsList) {
                for (CartVo cartVo : cartList) {
                    if (goodsVo.getId().equals(cartVo.getGoods_id())) {
                        goodsVo.setCart_num(cartVo.getNumber());
                    }
                }
            }
        }
        ApiPageUtils goodsData = new ApiPageUtils(goodsList, total, query.getLimit(), query.getPage());
        goodsData.setGoodsList(goodsData.getData());
        return ResultGenerator.genSuccessResult(goodsData);
    }

}