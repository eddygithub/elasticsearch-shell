package com.akin.elasticsearch.tools.shell.commands;

import java.util.Properties;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ESCliOperationCommands implements CommandMarker {
	
	private static final String COMMAND_CONNECT_NODE = "connect node";
	private static final String COMMAND_INDEX_CREATE = "create index";
	private static final String COMMAND_DELETE_API = "delete index";
	
	private static final String COMMAND_RESULT_SUCCESS = "Sucess";
	private static final String COMMAND_RESULT_FAIL = "Fail";
	
	private boolean connected = false;
	
	private TransportClient client;

	@CliAvailabilityIndicator({COMMAND_CONNECT_NODE})
	public boolean isSingleCommand(){
		return true;
	}
	
	@CliAvailabilityIndicator(value={COMMAND_INDEX_CREATE, COMMAND_DELETE_API})
	public boolean isComplexCommand(){
		boolean canExcute = false;
		if(connected){
			canExcute = true;
		}
		return canExcute;
	}
	
	@CliCommand(value={COMMAND_INDEX_CREATE})
	public String index(@CliOption(key="index", mandatory=true) String indexName){
		String resultStr = "%s! create index:%s";
		CreateIndexResponse createResponse = client.admin().indices().prepareCreate(indexName).execute().actionGet();
		if(createResponse.isAcknowledged()){
			resultStr = String.format(resultStr, COMMAND_RESULT_SUCCESS, indexName);
		}
		else{
			resultStr = String.format(resultStr, COMMAND_RESULT_FAIL, indexName);
		}
		return resultStr;
	}

	@CliCommand(value={COMMAND_DELETE_API})
	public String delete(@CliOption(key="index-name", mandatory=true) String indexName){
		String resultStr = "%s! delete index:%s";
		DeleteIndexResponse deleteResponse = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		if(deleteResponse.isAcknowledged()){
			resultStr = String.format(resultStr, COMMAND_RESULT_SUCCESS, indexName);
		}
		else{
			resultStr = String.format(resultStr, COMMAND_RESULT_FAIL, indexName);
		}
		return resultStr;
	}
	
	@SuppressWarnings("resource")
	@CliCommand(value={COMMAND_CONNECT_NODE})
	public String connect(@CliOption(key="cluster-name") String clusterName, @CliOption(key="node-name") String nodeName, @CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9300") int port){
		String resultString=null;
		try{
			Properties props = new Properties();
			if(!StringUtils.isEmpty(clusterName)){
				props.put(ESCliAdminCommands.CLUSTER_NAME_OPTION, clusterName);
			}
			if(!StringUtils.isEmpty(nodeName)){
				props.put(ESCliAdminCommands.NODE_NAME_OPTION, nodeName);
			}
			//props.put("client.transport.sniff", "true");
			Settings settings = ImmutableSettings.settingsBuilder().put(props).build();
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
	
	@Override
	protected void finalize() throws Throwable {
		client.close();
		super.finalize();
	}
}
