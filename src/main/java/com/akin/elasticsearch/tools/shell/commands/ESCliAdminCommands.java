package com.akin.elasticsearch.tools.shell.commands;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.Properties;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.web.client.RestTemplate;

public class ESCliAdminCommands implements CommandMarker {

	private static final String ADMIN_COMMAND_START_NODE = "start node";
	private static final String ADMIN_COMMAND_STOP_ALL_LOCAL_NODES = "stop local nodes";
	private static final String ADMIN_COMMAND_STOP_NODES = "stop nodes";
	private static final String ADMIN_COMMAND_LIST_NODE = "list node";
	private static final String ADMIN_COMMAND_PRINT_CLUSTER_INFO = "print cluster";
	
	private static final String CLUSTER_NAME_OPTION = "cluster.name";
	private static final String NODE_NAME_OPTION = "node.name";
	
	private static final String HELP_ADMIN_COMMAND_START_NODE = "This startup a node within elasticsearch cluster";
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@CliCommand(help=HELP_ADMIN_COMMAND_START_NODE, value={ADMIN_COMMAND_START_NODE})
	public String startUpNode(@CliOption(key="cluster-name", unspecifiedDefaultValue="es-cluster") String clusterName, 
			@CliOption(key="node-name") String nodeName){
		Properties props = new Properties();
		props.put(CLUSTER_NAME_OPTION, clusterName);
		props.put(NODE_NAME_OPTION, nodeName);
		Settings settings = ImmutableSettings.settingsBuilder().put(props).build();
		nodeBuilder().settings(settings).node();
		String commandString = String.format("starting a new node: %s in cluster: %s", nodeName, clusterName);
		return commandString;
	}
	
	
	/**
	 * only works when your cluster setting doesn't have action.disable_shutdown 
	 * @param hostName
	 * @param port
	 * @param delay
	 */
	@CliCommand(value={ADMIN_COMMAND_STOP_ALL_LOCAL_NODES})
	public void stopLocalNodes(@CliOption(key="host-name", specifiedDefaultValue="localhost") String hostName, @CliOption(key="port", specifiedDefaultValue="9300") int port, 
			@CliOption(key="nodes", specifiedDefaultValue="1") int delay){
		String url = String.format("http://%s:%d/_cluster/nodes/_local/_shutdown?delay=%ds", hostName, port, delay);
		restTemplate.getForEntity(url, String.class);
	}
	
	@CliCommand(value={ADMIN_COMMAND_STOP_NODES})
	public void stopNodes(@CliOption(key="host-name", specifiedDefaultValue="localhost") String hostName, @CliOption(key="port", specifiedDefaultValue="9300") int port,  
			@CliOption(key="nodes", mandatory=true) String nodes, @CliOption(key="nodes", specifiedDefaultValue="1") int delay){
		String url = String.format("http://%s:%d/_cluster/nodes/%s/_shutdown?delay=%ds", hostName, port, nodes, delay);
		restTemplate.getForEntity(url, String.class);
	}
	
	@CliCommand(value={ADMIN_COMMAND_PRINT_CLUSTER_INFO})
	public void printClusterInfo(){
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9200/_cluster/health?pretty=true", String.class);
		System.out.println(response.getBody());
	}
	
	
	@CliCommand(value={ADMIN_COMMAND_LIST_NODE})
	public void NodeInfo(@CliOption(key="host-name", specifiedDefaultValue="localhost") String hostName, @CliOption(key="port", specifiedDefaultValue="9300") int port){
		String url = "";
		restTemplate.getForEntity(url, String.class);
		
	}
}
