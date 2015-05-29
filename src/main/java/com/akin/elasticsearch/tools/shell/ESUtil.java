package com.akin.elasticsearch.tools.shell;

import org.springframework.web.client.RestTemplate;

public class ESUtil {

	public static ThreadLocal<RestTemplate> restTemplate = new ThreadLocal<RestTemplate>();
}
