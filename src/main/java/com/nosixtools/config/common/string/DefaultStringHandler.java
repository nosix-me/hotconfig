package com.nosixtools.config.common.string;

import com.nosixtools.config.common.ConfigurationHandler;

import java.io.File;


public class DefaultStringHandler extends StringHandler {

	private String content = null;
	
	public DefaultStringHandler(File file) {
		super(file);
		ConfigurationHandler.monitor(this);
	}
	
	public DefaultStringHandler(File file, Integer interval) {
		super(file, interval);
	}

	@Override
	public void doEventHandler(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
