package com.akin.elasticsearch.tools.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.JLineShellComponent;

@SpringBootApplication
public class ElasticSearchShellApplication {
	private static JLineShellComponent shell;

	public static void main(String[] args) {
		SpringApplication.run(ElasticSearchShellApplication.class, args);
		Bootstrap bootstrap = new Bootstrap();
		shell = bootstrap.getJLineShellComponent();
		shell.start();

		shell.promptLoop();
		shell.waitForComplete();
	}

}
