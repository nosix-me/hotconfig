package com.nosixtools.config.common;

import java.io.File;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

public class FileWatcher implements Runnable {
		
		private static final Logger logger = LoggerFactory.getLogger(FileWatcher.class);
		private final EventBus eventBus;
		private final Configuration configuration;
        private long lastChange;
        
        public FileWatcher(Configuration configuration, EventBus eventBus) {
            super();
            this.eventBus = eventBus;
            this.configuration = configuration;
            this.lastChange = 0L;
        }

        @Override
        public void run() {
            File file  = configuration.getFile();
        	logger.debug("Checking file:{} for changes", file);
            
            long lastModified = file.lastModified();

            if (lastModified > lastChange || lastModified == 0) {
                logger.info("Reloading configuration file:{}", file);
                lastChange = lastModified;
                try {
                    eventBus.post(configuration.getConfigContent(file));
                } catch (Exception e) {
                    logger.error("Failed to load configuration data. Exception follows {}",
                            ExceptionUtils.getFullStackTrace(e));
                } catch (NoClassDefFoundError e) {
                    logger.error("Failed to start agent because dependencies were not " +
                            "found in classpath. Error follows {}", ExceptionUtils.getFullStackTrace(e));
                } catch (Throwable t) {
                    logger.error("Unhandled error", ExceptionUtils.getFullStackTrace(t));
                }
            }
        }
    }