package com.nosix.config.hotconfig;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.nosix.config.hotconfig.annotation.HConfig;
import com.nosix.config.hotconfig.util.PropertyUtil;
import com.xiaoleilu.hutool.util.ClassUtil;

public class HotConfig {
    private static  Map<String, HotConfigExecutor> map = new HashMap<String, HotConfigExecutor>();
	
	public static void init(String startPackage) throws Throwable {
		findAnnotation(startPackage);
		executorTask();
	}

	private static void executorTask() throws Throwable {
        Iterator<Entry<String,HotConfigExecutor>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Entry<String,HotConfigExecutor> entry =it.next();
            entry.getValue().run();
        }
    }

	private static void findAnnotation(String startPackage) throws Exception {
		Set<Class<?>> classes = ClassUtil.scanPackage(startPackage);
		Iterator<Class<?>> it = classes.iterator();
		while(it.hasNext()) {
			Class<?> clz = it.next();
			Field[] fields = clz.getDeclaredFields();
			for(Field f:fields) {
				f.setAccessible(true);
				if(f.isAnnotationPresent(HConfig.class)) {
					HConfig config = f.getAnnotation(HConfig.class);
					checkClassType(f.getType(), clz);
					final String filePath = config.source();
					Class<?> configClazz = config.target();
					if(map.get(filePath) == null) {
						checkFile(filePath, clz);
						HotConfigExecutor hotConfigExecutor = new HotConfigExecutor();
						hotConfigExecutor.setConfigFileName(filePath);
						hotConfigExecutor.addFieldList(f);
						hotConfigExecutor.setClazz(configClazz);
						hotConfigExecutor.setKeys(config.keys());
						hotConfigExecutor.setSes(Executors.newScheduledThreadPool(1,new ThreadFactory() {
                              private String masterThreadName = "HOTCONFIG";
                              public Thread newThread(Runnable r) {
                                  return new Thread(r, String.format(masterThreadName+" filepath:"+ filePath));
                              }
						}));
						map.put(filePath, hotConfigExecutor);
					} else {
						HotConfigExecutor hotConfigExecutor = map.get(filePath);
						hotConfigExecutor.addFieldList(f);
					}
				}
			}
		}
	}

	private static void checkFile(String filePath,Class<?> clz) throws Exception {
        if(null == filePath){
            throw new Exception("class :"+clz.getName()+", annotation Hconfig source property is null");
        }
        
       if(filePath.startsWith("$") && filePath.indexOf("{")>-1 && filePath.indexOf("}")>-1){
            String newPath =filePath.replace("$", "").replace("{", "").replace("}", "");
            if(null != newPath  &&  PropertyUtil.getContextProperty(newPath) != null){
                filePath = new String(PropertyUtil.getContextProperty(newPath).toString());
            }else{
                throw new Exception("cannot find "+newPath);
            }
        }
        
        if(filePath.indexOf(":") == -1){
            throw new Exception("file pattern is wrong,right pattern is like classpath:a.txt");
        }
        
        String [] file= filePath.split(":");
        if(filePath.startsWith("classpath:")) {
            URL url = HotConfigExecutor.class.getClassLoader().getResource(filePath.split("classpath:")[1]);  
            File f = new File(url.getFile());
            if(!f.exists()){
                throw new Exception("class:"+clz.getName()+", source file value "+file[1]+" is not found");
            }
        } else if(filePath.startsWith("filepath:")) {
            File f = new File(filePath.split("filepath:")[1]);
            if(!f.exists()){
                throw new Exception("class:"+clz.getName()+", source file value"+file[1]+" is not found");
            }
        }
    }

	private static Object checkClassType(Class<?> type, Class<?> clz) throws Exception {
		if(null == clz) {
			throw new Exception("class is con not be null");
		}
		String clazz = type.getSimpleName();
		
		if(clazz.startsWith("Map") || clazz.startsWith("HashMap")){
            return new HashMap<Object,Object>();
        } else if(clazz.startsWith("HashTable")) {
            return new Hashtable<Object,Object>();
        } else if(clazz.startsWith("List") || clazz.startsWith("ArrayList") || clazz.startsWith("LinkedList") || clazz.startsWith("Vector")) {
            return new ArrayList<Object>();
        }
        //other type throw exception
        throw new Exception("hotconfig class type can only be Map Or List subClass,like HashMap,HashTable,current type is "+clazz+",Exception class:"+ clz.getName());
    }
}
