package com.nosixtools.config.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class PropertiesConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesConfiguration.class);
	private Object eventListener;
	private File file;
	private Integer interval = 10; //默认10s检查一次
	private ScheduledExecutorService executorService;

	public PropertiesConfiguration(File file, Object eventListener,Integer interval) {
		this.file = file;
		this.eventListener = eventListener; 
		this.interval = interval;
		init();
	}
	
	private void init() {
		EventBus eventBus = new EventBus();
		eventBus.register(eventListener);
		executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("conf-file-poller-%d").build());
        FileWatcherRunnable fileWatcherRunnable = new FileWatcherRunnable(this.file, eventBus, generateConfigurationParser());
        executorService.scheduleWithFixedDelay(fileWatcherRunnable, 0, interval, TimeUnit.SECONDS);
	}
	
	private Configuration generateConfigurationParser() {
		return new Configuration() {
			@Override
			public Map<String, String> getConfig(File file) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
					Properties properties = new Properties();
			        properties.load(reader);
			        @SuppressWarnings({ "rawtypes", "unchecked" })
					Map<String, String> result = new HashMap<String, String>((Map) properties);
			        return result;
				} catch (Exception e) {
					logger.error("load file:{} Exception:{}",file.getName(), ExceptionUtils.getFullStackTrace(e));
				} finally {
					if(reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							logger.warn("Unable to close file reader for file: {}, Exception:{}", file.getName(), ExceptionUtils.getFullStackTrace(e));
						}
					}
				}
				return null;
			}
		};
	}
}

	
