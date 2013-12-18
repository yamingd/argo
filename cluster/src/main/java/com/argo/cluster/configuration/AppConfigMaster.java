package com.argo.cluster.configuration;

import com.argo.cluster.ZKClientBeanBase;
import com.argo.core.ContextConfig;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStreamReader;
import java.util.*;

/**
 * <pre>
 *  /dev/*.yaml
 * </pre>
 * 
 * @author yamingd
 * 
 */
public class AppConfigMaster extends ZKClientBeanBase {
	
	public static final String FOLDER_CONFIGS = "/configs";
	public static AppConfigMaster instance = null;

	private String classPathRoot = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
		this.setRootPath(this.getRootPath()+"/"+ContextConfig.getRunning()+FOLDER_CONFIGS);
        AppConfigMaster.instance = this;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected void onConnected() {
		this.classPathRoot = ContextConfig.getRunning();
		Map<String, Map> configs = loadAllYamlConfig();
		if(configs==null){
			System.exit(1);
		}
		try {
			this.syncConfigToZK(configs);
		} catch (KeeperException e) {
			this.logger.error("syncConfigToZK Error.", e);
		} catch (InterruptedException e) {
			this.logger.error("syncConfigToZK Error.", e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void syncConfigToZK(Map<String, Map> configs) throws KeeperException, InterruptedException{
		this.zkServer.createFolderNodes(getRootPath(), CreateMode.PERSISTENT);
		Set<String> files = configs.keySet();
		for (String file : files) {
			Map config = configs.get(file); // /dev/sample(.yaml)
			this.syncMapToZK(this.getRootPath() + "/" + file, config);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void syncMapToZK(String path, Map config) throws KeeperException, InterruptedException{
		this.zkServer.createFolderNodes(path, CreateMode.PERSISTENT);
		Iterator itor = config.keySet().iterator();
		while(itor.hasNext()){
			String key = (String)itor.next();
			Object value = config.get(key);
            if (this.logger.isDebugEnabled()){
                this.logger.debug("syncMapToZK, file:{}", key);
            }
			if(value instanceof List){
				this.zkServer.createDataNode(path + "/" + key, value, CreateMode.PERSISTENT);
			}else if(value instanceof Map){
				this.zkServer.createDataNode(path + "/" + key, value, CreateMode.PERSISTENT);
			}else{
				this.zkServer.createDataNode(path + "/" + key, value, CreateMode.PERSISTENT);
			}
		}
		this.logger.info("syncMapToZK OK, path={}", path);
	}
	
	/**
	 * 加载所有.yaml配置文件.
	 * 若想排除某个文件, 修改后缀名即可.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String, Map> loadAllYamlConfig() {
		final Yaml yaml = new Yaml();
		// Let's scan config files
		PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
		Resource resourceRoot = pathResolver.getResource(classPathRoot);
		try {
			Resource[] resources = pathResolver.getResources("classpath:" + classPathRoot + "/**/*.yaml");
			Map<String, Map> configs = new HashMap<String, Map>();
			for (int i = 0; i < resources.length; i++) {
				String relPath = resources[i].getURI().toString()
						.substring(resourceRoot.getURI().toString().length());
				if(relPath.endsWith("zookeeper.yaml") || relPath.indexOf("disabled") >= 0){
					continue;
				}
				Map config = this.parseConfigFile(yaml, this.classPathRoot + "/" + relPath);
				if(config!=null){
					relPath = relPath.replace(".yaml", "");
                    if (relPath.startsWith("/")){
                        relPath = relPath.substring(1);
                    }
					configs.put(relPath, config);
				}
			}
			return configs;
		} catch (Exception ex) {
			this.logger.error("loadAllYamlConfig Error.", ex);
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Map parseConfigFile(Yaml yaml, String url) throws Exception {
        try {
            ClassPathResource classPathResource = new ClassPathResource(url);
            InputStreamReader ipsr = new InputStreamReader(classPathResource.getInputStream());
            return (Map)yaml.load(ipsr);
        } catch (Exception e) {
        	this.logger.error("parseConfigFile Error. "+url, e);
            return null;
        }
    }

	@Override
	protected void onNodeChanged(WatchedEvent event) {
		this.logger.info("onNodeChanged. {}", event);		
	}
	
	public void setNodeData(String path, Object data) throws KeeperException, Exception{
		this.zkServer.createDataNode(path, data, CreateMode.PERSISTENT);
	}
	
	public List<String> getPaths() throws KeeperException, Exception{
		return this.zkServer.getZK().getChildren(getRootPath(), false);
	}
}
