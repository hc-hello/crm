package com.shsxt.crm.service;

import com.shsxt.base.BaseService;
import com.shsxt.crm.dao.UserMapper;
import com.shsxt.crm.dao.UserRoleMapper;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.utils.AssertUtil;
import com.shsxt.crm.utils.Md5Util;
import com.shsxt.crm.utils.PhoneUtil;
import com.shsxt.crm.utils.UserIDBase64;
import com.shsxt.crm.vo.Role;
import com.shsxt.crm.vo.User;
import com.shsxt.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

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

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user){

        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        User temp=userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(null!=temp,"用户名已存在！");
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        AssertUtil.isTrue(insertHasKey(user)==null,"用户添加失败！");

        int userId=user.getId();
        relaionUserRole(userId,user.getRoleIds());

    }

    private void relaionUserRole(int userId, List<Integer> roleIds) {
        int count=userRoleMapper.countUserRoleByUserId(userId);
        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败！");
        }
        if (null!=roleIds || roleIds.size()>0){
            List<UserRole> userRoles=new ArrayList<>();
            roleIds.forEach(roleId->{
                UserRole userRole=new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRoles.add(userRole);
            });
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles)<userRoles.size(),"用户角色分配失败！");
        }
    }

    private void checkParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱地址！");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空！");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(phone)),"手机号格式不正确！");

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        AssertUtil.isTrue(null==user.getId() || null==selectByPrimaryKey(user.getId()),"待更新记录不存在！");
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        User temp=userMapper.queryUserByUserName(user.getUserName());
        if(null!=temp && temp.getIsValid()==1){
            AssertUtil.isTrue(!(user.getId().equals(temp.getId())),"该用户已存在！");
        }
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"用户更新失败！");

        relaionUserRole(user.getId(),user.getRoleIds());

    }

    public void deleteUser(Integer userId){
        User user=selectByPrimaryKey(userId);
        AssertUtil.isTrue(null==userId || null==user,"待更新记录不存在！");
        int count=userRoleMapper.countUserRoleByUserId(userId);
        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败！");
        }
        user.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"用户记录删除失败！");
    }
}
