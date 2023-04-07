package com.zhangyh.management.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyh.management.admin.mapper.UserInfoMapper;
import com.zhangyh.management.admin.model.po.UserInfo;
import com.zhangyh.management.admin.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
 * @author zhangyh
 * @Date 2023/4/7 12:21
 * @desc
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
}
