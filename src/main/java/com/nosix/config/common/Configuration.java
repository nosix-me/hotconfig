package com.nosix.config.common;

import java.io.File;
import java.util.Map;

public interface Configuration {
	Map<String,String> getConfig(File file);
}
