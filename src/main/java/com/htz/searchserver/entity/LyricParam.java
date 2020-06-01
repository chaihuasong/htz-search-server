package com.htz.searchserver.entity;

import com.htz.searchserver.utils.ExampleUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class LyricParam {
    @ApiModelProperty(value = "音频ID", example = "5ec67c56e2e76d28eb049215")
    private String itemId;
    @ApiModelProperty(value = "专辑ID", example = "5ec67915e2e76d28eb049211")
    private String sutraId;
    @ApiModelProperty(value = "音频名称", example = "001.幸福的诀窍")
    private String title;
    @ApiModelProperty(value = "原文、讲解详细内容(需要包含换行)", example = ExampleUtil.EXAMPLE_CONTENT)
    private String content;
}
