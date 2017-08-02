package com.nosixtools.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.nosixtools.config.bean.Name;
import com.nosixtools.config.hotconfig.HotConfig;
import com.nosixtools.config.hotconfig.annotation.HConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class HotConfigTest {


	@HConfig(source="${com.moji.name}")
	private static Map<String,String> testMap = new HashMap<String, String>();

	@HConfig(source="${com.moji.temp}")
	private static Map<String,String> tempMap = new HashMap<String, String>();


	@HConfig(source="${name.json}",keys={"name"},target=Name.class)
	private static Map<String,Name> nameMap = new HashMap<String,Name>();

	@HConfig(source="${name.json}",target=Name.class)
	private static List<Name> nameList = new ArrayList<Name>();

	public static void print() {
		for( Entry<String, String> it : testMap.entrySet()) {
			System.out.println("test:"+it.getKey()+"#"+it.getValue());
		}

		for( Entry<String, String> it : tempMap.entrySet()) {
			System.out.println("temp:"+it.getKey()+"#"+it.getValue());
		}

		for( Map.Entry<String, Name> it : nameMap.entrySet()) {
			System.out.println("nameMap:"+it.getKey()+"#"+it.getValue().getName());
		}

		for(Name name : nameList) {
			System.out.println("list:"+name.getName());
		}
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Throwable {
		String[] locations = new String[]{"classpath:application_ac.xml"};
		new ClassPathXmlApplicationContext(locations);
		HotConfig.init("com.nosixtools.config");
		while(true) {
			TimeUnit.SECONDS.sleep(3);
			HotConfigTest.print();
		}
	}
}
