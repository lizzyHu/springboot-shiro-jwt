package com.lizzy.shirojwt.config.shiro.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * preHandle -> isAccessAllowed -> isLoginAttempt -> executeLogin
 */
@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

	/**
	 * 判断用户是否想要登录 -- 检查header里面是否包含Token字段即可
	 * 已登录，返回true
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		
		HttpServletRequest hsr = (HttpServletRequest) request;
		String token = hsr.getHeader("Token");
		return (!StringUtils.isEmpty(token));
	}

	/**
     * 如果带有 token，则对 token 进行检查，否则直接通过
     */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		
		//判断请求的请求头是否带上 "Token"
		if (isLoginAttempt(request, response)) {
			//如果存在，则进入 executeLogin 方法执行登入，检查 token 是否正确
			try {
				executeLogin(request, response);
				return true;
			} catch (Exception e) {
				//token 错误
				responseError(response, e.getMessage());
			}
		}
		//如果请求头不存在 Token，则可能是执行登陆操作或者是游客状态访问，无需检查 token，直接返回 true
		return true;
	}

	/**
     * 执行登陆操作
     */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Token");
        JWTToken jwtToken = new JWTToken(token);
		
		return super.executeLogin(request, response);
	}

	/**
     * 对跨域提供支持
     */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(
        		"Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader(
        		"Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader(
        		"Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
		
		return super.preHandle(request, response);
	}
	
	/**
     * 将非法请求跳转到 /unauthorized/**
     */
    private void responseError(ServletResponse response, String message) {
        
    	try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //设置编码，否则中文字符在重定向时会变为空字符串
            message = URLEncoder.encode(message, "UTF-8");
            httpServletResponse.sendRedirect("/unauthorized/" + message);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
}
