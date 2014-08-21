package com.argo.core.component;

import com.argo.core.ObjectId;
import com.argo.core.base.BaseBean;
import com.argo.core.utils.TokenUtil;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * 附件存储
 * @author yaming_deng
 *
 */

@Component("fileStoreComponent")
public class FileStoreComponent extends BaseBean {
		
	/**
	 * 截取文件名的后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public String getSuffix(String fileName) {
		if (null != fileName && fileName.lastIndexOf(".") >= 0) {
			String[] temp = fileName.split("\\.");
			return temp[temp.length-1];
		}
		return fileName;
	}
	
	/**
	 * @param file 文件内容
	 * @param fileId 数据库产生的文件ID
	 * @param fileCategory 文件归类,如：fault,question,service,system
	 * @return
	 * @throws IOException 
	 */
	public String saveFile(File file, Integer fileId, String fileCategory, String fileExt) throws IOException{
		return this.saveFile(file, fileId, fileCategory, fileExt, true);
	}
	/**
	 * @param file: 文件流
	 * @param fileId：主键ID
	 * @param fileCategory: 分类名称，对应在硬盘的文件夹名称
	 * @param fileExt: 文件扩展名
	 * @param timeStampName：存储的文件名是否包含时间戳. new Date().getTime()
	 * @return
	 * @throws IOException
	 */
	public String saveFile(File file, Integer fileId, String fileCategory, String fileExt, boolean timeStampName) throws IOException{
		String fileName = String.format("%s-%s.%s", fileId,new Date().getTime(),fileExt);
		if(!timeStampName){
			fileName = String.format("%s.%s", fileId, fileExt);
		}
		String[] paths = this.generateFolder(fileId, fileCategory);
		File destFile = new File(paths[0]+fileName);
		if(destFile.exists()){
			destFile.delete();
		}
        Files.copy(file, destFile);
		file.delete();
		return paths[1]+fileName;
	}
	/**
	 * Hash File Content
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String hashFile(File file) throws IOException{
        HashCode hc = Files.hash(file, Hashing.sha1());
		return hc.toString();
	}
	public String hashFile(String filePath) throws IOException{
		if(StringUtils.isBlank(filePath)){
			return null;
		}
		File file = new File(String.format("%s/%s", getFileRootFolder(),filePath));
		if(!file.exists()){
			throw new FileNotFoundException("文件不存在:"+filePath);
		}
        HashCode hc = Files.hash(file, Hashing.sha1());
        return hc.toString();
	}
	/**
	 * 读取文件.
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public File readFile(String filePath)throws IOException{
		return this.readFile(this.getFileRootFolder(), filePath);
	}
	public File readFile(String rootFolder, String filePath)throws IOException{
		if(StringUtils.isBlank(filePath)){
			return null;
		}
		return this.readAbsFile(String.format("%s/%s", rootFolder,filePath));
	}
	public File readAbsFile(String fileAbsPath)throws IOException{
		if(StringUtils.isBlank(fileAbsPath)){
			return null;
		}
		File file = new File(fileAbsPath);
		if(!file.exists()){
			throw new FileNotFoundException("文件不存在:"+fileAbsPath);
		}
		return file;
	}
	/**
	 * 删除一个文件.
	 * @param filePath
	 * @throws IOException
	 */
	public void deleteFile(String filePath)throws Exception{
		try {
			File file = readFile(filePath);
			if(file!=null){
				file.delete();
			}
		} catch (FileNotFoundException e) {
			
		}
	}
	/**
	 * 用Hash算法计算存储目录
	 * @param fileId
	 * @param fileCategory
	 * @return String[fullPath, Path]
	 */
	public String[] generateFolder(Integer fileId, String fileCategory){
		String fileName = String.format("file-%s-%s", fileCategory,fileId).toLowerCase();
		String hex = TokenUtil.md5(fileName);
		String rootFolder = getFileRootFolder();
		String path = String.format("/%s/%s/%s/%s/%s/", fileCategory, hex.substring(0, 3),hex.substring(3, 6),hex.substring(6, 9),hex.substring(9, 12));
		File folder = new File(String.format("%s%s", rootFolder,path));
		if(!folder.exists()){
			folder.mkdirs();
		}
		return new String[]{String.format("%s%s", rootFolder,path), path};
	}

    /**
     * 用Hash算法计算存储目录
     * @param fileCategory
     * @return String[fullPath, Path]
     */
    public String[] randomFolder(String fileCategory){
        String fileName = String.format("file-%s-%s-%s", ObjectId.getGenMachineId(), fileCategory, new Date().getTime()).toLowerCase();
        String hex = TokenUtil.md5(fileName);
        String rootFolder = getFileRootFolder();
        String path = String.format("/%s/%s/%s/%s/%s/", fileCategory, hex.substring(0, 3),hex.substring(3, 6),hex.substring(6, 9),hex.substring(9, 12));
        File folder = new File(String.format("%s%s", rootFolder,path));
        if(!folder.exists()){
            folder.mkdirs();
        }
        return new String[]{String.format("%s%s", rootFolder,path), path};
    }

	/**
	 * 存储根目录.
	 * @return
	 */
	public String getFileRootFolder() {
		return (String)getSiteConfig().getFile().get("folder");
	}
	
	/**
	 * 获取文件名称
	 * @param file
	 * @param fileId
	 * @return
	 */
	protected String getFileName(File file, Integer fileId){
		String[] name = file.getName().split("\\.");
		return String.format("%s.%s", fileId,name[name.length-1]);
	}
}
