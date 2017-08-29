package com.nosixtools.config.common.string;

import java.io.File;

import com.google.common.eventbus.Subscribe;
import com.nosixtools.config.common.CommonHandler;
import com.nosixtools.config.common.Configuration;
import com.xiaoleilu.hutool.io.FileUtil;

public abstract class StringHandler extends CommonHandler implements Configuration {

	public StringHandler(File file, Integer interval) {
		super(file, interval);
	}

	public StringHandler(File file) {
		super(file);
	}
	
	@Subscribe
	public void eventHanler(String fileContent) {
		doEventHandler(fileContent);
	}
	
	abstract public void doEventHandler(String content);

	@Override
	public String getConfigContent(File file) {
		return FileUtil.readUtf8String(file);
	}
	
	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public Integer getInterval() {
		return this.interval;
	}
	
}
