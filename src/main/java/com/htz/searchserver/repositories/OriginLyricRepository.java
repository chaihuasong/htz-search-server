package com.htz.searchserver.repositories;

import com.htz.searchserver.entity.OriginLyric;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OriginLyricRepository extends ElasticsearchRepository <OriginLyric, String> {
}
