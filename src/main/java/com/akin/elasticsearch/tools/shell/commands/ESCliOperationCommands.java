package com.akin.elasticsearch.tools.shell.commands;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ESCliOperationCommands implements CommandMarker {
	
	private static final String COMMAND_CONNECT_NODE = "connect node";
	private static final String COMMAND_INDEX_API = "index";
	private static final String COMMAND_DELETE_API = "delete";
	private boolean connected = false;
	
	private TransportClient client;

	@CliAvailabilityIndicator({COMMAND_CONNECT_NODE})
	public boolean isSingleCommand(){
		return true;
	}
	
	@CliAvailabilityIndicator(value={COMMAND_INDEX_API, COMMAND_DELETE_API})
	public boolean isComplexCommand(){
		boolean canExcute = false;
		if(connected){
			canExcute = true;
		}
		return canExcute;
	}
	
	@CliCommand(value={COMMAND_INDEX_API})
	public void index(@CliOption(key="index") String index, @CliOption(key="type") String type){
		IndexRequestBuilder indexRequestBuilder = client.prepareIndex().setIndex(index).setType(type);
		indexRequestBuilder.execute().actionGet();
	}

	@CliCommand(value={COMMAND_DELETE_API})
	public void delete(@CliOption(key="index-name") String indexName, @CliOption(key="type-name") String typeName, @CliOption(key="id") String id){
		client.prepareDelete(indexName, typeName, id).execute().actionGet();
	}
	
	@CliCommand(value={COMMAND_CONNECT_NODE})
	public String connect(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9300") int port){
		String resultString=null;
		try{
			Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).build();
			client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(hostName, port));
			connected = true;
			resultString = String.format("You are connect to the elasticsearch cluster at host:%s, port:%d", hostName, port);
		}
		catch(Exception cex){
			resultString = String.format("Failed! to connect to the elasticsearch cluster at host:%s, port:%d", hostName, port);
			cex.printStackTrace();
		}
		
		return resultString;
	}
}
