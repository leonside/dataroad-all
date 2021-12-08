
package com.leonside.dataroad.plugin.es.reader;

import com.google.common.collect.Lists;
import com.leonside.dataroad.flink.inputformat.GenericRichInputFormat;
import com.leonside.dataroad.plugin.es.EsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.core.io.GenericInputSplit;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.types.Row;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * InputFormat for Elasticsearch
 *
 */
public class EsInputFormat extends GenericRichInputFormat {

    protected String address;

    protected String username;

    protected String password;

    protected String[] index;

    protected String[] type;

    protected String query;

    protected List<String> columnValues;

    protected List<String> columnTypes;

    protected List<String> columnNames;

    protected int batchSize = 10;

    protected Map<String,Object> clientConfig;

    protected long keepAlive = 1;

    private transient RestHighLevelClient client;

    private Iterator<Map<String, Object>> iterator;

    private transient SearchRequest searchRequest;

    private transient Scroll scroll;

    private String scrollId;

    @Override
    public void openInputFormat() throws IOException {
        super.openInputFormat();
    }

    @Override
    public void doOpen(InputSplit inputSplit) throws IOException {
        GenericInputSplit genericInputSplit = (GenericInputSplit)inputSplit;

        client = EsUtil.getClient(address, username, password, clientConfig);
        scroll = new Scroll(TimeValue.timeValueMinutes(keepAlive));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(batchSize);

        if(StringUtils.isNotEmpty(query)){
            searchSourceBuilder.query(QueryBuilders.wrapperQuery(query));
        }

        if(genericInputSplit.getTotalNumberOfSplits() > 1){
            searchSourceBuilder.slice(new SliceBuilder(genericInputSplit.getSplitNumber(), genericInputSplit.getTotalNumberOfSplits()));
        }

        searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.scroll(scroll);
        searchRequest.source(searchSourceBuilder);
    }

    @Override
    public InputSplit[] doCreateInputSplits(int splitNum) throws IOException {
        InputSplit[] splits = new InputSplit[splitNum];
        for (int i = 0; i < splitNum; i++) {
            splits[i] = new GenericInputSplit(i,splitNum);
        }

        return splits;
    }

    @Override
    public boolean reachedEnd() throws IOException {
        if(iterator != null && iterator.hasNext()) {
            return false;
        } else {
            return searchScroll();
        }
    }

    private boolean searchScroll() throws IOException{
        SearchHit[] searchHits;
        if(scrollId == null){
            SearchResponse searchResponse = client.search(searchRequest);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
        } else {
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            SearchResponse searchResponse = client.searchScroll(scrollRequest);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
        }

        List<Map<String, Object>> resultList = Lists.newArrayList();
        for(SearchHit searchHit : searchHits) {
            Map<String,Object> source = searchHit.getSourceAsMap();
            resultList.add(source);
        }

        iterator = resultList.iterator();
        return !iterator.hasNext();
    }

    @Override
    public Row doNextRecord(Row row) throws IOException {
        return EsUtil.jsonMapToRow(iterator.next(), columnNames, columnTypes, columnValues);
    }

    @Override
    public void doClose() throws IOException {
        if(client != null) {
            clearScroll();

            client.close();
            client = null;
        }
    }

    private void clearScroll() throws IOException{
        if(scrollId == null){
            return;
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest);
        boolean succeeded = clearScrollResponse.isSucceeded();
        LOG.info("Clear scroll response:{}", succeeded);
    }
}
