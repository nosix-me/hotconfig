package me.nosix.tools.config.common;

import java.util.Map;

import com.google.common.eventbus.Subscribe;

public abstract class BaseHandler {

	@Subscribe
	public void eventHanler(Map<String,String> settings) {
		doEventHandler(settings);
	}
	
	abstract public void doEventHandler(Map<String,String> settings);
}
