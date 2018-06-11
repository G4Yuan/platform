package com.github.wechat.cloud.controller;

import org.springframework.web.bind.annotation.RestController;

import com.github.common.result.Result;
import com.github.common.result.ResultGenerator;
import com.github.common.utils.StringUtils;
import com.github.model.entity.sys.SysRegionEntity;
import com.github.model.entity.wechat.RegionVo;
import com.github.wechat.api.ApiRegionCloud;
import com.github.wechat.cloud.base.ApiBaseAction;
import com.github.wechat.cloud.cache.RegionCacheUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiRegionController extends ApiBaseAction implements ApiRegionCloud{

    @Override
    public Result list(Integer parentId) {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getChildrenByParentId(parentId);
        List<RegionVo> regionVoList = new ArrayList<>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return ResultGenerator.genSuccessResult(regionVoList);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Result provinceList() {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getAllProvice();
        List<RegionVo> regionVoList = new ArrayList<>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return ResultGenerator.genSuccessResult(regionVoList);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Result provinceList(String proviceName) {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getChildrenCity(proviceName);
        List<RegionVo> regionVoList = new ArrayList<>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return ResultGenerator.genSuccessResult(regionVoList);
    }

    @Override
    public Result distinctList(String proviceName, String cityName) {
        List<SysRegionEntity> regionEntityList = RegionCacheUtil.getChildrenDistrict(proviceName, cityName);
        List<RegionVo> regionVoList = new ArrayList<>();
        if (null != regionEntityList && regionEntityList.size() > 0) {
            for (SysRegionEntity sysRegionEntity : regionEntityList) {
                regionVoList.add(new RegionVo(sysRegionEntity));
            }
        }
        return ResultGenerator.genSuccessResult(regionVoList);
    }

    @Override
    public Result info(Integer regionId) {
        SysRegionEntity regionEntity = RegionCacheUtil.getAreaByAreaId(regionId);
        return ResultGenerator.genSuccessResult(new RegionVo(regionEntity));
    }

    @Override
    public Result regionIdsByNames(String provinceName, String cityName, String districtName) {
        Map<String, Object> resultObj = new HashMap<>();
        Integer provinceId = 0;
        Integer cityId = 0;
        Integer districtId = 0;
        if (null != provinceName) {
            provinceId = RegionCacheUtil.getProvinceIdByName(provinceName);
        }
        if (null != provinceId && !StringUtils.isNullOrEmpty(cityName)) {
            cityId = RegionCacheUtil.getCityIdByName(provinceId, cityName);
        }
        if (null != provinceId && null != cityId && !StringUtils.isNullOrEmpty(districtName)) {
            districtId = RegionCacheUtil.getDistrictIdByName(provinceId, cityId, districtName);
        }
        resultObj.put("provinceId", provinceId);
        resultObj.put("cityId", cityId);
        resultObj.put("districtId", districtId);
        return ResultGenerator.genSuccessResult(resultObj);
    }
}