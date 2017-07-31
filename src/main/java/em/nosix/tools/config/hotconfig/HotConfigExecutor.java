package em.nosix.tools.config.hotconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import em.nosix.tools.config.hotconfig.util.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class HotConfigExecutor {
	private static Logger logger = LoggerFactory.getLogger(HotConfig.class);
	private String configFileName;
	private List<Field> fieldList = new ArrayList<Field>();
	private ScheduledExecutorService ses;
	private File file;
	private long lastModifyTime;
	private Class<?> clazz;
	private String []keys;
	public void run() {
		ses.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					loadConfigFile(configFileName);
				} catch (Throwable e) {
					logger.error("load configfile:{}, exception {}",configFileName, ExceptionUtils.getFullStackTrace(e));
				}
			}
		}, 1000l, 3000l, TimeUnit.MICROSECONDS);
	}
	
	public void loadConfigFile(String configFileName) throws Throwable {
		if(configFileName.startsWith("classpath:")) {
			setValueFromClassPath(configFileName);
		} else if(configFileName.startsWith("filepath:")) {
			setValueFromClassPath(configFileName);
		} else if(configFileName.startsWith("$") && configFileName.indexOf("{") > -1 && configFileName.indexOf("}") > -1) {
			String newPath = configFileName.replace("$", "").replace("{", "").replace("}", "");
            if (null != newPath && PropertyUtil.getContextProperty(newPath) != null) {
                if (PropertyUtil.getContextProperty(newPath).toString().startsWith("classpath:")) {
                    setValueFromClassPath(PropertyUtil.getContextProperty(newPath).toString());
                } else if (PropertyUtil.getContextProperty(newPath).toString().startsWith("filepath:")) {
                    setValueFromFilePath(PropertyUtil.getContextProperty(newPath).toString());
                } else {
                	throw new Exception("filePath:"+configFileName+" format error");
                }
            } else {
           	 	throw new Exception("filePath:"+configFileName+" format error");
            }
		}
	}

	private void setValueFromClassPath(String configFileName) throws Throwable {
		 Properties prop = new Properties();
		//classpath:**/*
		String filePath = configFileName.substring(configFileName.indexOf(":")+1, configFileName.length());
		URL url = HotConfigExecutor.class.getClassLoader().getResource(filePath);
		File f = getConfigFile(url.getFile());
		if (!isFileModify(f)) {
            return;
        }
		if(getClazz() == Boolean.class) {
			InputStream bf = ClassLoader.getSystemResourceAsStream(filePath);
	        prop.load(bf);
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Map<Object, Object> map = new HashMap<Object, Object>((Map) prop);
	        updateMemory(map);
		} else {
			generateConfigFromJson(f);
		}
	}
	
	private void setValueFromFilePath(String configFileName) throws Throwable {
		 Properties prop = new Properties();
		//filepath:**/*
		String filePath = configFileName.substring(configFileName.indexOf(":")+1, configFileName.length());
		File f = getConfigFile(filePath);
		if (!isFileModify(f)) {
           return;
       }
		if(getClazz() == Boolean.class) { 
			BufferedReader bf = new BufferedReader(new InputStreamReader((new FileInputStream(f))));
	        prop.load(bf);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Map<Object, Object> map = new HashMap<Object, Object>((Map) prop);
			updateMemory(map);
		} else {
			generateConfigFromJson(f);
		}
	}

	private void generateConfigFromJson(File f) throws Exception, IllegalAccessException {
		if(getKeys() != null && getKeys().length > 0) {
			Map<Object,Object> map = genrateMapFromJson(f);
			if(map == null || map.size() == 0) {
				logger.error("get empty config from file, do not update");
				return;
			}
			updateMemory(map);
			return ;
		} else {
			List<Object> list = generateListFromJson(f);
		    updateMemory(list);
		    return;
		}
	}

	private void updateMemory(Map<Object, Object> map)
            throws IllegalArgumentException, IllegalAccessException {
        Iterator<Field> it = fieldList.iterator();
        while (it.hasNext()) {
            Field f = it.next();
            f.set(map, map);
        }
    }
	
	private void updateMemory(List<?> list)
            throws IllegalArgumentException, IllegalAccessException {
        Iterator<Field> it = fieldList.iterator();
        while (it.hasNext()) {
            Field f = it.next();
            f.set(list, list);
        }
    }
	
	private File getConfigFile(String filePath) {
		if (getFile() == null) {
            File file = new File(filePath);
            setFile(file);
            return file;
        } else {
            return getFile();
        }
	}
	
	
	private List<Object> generateListFromJson(File f) throws Exception {
        List<Object> list = new ArrayList<Object>();
        String jsonText = FileUtils.readFileToString(f,"utf-8");
        JSONArray jsonArr = JSON.parseArray(jsonText);
        for (int i = 0; i < jsonArr.size(); i++) {
            Object obj = JSON.toJavaObject(jsonArr.getJSONObject(i), getClazz());
            list.add(obj);
        }
        return list;
    }
	
	private Map<Object, Object> genrateMapFromJson(File f) throws Exception {
		if(f == null) {
			return new HashMap<Object,Object>();
		}
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<String> listKey = Arrays.asList(getKeys());
		String jsonStr = FileUtils.readFileToString(file, "utf-8");
		JSONArray jsonArray = JSON.parseArray(jsonStr);
		if(jsonArray == null) {
			return new HashMap<Object,Object>();
		}
		for(int i = 0; i < jsonArray.size(); i++) {
			Object obj = JSON.toJavaObject(jsonArray.getJSONObject(i), getClazz());
			Field[] mapFields = obj.getClass().getDeclaredFields();
			StringBuilder stringBuilder = new StringBuilder();
			for(String key : listKey) {
				boolean flag = false;
				for (Field mapField : mapFields) {
                    if (mapField.getName().equals(key)) {
                        flag = true;
                        mapField.setAccessible(true);
                        stringBuilder.append(mapField.get(obj)).append("_");
                        continue;
                    }
                }
				if (!flag) {
                    logger.warn("field:" + key + ",not Found in" + getClazz().getName());
                }
			}
			String newKey = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
            map.put(newKey, obj);
		}
		return map;
	}

	
	
	
    private boolean isFileModify(File f) {
        long lastModify = f.lastModified();
        if (lastModify == getLastModifyTime()) {
            return false;
        }
        setLastModifyTime(f.lastModified());
        return true;
    }

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public void addFieldList(Field field) {
		this.fieldList.add(field);
	}

	public ScheduledExecutorService getSes() {
		return ses;
	}

	public void setSes(ScheduledExecutorService ses) {
		this.ses = ses;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getKeys() {
		return keys;
	}

}
