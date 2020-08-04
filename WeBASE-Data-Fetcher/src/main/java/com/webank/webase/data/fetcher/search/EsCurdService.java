package com.webank.webase.data.fetcher.search;

import com.webank.webase.data.fetcher.base.tools.JacksonUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EsCurdService {

    @Autowired
    private RestHighLevelClient rhlClient;

    public String createIndex(String indexName) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        CreateIndexResponse response =
                rhlClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return response.index();
    }

    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
        deleteIndexRequest.indices(indexName);
        AcknowledgedResponse acknowledgedResponse =
                rhlClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        boolean isDeleted = acknowledgedResponse.isAcknowledged();
        log.info("deleteIndex：{}", isDeleted);
        return isDeleted;
    }

    public boolean existIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        return rhlClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public RestStatus insert(String indexName, String id, Object object) throws IOException {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(indexName).id(id).source(JacksonUtils.objToString(object),
                XContentType.JSON);
        IndexResponse response = rhlClient.index(indexRequest, RequestOptions.DEFAULT);
        return response.status();
    }

    public SearchResponse search(Integer from, Integer size, String indexName,
            SearchSourceBuilder sourceBuilder, QueryBuilder queryBuilder) throws IOException {
        if (!existIndex(indexName)) {
            return null;
        }
        SearchRequest searchRequest = new SearchRequest(indexName);
        sourceBuilder.timeout(new TimeValue(120, TimeUnit.SECONDS)).explain(true)
                .query(queryBuilder);
        if (from != null && size != null) {
            sourceBuilder.from((from - 1) * size).size(size);
        }
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
        return response;
    }

    public RestStatus update(String indexName, String id, Object object) throws IOException {
        UpdateRequest request = new UpdateRequest();
        request.index(indexName).id(id).doc(JacksonUtils.objToString(object), XContentType.JSON);
        UpdateResponse response = rhlClient.update(request, RequestOptions.DEFAULT);
        return response.status();
    }

    public RestStatus delete(String indexName, String id) throws IOException {
        DeleteRequest request = new DeleteRequest();
        request.index(indexName).id(id);
        DeleteResponse response = rhlClient.delete(request, RequestOptions.DEFAULT);
        return response.status();
    }
}
