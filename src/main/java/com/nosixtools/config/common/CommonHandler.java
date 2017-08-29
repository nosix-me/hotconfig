package com.nosixtools.config.common;

import java.io.File;

public class CommonHandler {
	
	protected File file;
	protected Integer interval = 3; //默认3s检查一次
	
	public CommonHandler(File file) {
		super();
		this.file = file;
	}

	public CommonHandler(File file, Integer interval) {
		super();
		this.file = file;
		this.interval = interval;
	}
}
