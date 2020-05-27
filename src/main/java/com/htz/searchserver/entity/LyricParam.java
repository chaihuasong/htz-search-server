package com.htz.searchserver.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@ApiModel
public class LyricParam {
    @ApiModelProperty("音频ID")
    private String id;
    @ApiModelProperty("专辑ID")
    private String sutraId;
    @ApiModelProperty("音频名称")
    private String title;
    @ApiModelProperty("原文、讲解详细内容")
    private String content;
}
