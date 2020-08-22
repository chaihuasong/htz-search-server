package com.htz.searchserver.entity;

import com.htz.searchserver.utils.ExampleUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class OriginLyricParam {
    @ApiModelProperty(value = "音频ID", example = "xxx")
    private String itemId;
    @ApiModelProperty(value = "专辑ID", example = "xxx")
    private String sutraId;
    @ApiModelProperty(value = "音频名称", example = "001.幸福的诀窍")
    private String title;
    @ApiModelProperty(value = "原文内容", example = "xxx")
    private String content;
    @ApiModelProperty(value = "需要跳转网址的url", example = ExampleUtil.EXAMPLE_URL)
    private String url;
}
