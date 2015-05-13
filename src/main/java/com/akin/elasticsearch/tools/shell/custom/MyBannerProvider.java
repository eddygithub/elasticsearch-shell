package com.akin.elasticsearch.tools.shell.custom;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyBannerProvider extends DefaultBannerProvider {

	@Override
	public String getWelcomeMessage() {
		return "Welcome to ES Shell";
	}
}
