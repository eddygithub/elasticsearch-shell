package com.akin.elasticsearch.tools.shell;

import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.JLineShellComponent;

public class ElasticSearchShellApplication {
	private static JLineShellComponent shell;

	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();
		shell = bootstrap.getJLineShellComponent();
		shell.start();

		shell.promptLoop();
		shell.waitForComplete();
	}
}
