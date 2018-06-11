package com.github.common.result;

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
	//http响应code
    SUCCESS(200),//成功
    FAIL(400),//失败
    UNAUTHORIZED(401),//签名错误
    UNIONID_NULL(402),//unionid为空
    UNIONID_EFFECT(403),//unionid失效
    NOT_FOUND(404),//接口不存在
    INTERNAL_SERVER_ERROR(500),//服务器内部错误
    
    //自定义响应code
	PARAM_NULL(50001),//参数不全或者参数为空
	GETUSERINFO_FAIL(50002); //解密获取用户信息失败
    public int code;

    ResultCode(int code) {
        this.code = code;
    }
}
