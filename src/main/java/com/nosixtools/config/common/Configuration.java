package com.nosixtools.config.common;

import java.io.File;

public interface Configuration {
	String getConfigContent(File file);
	File getFile();
	Integer getInterval();
}
