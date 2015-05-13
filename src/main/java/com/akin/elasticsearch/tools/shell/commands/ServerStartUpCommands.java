package com.akin.elasticsearch.tools.shell.commands;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.Properties;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ServerStartUpCommands implements CommandMarker{
	
	private static final String COMMAND_START_NODE = "start node";
	private static final String COMMAND_STOP_NODE = "stop node";
	private static final String COMMAND_LIST_NODE = "list node";
	private static final String HELP_COMMAND_START_NODE = "This startup a node within elasticsearch cluster";
	
	private static final String CLUSTER_NAME = "cluster.name";
	private static final String NODE_NAME = "node.name";
	//TODO add some conditions
//	@CliAvailabilityIndicator(value={"server-start"})
//	public boolean isCommandAvailable(){
//		return true;
//	}
	
	@CliCommand(help=HELP_COMMAND_START_NODE, value={COMMAND_START_NODE})
	public String startUpNode(@CliOption(key="cluster-name", unspecifiedDefaultValue="es-cluster") String clusterName, 
			@CliOption(key="node-name") String nodeName){
		Properties props = new Properties();
		props.put(CLUSTER_NAME, clusterName);
		props.put(NODE_NAME, nodeName);
		Settings settings = ImmutableSettings.settingsBuilder().put(props).build();
		Node node = nodeBuilder().settings(settings).node();
		String commandString = String.format("starting a new node: %s in cluster: %s", nodeName, clusterName);
		return commandString;
	}
	
	@CliCommand(value={COMMAND_STOP_NODE})
	public void stopNode(@CliOption(key="node-name") String nodeName){

	}
	
	@CliCommand(value={COMMAND_LIST_NODE})
	public void NodeInfo(){
		Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("host1", 9300))
        .addTransportAddress(new InetSocketTransportAddress("host2", 9300));

	}
}
