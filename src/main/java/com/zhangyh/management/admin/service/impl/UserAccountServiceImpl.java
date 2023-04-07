package com.zhangyh.management.admin.service.impl;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyh.management.admin.mapper.UserAccountMapper;
import com.zhangyh.management.admin.mapper.UserInfoMapper;
import com.zhangyh.management.admin.model.dto.UserLoginDto;
import com.zhangyh.management.admin.model.dto.UserQueryDto;
import com.zhangyh.management.admin.model.dto.UserRegistryDto;
import com.zhangyh.management.admin.model.po.UserAccount;
import com.zhangyh.management.admin.model.po.UserInfo;
import com.zhangyh.management.admin.model.vo.UserAccountVo;
import com.zhangyh.management.admin.model.vo.UserVo;
import com.zhangyh.management.admin.service.UserAccountService;
import com.zhangyh.management.common.constants.ErrorCode;
import com.zhangyh.management.common.constants.GlobalConstants;
import com.zhangyh.management.common.exception.BusinessException;
import com.zhangyh.management.common.util.EncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyh
 * @Date 2023/4/6 16:34
 * @desc
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    private final Logger log= LoggerFactory.getLogger(UserAccountServiceImpl.class);

    public static final String privateKeyHeader="PRIVATE KEY";
    public static final String publicKeyHeader="PUBLIC KEY";
    public static final String privateKeyFile="server.key";
    public static final String publicKeyFIle="pub.key";
    public static final String algorithm="RSA";

    @Resource
    UserAccountMapper userAccountMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public UserAccount getCurrentUser(HttpServletRequest request) {
        UserAccountVo user = (UserAccountVo)request.getSession().getAttribute(GlobalConstants.SESSION_KEY);
        if(user==null||user.getId()==null){
            throw new BusinessException(ErrorCode.UNLOGIN);
        }
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(UserAccount.ID,user.getId());
        UserAccount userAccount = userAccountMapper.selectOne(queryWrapper);
        if(userAccount==null){
            throw new BusinessException(ErrorCode.NOT_EXIST_USER);
        }
        return userAccount;
    }

    @Override
    public UserAccountVo baseLogin(UserLoginDto user,HttpServletRequest request) {
        String password = user.getPassword();
        if(user.getUsername().length()<5){
            throw new BusinessException(ErrorCode.INVALID_PARAMS,"账号长度必须大于5位");
        }
        if(password.length()<6){
            throw new BusinessException(ErrorCode.INVALID_PARAMS,"密码长度必须长于6位");
        }
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq(UserAccount.USERNAME,user.getUsername());
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        if(userAccount==null){
            throw new BusinessException(ErrorCode.NOT_EXIST_USER);
        }
        String decodePassword=null;
        try {
            PrivateKey privateKey = (PrivateKey) EncryptUtils.get(privateKeyFile, algorithm, true,privateKeyHeader);
            decodePassword = EncryptUtils.decryptByPrivateKey(Base64.encode(privateKey.getEncoded()), userAccount.getPassword());
        } catch (Exception e) {
            log.info("----解密失败----");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if(!decodePassword.equals(password)){
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        UserAccountVo userAccountVo = new UserAccountVo(userAccount);
        //用户态保存
        request.getSession().setAttribute(GlobalConstants.SESSION_KEY,userAccountVo);
        return userAccountVo;
    }

    @Override
    public void userRegister(UserRegistryDto registryDto) {
        if(!registryDto.getPassword().equals(registryDto.getCheckPassword())){
            throw new BusinessException(ErrorCode.INVALID_PARAMS,"两次密码一样");
        }
        String userAccount = registryDto.getUsername();
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(UserAccount.USERNAME,userAccount);
        if(userAccountMapper.selectCount(queryWrapper)>0){
            throw new BusinessException(ErrorCode.ACCOUNT_DUPLICATE);
        }
        //组装账号信息
        UserAccount uAccount = new UserAccount();
        uAccount.setCreateTime(new Date());
        uAccount.setUsername(userAccount);
        String encPassword=null;
        try {
            PublicKey publicKey = (PublicKey) EncryptUtils.get(publicKeyFIle, algorithm, false,publicKeyHeader);
            encPassword = EncryptUtils.encryptByPublicKey(Base64.encode(publicKey.getEncoded()), registryDto.getPassword());
        } catch (Exception e) {
            log.info("----加密失败----");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        uAccount.setPassword(encPassword);
        userAccountMapper.insert(uAccount);
        //组装用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setAccountId(uAccount.getId());
        userInfo.setCreateTime(new Date());
        if(registryDto.getBirthday()!=null){
            userInfo.setBirthday(registryDto.getBirthday());
        }
        if(registryDto.getGender()!=null){
            userInfo.setGender(registryDto.getGender());
        }
        if(registryDto.getMobile()!=null){
            userInfo.setMobile(registryDto.getMobile());
        }
        if(registryDto.getName()!=null){
            userInfo.setName(registryDto.getName());
        }
        if(registryDto.getEmail()!=null){
            userInfo.setEmail(registryDto.getEmail());
        }
        userInfoMapper.insert(userInfo);
    }

    @Override
    public List<UserVo> selectUserVoPage(Page<UserVo> page, UserQueryDto queryDto) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_account.id,user_account.username,user_account.permissions,user_account.state" ,
                        " user_info.name, user_info.gender, user_info.mobile,user_info.email, user_info.birthday")
                .eq("user_account.deleted", 0)
                .eq(StringUtils.isNotBlank(queryDto.getUsername()), "user_account.username", queryDto.getUsername())
                .like(StringUtils.isNotBlank(queryDto.getName()),"user_info.name",queryDto.getName())
                .like(StringUtils.isNotBlank(queryDto.getPermissions()),"user_account.permissions",queryDto.getPermissions())
                .eq(queryDto.getState()!=null,"user_account.state",queryDto.getState())
                .eq(queryDto.getGender()!=null,"user_info.gender",queryDto.getGender())
                .eq(StringUtils.isNotBlank(queryDto.getMobile()),"user_info.mobile",queryDto.getMobile())
                .eq(StringUtils.isNotBlank(queryDto.getEmail()),"user_info.email",queryDto.getEmail())
                .orderByDesc("user_account.create_time");
        return userAccountMapper.selectUserVoPage(page, queryWrapper);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if(request.getSession().getAttribute(GlobalConstants.SESSION_KEY)==null){
            throw new BusinessException(ErrorCode.UNLOGIN);
        }
        request.removeAttribute(GlobalConstants.SESSION_KEY);
        return false;
    }


}
