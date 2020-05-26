package com.htz.searchserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "htz_lyric")
public class Lyric {
    @Id
    @Field(type = FieldType.Text, store = true)
    private String id;
    @Field(type = FieldType.Text, store = true)
    private String sutraId;
    @Field(type = FieldType.Text, store = true)
    private String time;
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word")
    private String content;
}
