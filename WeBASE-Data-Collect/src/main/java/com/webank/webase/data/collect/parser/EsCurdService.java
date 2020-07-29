package com.webank.webase.data.collect.parser;

import com.webank.webase.data.collect.base.tools.JacksonUtils;
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
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
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
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("dynamic", true)
                .startObject("properties")
                    .startObject("id")
                        .field("type","long")
                    .endObject()
                    .startObject("transHash")
                        .field("type","text")
                    .endObject()
                    .startObject("blockNumber")
                        .field("type","keyword")
                    .endObject()
                    .startObject("userName")
                        .field("type","text")
                    .endObject()
                    .startObject("userAddress")
                        .field("type","text")
                    .endObject()
                    .startObject("userType")
                        .field("type","integer")
                    .endObject()
                    .startObject("contractName")
                        .field("type","text")
                    .endObject()
                    .startObject("contractAddress")
                        .field("type","text")
                    .endObject()
                    .startObject("interfaceName")
                        .field("type","text")
                    .endObject()
                    .startObject("transType")
                        .field("type","integer")
                    .endObject()
                    .startObject("transParserType")
                        .field("type","integer")
                    .endObject()
                    .startObject("input")
                        .field("type","text")
                        .field("analyzer", "ik_max_word")
                    .endObject()
                    .startObject("output")
                        .field("type","text")
                        .field("analyzer", "ik_max_word")
                    .endObject()
                    .startObject("logs")
                        .field("type","text")
                        .field("analyzer", "ik_max_word")
                    .endObject()
                    .startObject("blockTimestamp")
                        .field("type","date")
                        .field("format", "yyyy-MM-dd HH:mm:ss")
                    .endObject()
                    .startObject("createTime")
                        .field("type","date")
                        .field("format", "yyyy-MM-dd HH:mm:ss")
                    .endObject()
                    .startObject("modifyTime")
                        .field("type","date")
                        .field("format", "yyyy-MM-dd HH:mm:ss")
                    .endObject()
                .endObject()
            .endObject();
        Settings settings = Settings.builder().put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0).build();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName, settings);
        createIndexRequest.mapping("doc", builder);
        CreateIndexResponse response =
                rhlClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return response.index();
    }

    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
        deleteIndexRequest.indices(indexName);
        AcknowledgedResponse acknowledgedResponse = rhlClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        boolean isDeleted = acknowledgedResponse.isAcknowledged();
        log.info("deleteIndex：{}", isDeleted);
        return isDeleted;
    }

    public boolean existIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        return rhlClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public RestStatus insert(String indexName, String id, Object object)
            throws IOException {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(indexName).id(id).source(JacksonUtils.objToString(object),
                XContentType.JSON);
        IndexResponse response = rhlClient.index(indexRequest, RequestOptions.DEFAULT);
        return response.status();
    }

    public SearchResponse search(int from, int size, String indexName,
            SearchSourceBuilder sourceBuilder, QueryBuilder queryBuilder) throws IOException {
        if (!existIndex(indexName)) {
            return null;
        }
        SearchRequest searchRequest = new SearchRequest(indexName);
        sourceBuilder.timeout(new TimeValue(120, TimeUnit.SECONDS)).explain(true)
                .from((from - 1) * size).size(size).query(queryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
        return response;
    }

    public RestStatus update(String indexName, String id, Object object)
            throws IOException {
        UpdateRequest request = new UpdateRequest();
        request.index(indexName).id(id).doc(JacksonUtils.objToString(object),
                XContentType.JSON);
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
