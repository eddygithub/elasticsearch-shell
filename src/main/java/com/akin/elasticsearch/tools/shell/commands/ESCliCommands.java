package com.akin.elasticsearch.tools.shell.commands;

import org.springframework.shell.core.CommandMarker;

public class ESCliCommands implements CommandMarker{

	//TODO add some conditions
//	@CliAvailabilityIndicator(value={"server-start"})
//	public boolean isCommandAvailable(){
//		return true;
//	}
	
	public static void main(String[] args) {
		ESCliAdminCommands adminCommands = new ESCliAdminCommands();
		adminCommands.startUpNode("es-cluster", "node1");
		
		System.out.println("node started");
	}
	
}
