package com.zhangyh.management.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyh.management.admin.model.dto.UserLoginDto;
import com.zhangyh.management.admin.model.dto.UserQueryDto;
import com.zhangyh.management.admin.model.dto.UserRegistryDto;
import com.zhangyh.management.admin.model.po.UserAccount;
import com.zhangyh.management.admin.model.vo.UserAccountVo;
import com.zhangyh.management.admin.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhangyh
 * @Date 2023/4/6 15:25
 * @desc
 */
public interface UserAccountService  extends IService<UserAccount> {

   /**
    * 获取登录的用户
    * @param request /
    * @return /
    */
   UserAccount getCurrentUser(HttpServletRequest request);

   /**
    * 登录
    * @param user /
    * @param request /
    * @return /
    */
   UserAccountVo baseLogin(UserLoginDto user,HttpServletRequest request);

   /**
    * 注册
    * @param registryDto /
    */
   void userRegister(UserRegistryDto registryDto);

   /**
    * 分页查询+模糊查询
    * @param page /
    * @param queryDto /.
    * @return /
    */
   List<UserVo> selectUserVoPage(Page<UserVo> page, UserQueryDto queryDto);

   /**
    * 退出登录
    * @param request /
    * @return /
    */
    boolean userLogout(HttpServletRequest request);
}
