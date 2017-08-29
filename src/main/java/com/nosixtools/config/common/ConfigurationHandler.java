package com.nosixtools.config.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ConfigurationHandler {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationHandler.class);
	private ScheduledExecutorService executorService;
	private Configuration configuration;
	
	public ConfigurationHandler(Configuration configuration) {
		this.configuration = configuration;
		init();
	}
	
	public static ConfigurationHandler monitor(Configuration configuration) {
		return new ConfigurationHandler(configuration);
	}
	
	private void init() {
		EventBus eventBus = new EventBus();
		eventBus.register(configuration);
		executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("conf-file-poller-%d").build());
        FileWatcher fileWatcher = new FileWatcher(configuration, eventBus);
        executorService.scheduleWithFixedDelay(fileWatcher, 0, configuration.getInterval(), TimeUnit.SECONDS);
        logger.info("started hotconfig");
        // sleep 1s
 		try {
 			TimeUnit.SECONDS.sleep(1);
 		} catch (InterruptedException e) {}
	}
}

	
