package com.github.wechat.cloud.inter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.github.common.exception.ApiRRException;
import com.github.model.entity.wechat.TokenEntity;
import com.github.wechat.cloud.annotation.IgnoreAuth;
import com.github.wechat.cloud.service.TokenService;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-03-23 15:38
 */
public class AuthorizationInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenService tokenService;

	public static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";
	public static final String LOGIN_TOKEN_KEY = "X-Nideshop-Token";

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		IgnoreAuth annotation;
		if (!(handler instanceof HandlerMethod)) {
			return true;
		} 

		HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
		annotation = method.getAnnotation(IgnoreAuth.class);
		
		// 如果有@IgnoreAuth注解，则不验证token
		if (annotation != null) {
			return true;
		}

		// 从header中获取token
		String token = request.getHeader(LOGIN_TOKEN_KEY);
		// 如果header中不存在token，则从参数中获取token
		if (StringUtils.isBlank(token)) {
			token = request.getParameter(LOGIN_TOKEN_KEY);
		}

		// token为空
		if (StringUtils.isBlank(token)) {
			throw new ApiRRException("请先登录", 401);
		}

		// 查询token信息
		TokenEntity tokenEntity = tokenService.queryByToken(token);
		if (tokenEntity == null || tokenEntity.getExpireTime().getTime() < System.currentTimeMillis()) {
			throw new ApiRRException("token失效，请重新登录", 401);
		}

		// 设置userId到request里，后续根据userId，获取用户信息
		request.setAttribute(LOGIN_USER_KEY, tokenEntity.getUserId());

		return true;
		
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
