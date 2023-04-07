package com.zhangyh.management.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.zhangyh.management.admin.annotation.AuthCheck;
import com.zhangyh.management.admin.annotation.Log;
import com.zhangyh.management.admin.annotation.PermissionEnum;
import com.zhangyh.management.admin.model.dto.UserLoginDto;
import com.zhangyh.management.admin.model.dto.UserQueryDto;
import com.zhangyh.management.admin.model.dto.UserRegistryDto;
import com.zhangyh.management.admin.model.po.UserAccount;
import com.zhangyh.management.admin.model.po.UserInfo;
import com.zhangyh.management.admin.model.vo.UserAccountVo;
import com.zhangyh.management.admin.model.vo.UserVo;
import com.zhangyh.management.admin.model.vo.VerifyImgResult;
import com.zhangyh.management.admin.service.UserAccountService;
import com.zhangyh.management.admin.service.UserInfoService;
import com.zhangyh.management.admin.service.impl.ImgVerifyCodeServiceImpl;
import com.zhangyh.management.common.constants.ErrorCode;
import com.zhangyh.management.common.constants.GlobalConstants;
import com.zhangyh.management.common.exception.BusinessException;
import com.zhangyh.management.common.http.response.ApiResponse;
import com.zhangyh.management.common.http.response.ResponseHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyh
 * @Date 2023/4/6 15:03
 * @desc
 */
@Api("用户账户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserAccountService userService;

    @Resource
    UserInfoService userInfoService;

    @Resource
    ImgVerifyCodeServiceImpl imgVerifyCodeService;

    @Log
    @ApiOperation(value = "用户信息分页查询",httpMethod = "POST")
    @PostMapping("/pageList")
    @AuthCheck(value = PermissionEnum.USER)
    public ApiResponse<PageDTO<UserVo>> pageList(@RequestBody UserQueryDto queryDto){
        long current = 1;
        long size = 10;
        if(queryDto.getCurrent()!=null){
            current=queryDto.getCurrent();
        }
        if(queryDto.getPageSize()!=null){
            size=queryDto.getPageSize();
        }
        Page<UserVo> page = new Page<>(current, size);
        PageDTO<UserVo> pageDTO = new PageDTO<>(current, size);
        List<UserVo> userVoList = userService.selectUserVoPage(page, queryDto);
        pageDTO.setRecords(userVoList);
        pageDTO.setTotal(page.getTotal());
        return ResponseHelper.success(pageDTO);
    }

    @ApiOperation(value = "基础用户登录",httpMethod = "POST")
    @PostMapping(value = "/baseLogin" )
    public ApiResponse<UserAccountVo> baseLogin(@Validated @RequestBody UserLoginDto user, HttpServletRequest request){
        String verifyCode = user.getVerifyCode();
        String randomKey = user.getRandomKey();
//        imgVerifyCodeService.checkCaptcha(randomKey,verifyCode);
        UserAccountVo userAccountVo = userService.baseLogin(user,request);
        return ResponseHelper.success(userAccountVo);
    }

    @ApiOperation(value = "退出登录",httpMethod = "GET")
    @GetMapping("/logout")
    public ApiResponse<Boolean> userLogout(HttpServletRequest request){
        if(request==null){
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        boolean result = userService.userLogout(request);
        return ResponseHelper.success(result);
    }

    @ApiOperation(value = "获取验证码",httpMethod = "GET")
    @GetMapping("/imgVerifyCode")
    public ApiResponse<VerifyImgResult> getVerifyCode(@RequestParam("length") Integer length){
        if(length==null||length==0){
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
      return  ResponseHelper.success(imgVerifyCodeService.generateVerifyCoe(length));
    }

    @ApiOperation(value = "用户注册",httpMethod = "POST")
    @PostMapping("/userRegistry")
    public  ApiResponse<UserAccountVo> userRegistry(@RequestBody @Validated UserRegistryDto registryDto){
        userService.userRegister(registryDto);
        return ResponseHelper.success();
    }

    @Log
    @AuthCheck(value = PermissionEnum.ADMIN)
    @ApiOperation(value = "账号删除",httpMethod = "DELETE")
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> deleteUser(@RequestParam String ids){
        if(StringUtils.isBlank(ids)){
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        String[] id = ids.split(",");
        boolean userAccountDeleteFlag = userService.removeById(id);
        Arrays.stream(id).forEach(i->{
            QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
            userInfoQueryWrapper.eq(UserInfo.ACCOUNT_ID,id);
            userInfoService.remove(userInfoQueryWrapper);
        });
        return ResponseHelper.success(userAccountDeleteFlag);
    }

    @AuthCheck(value = PermissionEnum.USER)
    @ApiOperation(value = "查询用户账号",httpMethod = "GET")
    @GetMapping("/getUserAccount")
    public ApiResponse<List<UserAccountVo>> getUser(String ids){
        if(StringUtils.isBlank(ids)){
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        String[] id = ids.split(",");
        List<UserAccount> userAccounts = userService.listByIds(Arrays.asList(id));
        List<UserAccountVo> res = userAccounts.stream().map(UserAccountVo::new).collect(Collectors.toList());
        return ResponseHelper.success(res);
    }
}
