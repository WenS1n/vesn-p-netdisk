package cn.vesns.netdisk.service;

import cn.vesns.netdisk.config.es.FileSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author : vip865047755@126.com
 * @version :JDK1.8
 * @date : 2021-11-02 20:43
 * @File : ElasticSerachService.java
 * @software: IntelliJ IDEA
 */
public interface ElasticSearchService extends ElasticsearchRepository<FileSearch,Long> {
}
