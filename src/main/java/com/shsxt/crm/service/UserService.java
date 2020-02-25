package com.shsxt.crm.service;

import com.shsxt.base.BaseService;
import com.shsxt.crm.dao.UserMapper;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.utils.AssertUtil;
import com.shsxt.crm.utils.Md5Util;
import com.shsxt.crm.utils.UserIDBase64;
import com.shsxt.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     */
    public UserModel login(String userName,String userPwd){

        //对参数进行非空校验
        checkLoginParams(userName,userPwd);
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user==null,"用户名不存在");
        AssertUtil.isTrue(user.getUserPwd().equals(Md5Util.encode(userPwd)),"用户密码错误");
        System.out.println(user.getUserPwd());
        return buildUserModel(user);
    }

    private UserModel buildUserModel(User user) {
        return new UserModel(UserIDBase64.encoderUserID(user.getId()),user.getUserName(),user.getTrueName());
    }

    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }

    /**
     * 用户密码更新
     */
    public void updateUserpwd(Integer userId,String oldPassword,String newPassword,String confirmPassword){

        /**
         * 1、参数校验
         *      userId 非空，记录必须存在
         *      oldPassword 非空，必须与数据库中的密码一致
         *      newPassword 非空，新密码不能与旧密码一致
         *      confirmPassword 非空，两次密码一致
         * 2、设置新密码
         *      新密码加密
         *
         * 3、执行更新
         */
        checkPassword(userId,oldPassword,newPassword,confirmPassword);
        User user=selectByPrimaryKey(userId);
        user.setUserPwd(Md5Util.encode(newPassword));
        Integer row = updateByPrimaryKeySelective(user);
        AssertUtil.isTrue(row<1,"密码更新失败");

    }

    private void checkPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        User user=selectByPrimaryKey(userId);
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入旧密码");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"请输入确认密码");

        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"旧密码输入错误");
        AssertUtil.isTrue(user.getUserPwd().equals(newPassword),"新密码不能与旧密码相同");
        AssertUtil.isTrue(!newPassword.equals(confirmPassword),"两次密码输入不一致");
    }
}
