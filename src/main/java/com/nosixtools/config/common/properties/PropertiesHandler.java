package com.nosixtools.config.common.properties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.eventbus.Subscribe;
import com.nosixtools.config.common.CommonHandler;
import com.nosixtools.config.common.Configuration;
import com.xiaoleilu.hutool.io.FileUtil;

public abstract class PropertiesHandler extends CommonHandler implements Configuration {
	
	public PropertiesHandler(File file) {
		super(file);
	}

	public PropertiesHandler(File file, Integer interval) {
		super(file, interval);
	}

	@Override
	public String getConfigContent(File file) {
		return FileUtil.readUtf8String(file);
	}

	@Subscribe
	public void eventHanler(String fileContent) {
		try {
			InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
			Properties properties = new Properties();
			properties.load(inputStream);
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Map<String, String> result = new HashMap<String, String>((Map) properties);
			doEventHandler(result);
		} catch (IOException e) {
			doEventHandler(new HashMap<String,String>());
		}
	}
	
	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public Integer getInterval() {
		return this.interval;
	}

	abstract public void doEventHandler(Map<String,String> settings);
}
