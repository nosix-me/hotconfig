package com.nosixtools.config.common;



import com.nosixtools.config.common.string.DefaultStringHandler;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class StringCommonConfigTest {

	public static void main(String[] args) throws InterruptedException {
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").toString().replace("file:","") + "/config/test.properties";
		
		File file = new File(filePath);
		DefaultStringHandler config = new DefaultStringHandler(file);
		
		while(true) {
			System.out.println(config.getContent());
			System.out.println("-----------------------------");
			TimeUnit.SECONDS.sleep(1);
		}
	}
}
