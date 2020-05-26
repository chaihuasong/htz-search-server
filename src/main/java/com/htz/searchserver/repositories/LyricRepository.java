package com.htz.searchserver.repositories;

import com.htz.searchserver.entity.Lyric;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LyricRepository extends ElasticsearchRepository <Lyric, String> {
}
