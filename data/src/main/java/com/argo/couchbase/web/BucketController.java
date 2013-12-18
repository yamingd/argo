package com.argo.couchbase.web;

import com.argo.couchbase.CouchbaseTemplate;
import com.argo.couchbase.DesignDocManager;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/admin/couchbase/")
public class BucketController {
	
	@Autowired
	private DesignDocManager designDocManager;
	
	@Autowired
	private CouchbaseTemplate couchbaseTemplate;
	
	/**
	 * 访问URL: /admin/couchbase/init
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="init", method=RequestMethod.GET)
	public @ResponseBody String init(HttpServletRequest request, HttpServletResponse response) {
		try {
			designDocManager.syncCreateAll();
			return "DONE";
		} catch (IOException e) {
			DesignDocManager.logger.error("初始化DDOC Error.", e);
			return e.getMessage();
		}
	}
	
	/**
	 * 访问URL: /admin/couchbase/view/update?path=/a/b/c/d
	 * @param path
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="view/update", method=RequestMethod.GET)
	public @ResponseBody String viewUpdate(@RequestParam(value="path", defaultValue="") String path,
			HttpServletRequest request, HttpServletResponse response) {
		if(path == null || path.length() == 0){
			return "No Path";
		}
		try {
			designDocManager.syncCreate(path);
			return "DONE";
		} catch (IOException e) {
			DesignDocManager.logger.error("初始化DDOC Error. path=" + path, e);
			return e.getMessage();
		}
	}
	
	/**
	 * 访问URL: /admin/couchbase/json?bucket=$bucket&key=$key
	 * @param key
	 * @param bucket
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="json", method=RequestMethod.GET)
	public @ResponseBody String jsonView(@RequestParam(value="key", defaultValue="") String key,
			@RequestParam(value="bucket", defaultValue="") String bucket,
			HttpServletRequest request, HttpServletResponse response) {
		if(key == null || key.length() == 0){
			return "No key";
		}
		try {
			Object temp = this.couchbaseTemplate.getBinary(bucket, key);
			if(temp == null){
				return "JSON is Empty";
			}
			return ObjectUtils.toString(temp);
		} catch (Exception e) {
			DesignDocManager.logger.error("Read JsonView Error. key=" + key +", bucket=" + bucket, e);
			return e.getMessage();
		}
	}
}
