package com.htz.searchserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "htz_origin_lyric")
public class OriginLyric {
    @Id
    @Field(type = FieldType.Text, store = true)
    private String itemId;
    @Field(type = FieldType.Text, store = true)
    private String sutraId;
    @Field(type = FieldType.Text, store = true)
    private String time = "";
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Text, store = true)
    private String url = "";
}
