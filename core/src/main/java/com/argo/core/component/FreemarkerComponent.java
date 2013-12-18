package com.argo.core.component;

import com.argo.core.base.BaseBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author yaming_deng
 *
 */
@Component("freemarkerComponent")
public class FreemarkerComponent extends BaseBean {
	
	private static final String TEMPLATE_FOLDER = "template_folder";

	private Configuration configuration ;
	
	private String contextPath;
	
	private final Logger logger = LoggerFactory.getLogger(FreemarkerComponent.class);
	
	@Autowired
	@Qualifier("freemarkerConfiguration")
	private Properties properties;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		this.buildRenderConfig();		
	}
	
	//初始化生成模板配置对象
	private void buildRenderConfig() throws TemplateException, IOException{
		configuration = new Configuration();
		Iterator<Object> itor = this.properties.keySet().iterator();
		while(itor.hasNext()){
			String key = itor.next().toString();
			if(key.equalsIgnoreCase(TEMPLATE_FOLDER)){
				continue;
			}
			configuration.setSetting(key, this.properties.getProperty(key));
		}
		contextPath = FreemarkerComponent.class.getResource("/").getPath().split("WEB-INF")[0];
		configuration.setDirectoryForTemplateLoading(new File(contextPath));
	}
	
	public Map<String, Object> getGlobalParams(){
		Map<String, Object> map = new HashMap<String, Object>();
		//配置
		map.put("siteConfig", getSiteConfig());
		return map;
	}
	
	/**
	 * @param filePath 相对templateFolder的路径.
	 * @return
	 * @throws Exception
	 */
	public Template parseTemplateFromFile(String filePath) throws Exception{
		Template template;
		try{
			//根据模板路径获取模板对象
			template = configuration.getTemplate(filePath);
			return template;
		}catch (IOException e) {
			logger.error("==============load FTL template error:contextPath="+this.contextPath+",filePath="+filePath, e);
			throw new Exception("系统IO异常！contextPath="+this.contextPath+",filePath="+filePath,e);
		}
	}
	
	public String render(String templateFile,Map<String,Object> params) throws Exception{
		Template template;
		try {
			template = this.parseTemplateFromFile(templateFile);
		} catch (Exception e1) {
			logger.warn("页面模板不存在: templateFile="+templateFile);
			return null;
		}
		try{
			Map<String, Object> map = this.getGlobalParams();
			//加载插件类获取的数据
			if(params!=null){
				map.putAll(params);
			}
			StringWriter sw = new StringWriter();
			template.process(map, sw);
			StringBuffer sb = sw.getBuffer();
			return sb.toString();
			
		}catch (Exception e) {
			throw new Exception("输出页面出错！templateFile="+templateFile,e);
		}
	}
}
