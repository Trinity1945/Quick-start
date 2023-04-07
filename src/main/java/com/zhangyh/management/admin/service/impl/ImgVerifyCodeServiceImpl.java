package com.zhangyh.management.admin.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.util.IdUtil;
import com.zhangyh.management.admin.model.vo.VerifyImgResult;
import com.zhangyh.management.common.constants.ErrorCode;
import com.zhangyh.management.common.exception.BusinessException;
import com.zhangyh.management.common.util.CaptchaUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhangyh
 * @Date 2023/4/6 17:30
 * @desc
 */
@Service
public class ImgVerifyCodeServiceImpl {

    /**
     * 验证码缓存
     */
    private final FIFOCache<String, String> verifyCodeCache = CacheUtil.newFIFOCache(100,60 * 1000L);

    public boolean verifyCaptcha(String uuid,String code){
        String verifyCode = verifyCodeCache.get(uuid);
        return Objects.equals(verifyCode,code);
    }

    public void checkCaptcha(String uuid,String code){
        boolean flag = verifyCaptcha(uuid, code);
        if(!flag){
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }
    }

    public VerifyImgResult generateVerifyCoe(int length){
        String verifyCode = CaptchaUtil.verifyCode(length);
        String uuid = IdUtil.fastUUID();
        verifyCodeCache.put(uuid,verifyCode);
        byte[] imageCode = CaptchaUtil.createImageCode(verifyCode);
        VerifyImgResult verifyImgResult = new VerifyImgResult();
        verifyImgResult.setImgBase64(imageCode);
        verifyImgResult.setUuid(uuid);
        return verifyImgResult;
    }
}
