package com.nosixtools.config.common.properties;

import com.nosixtools.config.common.ConfigurationHandler;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultPropertiesHandler extends PropertiesHandler {

	private Map<String,String> propertiesMap = new ConcurrentHashMap<String, String>();

	public DefaultPropertiesHandler(File file) {
		super(file);
		ConfigurationHandler.monitor(this);
	}

	public DefaultPropertiesHandler(File file, Integer interval) {
		super(file, interval);
	}

	@Override
	public void doEventHandler(Map<String, String> settings) {
		propertiesMap.putAll(settings);
	}

	public Map<String, String> getPropertiesMap() {
		return propertiesMap;
	}
	
}
