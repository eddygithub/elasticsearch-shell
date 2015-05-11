package com.akin.elasticsearch.tools.shell.custom;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyPromptProvider extends DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "es-shell>";
	}

	
	@Override
	public String getProviderName() {
		return "ES Shell prompt provider";
	}

}
