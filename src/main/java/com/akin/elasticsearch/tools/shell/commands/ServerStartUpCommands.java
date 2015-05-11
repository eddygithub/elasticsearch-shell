package com.akin.elasticsearch.tools.shell.commands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ServerStartUpCommands implements CommandMarker{
	
	private static final String COMMAND_START_NODE = "start node";
	private static final String HELP_COMMAND_START_NODE = "This startup a node within elasticsearch cluster";
	
	//TODO add some conditions
//	@CliAvailabilityIndicator(value={"server-start"})
//	public boolean isCommandAvailable(){
//		return true;
//	}
	
	@CliCommand(help=HELP_COMMAND_START_NODE, value={COMMAND_START_NODE})
	public String startUpNode(@CliOption(key="cluster-name", unspecifiedDefaultValue="es-cluster") String clusterName, 
			@CliOption(key="node-name") String nodeName){
		String commandString = String.format("starting a new node: %s in cluster: %s", nodeName, clusterName);
		System.out.println(commandString);
		return commandString;
	}
}
