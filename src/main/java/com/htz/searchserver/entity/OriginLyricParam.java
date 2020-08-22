package com.htz.searchserver.entity;

import com.htz.searchserver.utils.ExampleUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class OriginLyricParam {
    @ApiModelProperty(value = "id", example = "xxx")
    private String id;
    @ApiModelProperty(value = "专辑名称", example = "xxx")
    private String sutraTitle;
    @ApiModelProperty(value = "音频标题", example = "xxx")
    private String title;
    @ApiModelProperty(value = "原文内容", example = "xxx")
    private String content;
    @ApiModelProperty(value = "需要跳转网址1的url", example = ExampleUtil.EXAMPLE_URL)
    private String url1;
    @ApiModelProperty(value = "需要跳转网址2的url", example = ExampleUtil.EXAMPLE_URL)
    private String url2;
}
