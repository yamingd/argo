package com.argo.couchbase;

import com.argo.couchbase.exception.BucketException;
import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.ViewDesign;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.*;
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
@Component
public class DesignDocManager implements InitializingBean {

	private static final String CB_DDOC = "couchbase";

	public static final Logger logger = LoggerFactory.getLogger(DesignDocManager.class);
	
	private static final String V_REDUCE = "reduce";
	private static final String V_MAP = "map";
	
	@Autowired
	private BucketManager bucketManager;
	
	@Autowired
	private CouchbaseTemplate couchbaseTemplate;

    private List<String> changes = null;

    private File getFolder() throws IOException {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        Resource resourceRoot = pathResolver.getResource(CB_DDOC);
        return resourceRoot.getFile();
    }
	/**
	 * 初始化所有DDOC. 
	 * @throws IOException
	 */
	public synchronized void syncCreateAll() throws IOException {
        File folder = this.getFolder();
		if (folder != null) {
            logger.info(folder.getAbsolutePath());
            File file = new File(folder.getAbsolutePath() + File.separator + "changes");
            this.loadChangedDdocs(file);
			File[] files = folder.listFiles();
			for (File item : files) {
                if (item.isFile()){
                    continue;
                }
				try {
					this.syncViews(item);
				} catch (BucketException e) {
					logger.error("Sync Create DDOC Error. path=" + item.getPath(), e);
				}
			}
            this.changes = null;
            try {
                if (file.exists()){
                    FileUtils.write(file, "");
                }
            } catch (IOException e) {

            }
        } else {
			throw new IOException("Folder couchbase can't found in classpath.");
		}
	}

    private void loadChangedDdocs(File file){
        if (file.exists()){
            try {
                this.changes = FileUtils.readLines(file);
            } catch (IOException e) {
                logger.error("loadChangedDDocs", e);
            }
        }
    }

	/**
	 * 列出现有的DDOCS.
	 * @return
	 * @throws IOException
	 */
	public synchronized List<String> listAll() throws IOException {
        File folder = this.getFolder();
		if (folder != null) {
			File[] files = folder.listFiles();
			List<String> paths = new ArrayList<String>();
			for (File item : files) {
				paths.add(item.getAbsolutePath());
			}
			return paths;
		} else {
			throw new IOException("Folder couchbase can't found in classpath.");
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
        if (this.changes!=null && !this.changes.contains(ddocName)){
            return;
        }
		File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
		Map<String, String> maps = new HashMap<String, String>();
		Map<String, String> reduces = new HashMap<String, String>();
		for (File file : files) {
            // {viewName}.map.json
            // {viewName}.reduce.json
            if (logger.isDebugEnabled()){
                logger.debug("syncViews: file:{}, {}", file.getAbsoluteFile(), file.getName());
            }
			String[] temp = file.getName().split("\\.");
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
        logger.info("Create DesignDocument. bucketName={}, ddocName={}", bucketName, ddocName);
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
		this.syncCreateAll();
	}
}
