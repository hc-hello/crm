package com.shsxt.crm.interceptors;

import com.shsxt.crm.exceptions.NoLoginException;
import com.shsxt.crm.service.UserService;
import com.shsxt.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        int userId= LoginUserUtil.releaseUserIdFromCookie(request);
        /*if(userId==0 || null==userService.selectByPrimaryKey(userId)){
            response.sendRedirect(request.getContextPath()+"/index");
            return false;
        }*/
        System.out.println(userId);
        if(userId==0 || null==userService.selectByPrimaryKey(userId)){
            throw new NoLoginException();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName());
            }
        }
        return true;
    }
}
