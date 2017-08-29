package com.nosixtools.config.common;



import com.nosixtools.config.common.properties.DefaultPropertiesHandler;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class PropertiesCommonConfigTest {


	public static void main(String[] args) throws InterruptedException {
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").toString().replace("file:","") + "/config/test.properties";
		
		File file = new File(filePath);
		DefaultPropertiesHandler config = new DefaultPropertiesHandler(file);
		
		Map<String,String> settings = config.getPropertiesMap();
		while(true) {
			Iterator<Entry<String, String>> it = settings.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, String> temp = it.next();
				System.out.println(temp.getValue());
			}
			System.out.println("-----------------------------");
			TimeUnit.SECONDS.sleep(1);
		}
	}
}
