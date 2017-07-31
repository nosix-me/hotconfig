package em.nosix.tools.config;

import java.util.Map;
import java.util.Map.Entry;

import em.nosix.tools.config.common.BaseHandler;
import em.nosix.tools.config.common.PropertiesConfiguration;

import java.io.File;

public class CommonConfigTest extends BaseHandler {

	public static void main(String[] args) {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").toString().replace("file:","");
		File file = new File(rootPath+"/config/test.properties");
		new PropertiesConfiguration(file, new CommonConfigTest(), 10);
	}

	@Override
	public void doEventHandler(Map<String, String> settings) {
		for( Entry<String, String> it : settings.entrySet()) {
			System.out.println(it.getKey()+"#"+it.getValue());
		}
	}
}
