package com.akin.elasticsearch.tools.shell.commands;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class ESCliAdminCommands implements CommandMarker {

	private static final String ADMIN_COMMAND_LIST_INDEXES = "list indexes";
	private static final String ADMIN_COMMAND_START_NODE = "start node";
	private static final String ADMIN_COMMAND_STOP_ALL_LOCAL_NODES = "stop local nodes";
	private static final String ADMIN_COMMAND_STOP_ALL_NODES = "stop all nodes";
	private static final String ADMIN_COMMAND_STOP_NODES = "stop nodes";
	private static final String ADMIN_COMMAND_LIST_NODE = "list node";
	private static final String ADMIN_COMMAND_PRINT_CLUSTER_INFO = "print cluster";
	private static final String ADMIN_SETTING_PATH_HOME = "path.home";
	static final String CLUSTER_NAME_OPTION = "cluster.name";
	static final String NODE_NAME_OPTION = "node.name";
	
	private static final String HELP_ADMIN_COMMAND_START_NODE = "This startup a node within elasticsearch cluster";
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@CliCommand(value={ADMIN_COMMAND_LIST_INDEXES})
	public String listIndexes( @CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		String url = String.format("http://%s:%d/_cat/indices?v", hostName, port);
		return restTemplate.getForEntity(url, String.class).getBody();
	}
	
	@CliCommand(help=HELP_ADMIN_COMMAND_START_NODE, value={ADMIN_COMMAND_START_NODE})
	public String startUpNode(@CliOption(key="cluster-name") String clusterName, @CliOption(key="node-name") String nodeName,
			@CliOption(key="path-home") String homePath){
		Properties props = new Properties();
		if(!StringUtils.isEmpty(clusterName)){
			props.put(CLUSTER_NAME_OPTION, clusterName);
		}
		if(!StringUtils.isEmpty(nodeName)){
			props.put(NODE_NAME_OPTION, nodeName);
		}
		if(!StringUtils.isEmpty(homePath)){
			props.put(ADMIN_SETTING_PATH_HOME, homePath);
		}
		Settings settings = ImmutableSettings.settingsBuilder().put(props).build();
		Node node = nodeBuilder().settings(settings).node();
		String commandString = String.format("starting a new node: %s in cluster: %s", node.settings().get(NODE_NAME_OPTION), node.settings().get(CLUSTER_NAME_OPTION));
		return commandString;
	}
	
	/**
	 * only works when your cluster setting doesn't have action.disable_shutdown 
	 * @param hostName
	 * @param port
	 * @param delay
	 */
	@CliCommand(value={ADMIN_COMMAND_STOP_ALL_LOCAL_NODES})
	public void stopLocalNodes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port, 
			@CliOption(key="delay", unspecifiedDefaultValue="1") int delay){
		String url = String.format("http://%s:%d/_cluster/nodes/_local/_shutdown?delay=%ds", hostName, port, delay);
		restTemplate.postForEntity(url, null, String.class);
	}
	
	@CliCommand(value={ADMIN_COMMAND_STOP_ALL_NODES, "quit", "exit"})
	public ExitShellRequest stopAllNodes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		try{
			String url = String.format("http://%s:%d/_cluster/nodes/_all/_shutdown", hostName, port);
			CompletableFuture.supplyAsync(() -> restTemplate.postForEntity(url, null, String.class));
		}
		catch(Exception ex){
		}
		return ExitShellRequest.NORMAL_EXIT;
	}
	
	@CliCommand(value={ADMIN_COMMAND_STOP_NODES})
	public void stopNodes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port,  
			@CliOption(key="nodes", mandatory=true) String nodes, @CliOption(key="delay", unspecifiedDefaultValue="1") int delay){
		String url = String.format("http://%s:%d/_cluster/nodes/%s/_shutdown?delay=%ds", hostName, port, nodes, delay);
		restTemplate.postForEntity(url, null, String.class);
	}
	
	@CliCommand(value={ADMIN_COMMAND_PRINT_CLUSTER_INFO})
	public void printClusterInfo(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("http://%s:%d/_cluster/health?pretty=true", hostName, port);
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		System.out.println(response.getBody());
	}
	
	@CliCommand(value={ADMIN_COMMAND_LIST_NODE})
	public String NodeInfo(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		String url = String.format("http://%s:%d/_nodes/stats/process?pretty", hostName, port);
		return restTemplate.getForEntity(url, String.class).getBody();
	}
}
