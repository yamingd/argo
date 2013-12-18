package com.argo.core.component;

import com.argo.core.configuration.SiteConfig;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.CRC32;

/**
 * 计算JS/CSS文件内容的哈希值, 用于自动发现js/css是否有修改.
 * @author yaming_deng
 *
 */
public class StaticFileHashComponent implements InitializingBean {
		
	private static StaticFileHashComponent instance;

	private CRC32 CHECKSUM2 = new java.util.zip.CRC32();

	private String rootFolder = "";
	
	private ConcurrentMap<String, String> fileHashMap = null;
	
	public static StaticFileHashComponent getInstance() {
		return instance;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.fileHashMap = new ConcurrentHashMap<String, String>();
		doHash();
		StaticFileHashComponent.instance = this;
	}

	/**
	 * 计算静态文件Hash值
	 * @throws IOException
	 */
	public void doHash() throws IOException {
		
		String workingFolder = StaticFileHashComponent.class.getResource("/").getPath().split("WEB-INF")[0];
		
		int count = 0;
		String[] paths = this.rootFolder.split("/");
		for(String item : paths){
			if(item.equals("..")){
				count ++;
			}
		}
		this.rootFolder = StringUtils.join(paths, "/", count,paths.length);
		
		File tmp = new File(workingFolder);
		while(count>0){
			tmp = tmp.getParentFile();
			count--;
		}
		
		File file = new File(tmp.getAbsolutePath()+"/"+this.rootFolder);
		
		String parentFolder = "/";

		hashJSCSSFile(file, parentFolder);
	}

	/**
	 * 计算静态文件Hash值
	 * @param file
	 * @param parentFolder
	 * @throws IOException
	 */
	private void hashJSCSSFile(File file, String parentFolder)
			throws IOException {
		File[] files = file.listFiles();
		if(files!=null){
			for(File item : files){
				if(item.isFile() && this.isJsOrCss(item.getName())){
					String filePath = item.getAbsolutePath();
					fileHashMap.put(parentFolder+item.getName(), this.hashFileContent(filePath));
				}else if(item.isDirectory() && !item.isHidden()){
					this.hashJSCSSFile(item, parentFolder+item.getName()+"/");
				}
			}
		}
	}
	
	/**
	 * 是否是JS/CSS文件.
	 * @param fileName
	 * @return
	 */
	private boolean isJsOrCss(String fileName){
		return fileName.endsWith(".css") || fileName.endsWith(".js");
	}
	
	public void setRootFolder(String folder) {
		this.rootFolder = folder;
	}

	public String getRootFolder() {
		if(this.rootFolder==null){
			this.rootFolder = (String) SiteConfig.instance.getStatic().get("folder");
		}
		return rootFolder;
	}
	
	/**
	 * 计算文件Hash值.
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public String hashFileContent(String filePath) throws IOException{
        HashCode hc = Files.hash(new File(filePath), Hashing.sha1());
		return hc.toString().substring(0, 5);
	}
	
	/**
	 * 获取文件的Hash值.
	 * @param file
	 * @return
	 */
	public String getHash(String file){
		if(file.startsWith("/")){
			return this.fileHashMap.get(file);
		}
		return this.fileHashMap.get("/"+file);
	}
}
