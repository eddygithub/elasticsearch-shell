package com.akin.elasticsearch.tools.shell.commands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliOption;

import com.akin.elasticsearch.tools.shell.ESUtil;

public class ESCliSearchCommands implements CommandMarker {

	public String emptySearch(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port){
		String url = String.format("http://%s:%d/_search", hostName, port);
		return  ESUtil.restTemplate.get().getForEntity(url, String.class).getBody();
	}
	
	public String searchIndexes(@CliOption(key="host-name", unspecifiedDefaultValue="localhost") String hostName, @CliOption(key="port", unspecifiedDefaultValue="9200") int port,
			@CliOption(key="indexes", unspecifiedDefaultValue="all") String indexes, @CliOption(key="types", mandatory=true) String types){
		String url = String.format("http://%s:%d/%s/%s/_search", hostName, port, indexes, types);
		return  ESUtil.restTemplate.get().getForEntity(url, String.class).getBody();
	}
}
