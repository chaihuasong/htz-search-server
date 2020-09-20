package com.htz.searchserver.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class IDQuery {
    @ApiModelProperty(value = "ID")
    private String id;
}
