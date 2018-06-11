package com.github.sys.cloud.oss;

import com.github.common.constant.ConfigConstant;
import com.github.common.utils.SpringContextUtils;
import com.github.sys.cloud.enums.CloudEnum;
import com.github.sys.cloud.service.SysConfigService;

/**
 * 文件上传Factory
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-26 10:18
 */
public final class OSSFactory {
    private static SysConfigService sysConfigService;

    static {
        OSSFactory.sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
    }

    public static CloudStorageService build() {
        //获取云存储配置信息
        CloudStorageConfig config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorageConfig.class);

        if (config.getType() == CloudEnum.QINIU.getValue()) {
            return new QiniuCloudStorageService(config);
        } else if (config.getType() == CloudEnum.ALIYUN.getValue()) {
            return new AliyunCloudStorageService(config);
        } else if (config.getType() == CloudEnum.QCLOUD.getValue()) {
            return new QcloudCloudStorageService(config);
        }

        return null;
    }

}
