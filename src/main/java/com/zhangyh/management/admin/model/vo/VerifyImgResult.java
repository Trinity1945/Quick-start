package com.zhangyh.management.admin.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangyh
 * @Date 2023/4/6 17:31
 * @desc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("验证码生成类")
public class VerifyImgResult {
    @ApiModelProperty(value = "验证码图片", example = "data:image/png;base64,iajsiAAA...")
    private byte[] imgBase64;

    @ApiModelProperty(value = "验证码 UUID", example = "c140a792-4ca2-4dac-8d4c-35750b78524f")
    private String uuid;
}
