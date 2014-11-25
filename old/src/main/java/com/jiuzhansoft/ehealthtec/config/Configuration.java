package com.jiuzhansoft.ehealthtec.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
	public static final String ATTEMPTS = "attempts";
	public static final String ATTEMPTS_TIME = "attemptsTime";
	public static final String CONNECT_TIMEOUT = "connectTimeout";
	public static final String READ_TIMEOUT = "readTimeout";
	public static final String REQUEST_METHOD = "requestMethod";
	public static final String GET = "get";
	public static final String HOST = "host";
	public static final String MAINSERVER = "mainserver";
	public static final String LOCAL_FILE_CACHE = "localFileCache";
	public static final String LOCAL_MEMORY_CACHE = "localMemoryCache";
	public static final String INIT_POOL_SIZE = "initPoolSize";
	public static final String MAX_POOL_SIZE = "maxPoolSize";
	public static final String LEAVE_TIME_GAP = "leaveTimeGap";
	public static final String PRINT_LOG = "printLog";
	public static final String DEBUG_LOG = "debugLog";
	public static final String ERROR_LOG = "errorLog";
	public static final String INFO_LOG = "infoLog";
	public static final String VIEW_LOG = "viewLog";
	public static final String WARN_LOG = "warnLog";
	public static final String TEST_LOG = "testLog";
	private static Properties properties;
	private static Map<String, String> localProperties = new HashMap<String, String>();
    public static final long CHECK_UPDATE_INTERVAl = 24*60*60*1000;
	
	static{
		localProperties.put(PRINT_LOG, "true");
		localProperties.put(DEBUG_LOG, "true");
		localProperties.put(VIEW_LOG, "true");
		localProperties.put(ERROR_LOG, "true");
		localProperties.put(INFO_LOG, "true");
		localProperties.put(WARN_LOG, "false");
		localProperties.put(TEST_LOG, "false");
		
		//http
		//localProperties.put(HOST, "112.124.0.195:8080");
		localProperties.put(HOST, "182.254.137.149:8080");
		localProperties.put(MAINSERVER, "");
		localProperties.put(CONNECT_TIMEOUT, "20000");
		localProperties.put(READ_TIMEOUT, "20000");
		localProperties.put(ATTEMPTS, "2");
		localProperties.put(ATTEMPTS_TIME, "0");
		localProperties.put(REQUEST_METHOD, "get");
		localProperties.put(LOCAL_MEMORY_CACHE, "false");
		localProperties.put(LOCAL_FILE_CACHE, "false");
		
		localProperties.put(INIT_POOL_SIZE, "5");
		localProperties.put(MAX_POOL_SIZE, "5");
		localProperties.put(LEAVE_TIME_GAP, "3600000");
		
		
		InputStream is = Configuration.class.getClassLoader().getResourceAsStream("config.properties");
		if(is != null){
			properties = new Properties();
			try {
				properties.load(is);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getProperty(String key) {
		return getProperty(key, null);
	}
	public static String getProperty(String key, String defaultVal) {
		String value = null;
		if (properties != null)
			value = properties.getProperty(key);
		if (value == null)
			value = (String) localProperties.get(key);
		if (value == null)
			value = defaultVal;
		return value;
	}
	
	public static Boolean getBooleanProperty(String key) {
		return getBooleanProperty(key, null);
	}

	public static Boolean getBooleanProperty(String key, Boolean defaultVal) {
		String value = getProperty(key);
		Boolean ret = null;
		if (value == null) {
			ret = defaultVal;
		} else {
			try {
				ret = Boolean.valueOf(value);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return ret;
	}

	public static Integer getIntegerProperty(String key) {
		return getIntegerProperty(key, null);
	}

	public static Integer getIntegerProperty(String key, Integer defaultVal) {
		String value = getProperty(key);
		Integer ret = null;
		if (value == null) {
			ret = defaultVal;
		} else {
			try {
				ret = Integer.valueOf(value);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
