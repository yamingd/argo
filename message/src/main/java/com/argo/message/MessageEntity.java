package com.argo.message;

import com.argo.core.json.GsonUtil;
import com.argo.core.web.WebContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * MQ消息内容.
 * @author yaming_deng
 *
 */
public class MessageEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7580977470511492286L;
	private String fromAppName = "";
	private String toQueueName = "";
	/**
	 * 用户操作代号. 必须提供。
	 */
	private Integer opCode = null;
	/**
	 * 当前操作人. 必须提供
	 */
	private Long opUserId = null;
	/**
	 * 当前操作人的IP地址
	 */
	private String opUserIp = null;
	/**
	 * 扩展数据Map.只允许存储Int,Date,String三种类型的数据
	 */
	private Map<String, Object> extraData = new HashMap<String, Object>();
	
	private Date opDate = null;
	
	public Integer getOpCode() {
		return opCode;
	}
	public void setOpCode(Integer opCode) {
		this.opCode = opCode;
	}
	
	public Long getOpUserId() {
		return opUserId;
	}
	public void setOpUserId(Long opUserId) {
		this.opUserId = opUserId;
	}
	
	public String getOpUserIp() {
		return opUserIp;
	}
	public void setOpUserIp(String opUserIp) {
		this.opUserIp = opUserIp;
	}
	
	public void putData(String key, Date value){
		this.extraData.put(key, value);
	}
	
	public void putData(String key, String value){
		this.extraData.put(key, value);
	}
	
	public void putData(String key, Integer value){
		this.extraData.put(key, value);
	}
	
	public void putData(Map<String, String> params){
		if(params==null){
			return;
		}
		Iterator<String> itor = params.keySet().iterator();
		while(itor.hasNext()){
			String key = itor.next();
			this.extraData.put(key, params.get(key));
		}
	}
	
	public void removeData(String key){
		this.extraData.remove(key);
	}
	
	public Date getDateValue(String key){
		String val = this.getStringValue(key);
		try {
			return DateUtils.parseDate(val, new String[]{GsonUtil.DATE_FORMAT});
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getStringValue(String key){
		return ObjectUtils.toString(extraData.get(key));
	}
	
	public Integer getIntegerValue(String key){
		if(extraData.get(key)==null){
			return null;
		}
		return Integer.parseInt(ObjectUtils.toString(extraData.get(key)));
	}
	
	public Long getLongValue(String key){
		return Long.parseLong(ObjectUtils.toString(extraData.get(key)));
	}

	public boolean checkData(){
		if(this.opUserIp==null){
			this.opUserIp = WebContext.getContext().getRequestIp();
		}
		Assert.notNull(this.toQueueName, "toQueueName can't be null.");
		Assert.notNull(this.opCode, "opCode can't be null.");
		Assert.notNull(this.opUserId, "opUserId can't be null.");
		Assert.notNull(this.opUserIp, "opUserIp can't be null.");
		this.opDate = new Date();
		return true;
	}

	
	public void setExtraData(Map<String, Object> extraData) {
		this.extraData = extraData;
	}
	public Map<String, Object> getExtraData() {
		return extraData;
	}
	public void setToQueueName(String toQueueName) {
		this.toQueueName = toQueueName;
	}
	public String getToQueueName() {
		return toQueueName;
	}
	public void setFromAppName(String fromAppName) {
		this.fromAppName = fromAppName;
	}
	public String getFromAppName() {
		return fromAppName;
	}
	/**
	 * @param opDate the opDate to set
	 */
	public void setOpDate(Date opDate) {
		this.opDate = opDate;
	}
	/**
	 * @return the opDate
	 */
	public Date getOpDate() {
		return opDate;
	}
}
