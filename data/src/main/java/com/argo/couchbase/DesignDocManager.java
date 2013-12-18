package com.argo.couchbase;

import com.argo.couchbase.exception.BucketException;
import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.ViewDesign;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * ddoc存放文件夹:
 * 
 * /WEB-INF/couchbase/{ddocName}/{viewName}.map.js
 * /WEB-INF/couchbase/{ddocName}/{viewName}.reduce.js
 * 
 * ddocName is {@link BucketEntity}'s Name
 * 
 * @author yaming_deng
 * 
 */
public class DesignDocManager implements InitializingBean {

	private static final String CB_DDOC = "couchbase";

	public static final Logger logger = LoggerFactory.getLogger(DesignDocManager.class);
	
	private static final String V_REDUCE = "reduce";
	private static final String V_MAP = "map";
	
	@Autowired
	private BucketManager bucketManager;
	
	@Autowired
	private CouchbaseTemplate couchbaseTemplate;
	
	/**
	 * 初始化所有DDOC. 
	 * @throws IOException
	 */
	public synchronized void syncCreateAll() throws IOException {
		URL url = ClassLoader.getSystemResource("web.xml");
		if (url != null) {
			String fileName = url.getFile().split("/WEB-INF/")[0];
			File folder = new File(fileName + CB_DDOC);
			File[] files = folder.listFiles();
			for (File item : files) {
				try {
					this.syncViews(item);
				} catch (BucketException e) {
					logger.error("Sync Create DDOC Error. path=" + item.getPath(), e);
				}
			}
		} else {
			throw new IOException("File not found with system classloader.");
		}
	}
	
	/**
	 * 列出现有的DDOCS.
	 * @return
	 * @throws IOException
	 */
	public synchronized List<String> listAll() throws IOException {
		URL url = ClassLoader.getSystemResource("web.xml");
		if (url != null) {
			String fileName = url.getFile().split("/WEB-INF/")[0];
			File folder = new File(fileName + CB_DDOC);
			File[] files = folder.listFiles();
			List<String> paths = new ArrayList<String>();
			for (File item : files) {
				paths.add(item.getAbsolutePath());
			}
			return paths;
		} else {
			throw new IOException("File not found with system classloader.");
		}
	}
	
	/**
	 * 
	 * 更新某个DDOC.
	 * @param path
	 * @throws IOException
	 */
	public synchronized void syncCreate(String path) throws IOException {
		try {
			File file = new File(path);
			this.syncViews(file);
		} catch (BucketException e) {
			logger.error("Sync Create DDOC Error. path=" + path, e);
		}
	}
	
	private void syncViews(File folder) throws IOException, BucketException{
		String ddocName = folder.getName();
		File[] files = folder.listFiles();
		Map<String, String> maps = new HashMap<String, String>();
		Map<String, String> reduces = new HashMap<String, String>();
		for (File file : files) {
			String[] temp = file.getName().split(".");
			if(!temp[2].equalsIgnoreCase("js")){
				continue;
			}
			String viewName = temp[0];
			String value = readFileAsString(file);
			if(V_MAP.equalsIgnoreCase(temp[1])){
				maps.put(viewName, value);
			}else if(V_REDUCE.equalsIgnoreCase(temp[1])){
				reduces.put(viewName, value);
			}
		}
		
		DesignDocument ddoc = new DesignDocument(ddocName);
		Iterator<String> itor = maps.keySet().iterator();
		while(itor.hasNext()){
			String viewName = itor.next();
			String map = ObjectUtils.toString(maps.get(viewName));
			String reduce = ObjectUtils.toString(reduces.get(viewName));
			ViewDesign vd = new ViewDesign(viewName, map, reduce);
			ddoc.getViews().add(vd);
		}
		
		String bucketName = this.bucketManager.getBucket(ddocName);
		this.couchbaseTemplate.syncDDocViews(bucketName, ddoc);
	}

	private String readFileAsString(File afile) throws IOException {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(afile));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
				
	}
}
