package com.webank.webase.data.fetcher.search;

import com.webank.webase.data.fetcher.search.entity.ElasticSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends ElasticsearchRepository<ElasticSearchDto, String> {

    Page<ElasticSearchDto> findByUserAddressOrContractAddressLike(String userAddress, String contractAddress,
            Pageable pageable);
}
