package com.argo.couchbase;

import com.argo.core.ContextConfig;
import com.argo.core.base.BaseEntity;
import com.google.common.hash.HashCode;
import com.google.gson.annotations.Expose;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public abstract class BucketEntity extends BaseEntity {
	
	/**
	 * 
	 */

    private String _docType;
    private Long oid;

    @DateTimeFormat(pattern = ContextConfig.DATE_TIME_FORMAT)
	private Date createAt;

    @DateTimeFormat(pattern = ContextConfig.DATE_TIME_FORMAT)
	private Date updateAt;

    @DateTimeFormat(pattern = ContextConfig.DATE_TIME_FORMAT)
	private Date deleteAt;

	private boolean deleted = false;

    public BucketEntity() {
        this._docType = this.getClass().getSimpleName();
    }

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		BucketEntity temp = (BucketEntity)obj;
		if(temp.getPK().equalsIgnoreCase(this.getPK())){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return HashCode.fromString(this.getPK()).asInt();
	}
	
	/**
	 * 用于承载从View查询时返回的Value.
	 */
	@Expose(serialize = false, deserialize = false)
	private transient String viewValue;
	
	/**
	 * 构造主键.
	 * @return
	 */
    @Override
	public String getPK(){
		return this.oid+"";
	}
	
	/**
	 * 获取Couchbase的缓存Key.
	 * @return
	 */
	public String getCouchbaseKey(){
		return String.format("%s:%s", this.getClass().getSimpleName(), this.getPK().trim());
	}
	
	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public Date getDeleteAt() {
		return deleteAt;
	}

	public void setDeleteAt(Date deleteAt) {
		this.deleteAt = deleteAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getViewValue() {
		return viewValue;
	}

	public void setViewValue(String viewValue) {
		this.viewValue = viewValue;
	}

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }
}
