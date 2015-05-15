package com.akin.elasticsearch.tools.shell.commands;

import java.util.Optional;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;

public class ESCliOperationCommands implements CommandMarker {
	
	private static final String COMMAND_CONNECT_NODE = "connect node";
	private static final String COMMAND_CREATE_API = "create";
	private static final String COMMAND_DELETE_API = "delete";
	private static final String COMMAND_UPDATE_API = "update";
	private static final String COMMAND_BULK_API = "bulk";
	private static final String COMMAND_SEARCH_API = "search";
	
	private TransportClient client;
	
	@CliCommand(value={COMMAND_SEARCH_API})
	public void search(@CliOption(key="indices") String indices, @CliOption(key="types") String types, @CliOption(key="query") String queryString){
		SearchResponse response = client.prepareSearch(indices).setTypes(types)
		.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
		.setQuery(QueryBuilders.simpleQueryString(queryString)).execute().actionGet();
		//TODO extract result from the response
	}
	
	@CliCommand(value={COMMAND_CREATE_API})
	public void create(@CliOption(key="index-name") String indexName, @CliOption(key="type-name") String typeName, @CliOption(key="id") Optional<String> id, @CliOption(key="json-file") String jsonFile){
		if(id.isPresent()){
			client.prepareIndex(indexName, typeName, id.get()).setSource(jsonFile).execute().actionGet();
		}
		else{
			client.prepareIndex(indexName, typeName).setSource(jsonFile).execute().actionGet();
		}
	}
	
	@CliCommand(value={COMMAND_DELETE_API})
	public void delete(@CliOption(key="index-name") String indexName, @CliOption(key="type-name") String typeName, @CliOption(key="id") String id){
		client.prepareDelete(indexName, typeName, id).execute().actionGet();
	}
	
	@CliCommand(value={COMMAND_UPDATE_API})
	public void update(@CliOption(key="index-name") String indexName, @CliOption(key="type-name") String typeName, @CliOption(key="id") String id, @CliOption(key="value") String jsonFile) throws Exception{
		client.prepareUpdate(indexName, typeName, id).setSource(jsonFile.getBytes()).execute().actionGet();
	}
	
	@CliCommand(value={COMMAND_BULK_API})
	public void bulk(@CliOption(key="value") String jsonFile) throws Exception{
		BulkProcessor bulkProcessor = BulkProcessor.builder(client, 
				new BulkProcessor.Listener() {
					@Override
					public void beforeBulk(long executionId, BulkRequest request) {
						
					}
					
					@Override
					public void afterBulk(long executionId, BulkRequest request,
							Throwable failure) {
						
					}
					
					@Override
					public void afterBulk(long executionId, BulkRequest request,
							BulkResponse response) {
						
					}
				})
				.setBulkActions(1000)
				.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
				.setFlushInterval(TimeValue.timeValueSeconds(5))
				.setConcurrentRequests(1)
				.build();
		
		//TODO perform bulk actions
		bulkProcessor.wait(1000);
	}
	
	@CliCommand(value={COMMAND_CONNECT_NODE})
	public String connect(@CliOption(key="host-name", specifiedDefaultValue="localhost") String hostName, @CliOption(key="port", specifiedDefaultValue="9300") int port){
		InetSocketTransportAddress netAdr = new InetSocketTransportAddress(hostName, port);
		client = new TransportClient();
		client.addTransportAddress(netAdr);
		return String.format("You are connect to the elasticsearch cluster at host:%s, port:%d", hostName, port);
	}
}
