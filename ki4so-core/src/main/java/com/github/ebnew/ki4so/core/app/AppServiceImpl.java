package com.github.ebnew.ki4so.core.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.ebnew.ki4so.core.dao.fs.FileSystemDao;

/**
 * 默认的应用管理服务实现类，默认将应用信息存储在
 * json格式的数据文件中。
 * @author burgess yang
 *
 */
public class AppServiceImpl extends FileSystemDao implements AppService {
	
	private static Logger logger = Logger.getLogger(AppServiceImpl.class.getName());
	
	/**
	 * 外部数据文件地址，优先级更高。
	 */
	public static final String  DEFAULT_EXTERNAL_DATA =  "E:\\workspace\\ki4so\\ki4so-core\\target\\classes\\apps.js";
	
	/**
	 * 默认的数据文件地址，在classpath下。
	 */
	public static final String DEFAULT_CLASSPATH_DATA = "classpath:apps.js";
	
	/**
	 * 应用的映射表，key是appId，value是应用对象信息。
	 */
	private Map<String, App> appMap = null;
	
	/**
	 * ki4so服务器本身的应用配置信息。
	 */
	private App ki4soServerApp = null;
	
	
	/**
	 * 构造方法。
	 */
	public AppServiceImpl(){
		this.externalData = DEFAULT_EXTERNAL_DATA;
		this.classPathData = DEFAULT_CLASSPATH_DATA;
		//加载数据。
		loadAppData();
	}
	
	private void loadAppData(){
		try{
			String s = this.readDataFromFile();
			//将读取的应用列表转换为应用map。
			List<App> apps = JSON.parseObject(s, new TypeReference<List<App>>(){});
			appMap = new HashMap<String, App>(apps.size());
			for(App app:apps){
				appMap.put(app.getAppId(), app);
				//设置ki4so应用服务器。
				if(ki4soServerApp==null){
					if(app.isKi4soServer()){
						this.ki4soServerApp = app;
					}
				}
			}
			apps = null;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "load app data file error.", e);
		}
	}
	

	@Override
	public App findAppById(String appId) {
		if(appMap!=null){
			return appMap.get(appId);
		}
		return null;
	}
	
	public static void main(String[] args) {
		AppServiceImpl appServiceImpl = new AppServiceImpl();
		System.out.println(appServiceImpl.findAppById("1001"));
	}

	@Override
	public App findKi4soServerApp() {
		return this.ki4soServerApp;
	}

	@Override
	public App findAppByHost(String host) {
		if(StringUtils.isEmpty(host)){
			return null;
		}
		Collection<App> apps = appMap.values();
		for(App app: apps){
			if(host.startsWith(app.getHost())){
				return app;
			}
		}
		return null;
	}

}
