package com.shsxt.crm.controller;

import com.shsxt.crm.exceptions.ParamsException;
import com.shsxt.crm.model.ResultInfo;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.service.UserService;
import com.shsxt.crm.utils.LoginUserUtil;
import com.shsxt.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("user/queryUserByUserId")
    @ResponseBody
    public User queryUserByUserId(Integer userId){
        return userService.selectByPrimaryKey(userId);
    }

    @PostMapping("user/login")
    @ResponseBody
    public ResultInfo login(String userName,String userPwd){
        ResultInfo resultInfo=new ResultInfo();
        UserModel usermodel = userService.login(userName, userPwd);
        resultInfo.setResult(usermodel);
        return resultInfo;
    }

    @RequestMapping("user/updateUserPwd")
    @ResponseBody
    public ResultInfo updateUserPwd(HttpServletRequest request,String oldPassword,String newPassword,String confirmPassword){
        ResultInfo resultInfo=new ResultInfo();
        userService.updateUserpwd(LoginUserUtil.releaseUserIdFromCookie(request),oldPassword,newPassword,confirmPassword);
        return resultInfo;
    }


}
