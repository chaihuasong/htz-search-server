package com.htz.searchserver.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PageQuery {
    @ApiModelProperty(value = "页面索引", example = "0")
    private int page_index;
    @ApiModelProperty(value = "每页条数", example = "20")
    private int page_size;

    public PageQuery(int pageIndex, int pageSize) {
        this.page_index = pageIndex;
        this.page_size = pageSize;
    }
}
