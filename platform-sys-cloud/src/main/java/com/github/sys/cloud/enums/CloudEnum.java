package com.github.sys.cloud.enums;

public enum CloudEnum {

	 /**
     * 七牛云
     */
    QINIU(1),
    /**
     * 阿里云
     */
    ALIYUN(2),
    /**
     * 腾讯云
     */
    QCLOUD(3);

    private int value;

    private CloudEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
