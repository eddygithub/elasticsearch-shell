package com.akin.elasticsearch.tools.shell.commands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.akin.elasticsearch.tools.shell.ESUtil;

@Component
public class ESCliAdminCommands implements CommandMarker {

	private static final String ADMIN_COMMAND_LIST_INDEXES = "list indexes";
	private static final String ADMIN_COMMAND_START_NODE = "start node";
	private static final String ADMIN_COMMAND_START_MARVEL = "start marvel";
	private static final String ADMIN_COMMAND_START_KIBANA = "start kibana";
	private static final String ADMIN_COMMAND_STOP_ALL_LOCAL_NODES = "stop local nodes";
	private static final String ADMIN_COMMAND_STOP_ALL_NODES = "stop all nodes";
	private static final String ADMIN_COMMAND_STOP_NODES = "stop nodes";
	private static final String ADMIN_COMMAND_LIST_NODE = "list node";
	private static final String ADMIN_COMMAND_PRINT_CLUSTER_INFO = "print cluster";
	private static final String PATH_HOME_OPTION = "path.home";
	static final String CLUSTER_NAME_OPTION = "cluster.name";
	static final String NODE_NAME_OPTION = "node.name";
	
	private static final String HELP_ADMIN_COMMAND_START_NODE = "This startup a node within elasticsearch cluster";
	
	Logger log = LoggerFactory.getLogger(ESCliAdminCommands.class);
	
	@CliCommand(value={ADMIN_COMMAND_START_KIBANA})
	public void startKibana()throws Exception{
		Process p = Runtime.getRuntime().exec("kibana");
		p.waitFor(2, TimeUnit.SECONDS);
		
		Runtime.getRuntime().exec("open http://localhost:5601/");
	}
	
	@CliCommand(value={ADMIN_COMMAND_START_MARVEL})
	public void startMarvel(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port) throws Exception{
		Runtime.getRuntime().exec(String.format("open http://%s:%s/_plugin/marvel/", hostName, port));
	}
	
	@CliCommand(value={ADMIN_COMMAND_LIST_INDEXES})
	public String listIndexes( @CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		String url = String.format("http://%s:%d/_cat/indices?v", hostName, port);
		return ESUtil.restTemplate.get().getForEntity(url, String.class).getBody();
	}
	
	@CliCommand(help=HELP_ADMIN_COMMAND_START_NODE, value={ADMIN_COMMAND_START_NODE})
	public String startUpNode(@CliOption(key="cluster-name") String clusterName, @CliOption(key="node-name") String nodeName,
			@CliOption(key="path-home") String homePath) throws Exception{
		StringBuilder commandLine = new StringBuilder("elasticsearch");
		
		if(!StringUtils.isEmpty(clusterName)){
			commandLine.append(" -Des.").append(CLUSTER_NAME_OPTION).append("=").append(clusterName);
		}
		if(!StringUtils.isEmpty(nodeName)){
			commandLine.append(" -Des.").append(NODE_NAME_OPTION).append("=").append(nodeName);
		}
		if(!StringUtils.isEmpty(homePath)){
			commandLine.append(" -Des.").append(PATH_HOME_OPTION).append("=").append(homePath);
		}

		log.info(commandLine.toString());
		Runtime.getRuntime().exec(commandLine.toString());
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
	public void stopLocalNodes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port, 
			@CliOption(key="delay", unspecifiedDefaultValue="1") int delay){
		String url = String.format("http://%s:%d/_cluster/nodes/_local/_shutdown?delay=%ds", hostName, port, delay);
		 ESUtil.restTemplate.get().postForEntity(url, null, String.class);
	}
	
	@CliCommand(value={ADMIN_COMMAND_STOP_ALL_NODES, "quit", "exit"})
	public ExitShellRequest stopAllNodes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		try{
			String url = String.format("http://%s:%d/_cluster/nodes/_all/_shutdown", hostName, port);
			CompletableFuture.supplyAsync(() ->  ESUtil.restTemplate.get().postForEntity(url, null, String.class));
		}
		catch(Exception ex){
		}
		return ExitShellRequest.NORMAL_EXIT;
	}
	
	@CliCommand(value={ADMIN_COMMAND_STOP_NODES})
	public void stopNodes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port,  
			@CliOption(key="nodes", mandatory=true) String nodes, @CliOption(key="delay", unspecifiedDefaultValue="1") int delay){
		String url = String.format("http://%s:%d/_cluster/nodes/%s/_shutdown?delay=%ds", hostName, port, nodes, delay);
		 ESUtil.restTemplate.get().postForEntity(url, null, String.class);
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
		return  ESUtil.restTemplate.get().getForEntity(url, String.class).getBody();
	}
}
