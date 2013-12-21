package com.argo.couchbase;

import com.argo.core.json.GsonUtil;
import com.argo.core.metric.MetricCollectorImpl;
import com.argo.couchbase.exception.BucketException;
import com.argo.couchbase.exception.BucketQueryException;
import com.codahale.metrics.Timer;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.*;
import net.spy.memcached.internal.OperationFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 提供Couchbase的基本(CRUD)操作
 * 
 * @author yaming_deng
 * 
 */
@Component
public class CouchbaseTemplate implements InitializingBean {

	public static final Logger logger = LoggerFactory
			.getLogger(CouchbaseTemplate.class);


	@Autowired
	private BucketManager bucketManager;


	private <T> T execute(final BucketCallback<T> action)
			throws BucketException {
		try {
			return action.doInBucket();
		} catch (RuntimeException e) {
			throw new BucketException(e.getMessage(), e);
		} catch (TimeoutException e) {
			throw new BucketQueryException(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new BucketQueryException(e.getMessage(), e);
		} catch (ExecutionException e) {
			throw new BucketQueryException(e.getMessage(), e);
		}
	}

    private void callCountIncr(String bucketName, String name){
        String realName = bucketName+":"+name;
        MetricCollectorImpl.current().incrementCounter(this.getClass(), realName, 1);
    }

	/**
	 * 添加非JSON结构的数据(永不过期).
	 * 
	 * @param bucketName
	 * @param key
	 * @param data
	 * @throws BucketException
	 */
	public final void addBinary(String bucketName, final String key,
			final Object data) throws BucketException {
        callCountIncr(bucketName, "addBinary");
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() throws InterruptedException,
					ExecutionException {
				return client.add(key.toLowerCase(), 0, data).get();
			}
		});
	}

	/**
	 * 添加非JSON结构的数据(按照exp参数过期).
	 * 
	 * @param bucketName
	 * @param key
	 * @param data
	 * @param exp
	 *            过期时间，单位为秒.
	 * @throws BucketException
	 */
	public final void addBinary(String bucketName, final String key,
			final Object data, final int exp) throws BucketException {
        callCountIncr(bucketName, "addBinary");
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() throws InterruptedException,
					ExecutionException {
				return client.add(key.toLowerCase(), exp, data).get();
			}
		});
	}

	/**
	 * 保存非JSON结构的数据(永不过期).
	 * 
	 * @param bucketName
	 * @param key
	 * @param data
	 * @throws BucketException
	 */
	public final void setBinary(String bucketName, final String key,
			final Object data) throws BucketException {
        callCountIncr(bucketName, "setBinary");
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() throws InterruptedException,
					ExecutionException {
				return client.set(key.toLowerCase(), 0, data).get();
			}
		});
	}

	/**
	 * 保存非JSON结构的数据(按照exp参数过期).
	 * 
	 * @param bucketName
	 * @param key
	 * @param data
	 * @param exp
	 *            过期时间，单位为秒.
	 * @throws BucketException
	 */
	public final void setBinary(String bucketName, final String key,
			final Object data, final int exp) throws BucketException {
        callCountIncr(bucketName, "setBinary");
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() throws InterruptedException,
					ExecutionException {
				return client.set(key.toLowerCase(), exp, data).get();
			}
		});
	}

	/**
	 * 读取非JSON结构的数据
	 * 
	 * @param bucketName
	 * @param key
	 * @return
	 * @throws BucketException
	 */
	public final Object getBinary(String bucketName, final String key)
			throws BucketException {
        callCountIncr(bucketName, "getBinary");
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		return execute(new BucketCallback<Object>() {
			@Override
			public Object doInBucket() throws InterruptedException,
					ExecutionException {
				return client.get(key.toLowerCase());
			}
		});
	}

	/**
	 * 使用incr操作来完成主键生成.
	 * 
	 * @param clzz
	 * @return
	 * @throws BucketException
	 */
	public final Long uuid(Class<?> clzz) throws BucketException {
        String bucketName = this.bucketManager.getBucketByEntity(clzz);
        callCountIncr(bucketName, "uuid");
		final String key = String.format("pk:%s", clzz.getSimpleName());
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		return client.incr(key, 1);
	}

	/**
	 * 新增JSON结构数据.
	 * 
	 * @param objectToSave
	 *            要保存的对象, 需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public final void insert(final BucketEntity objectToSave)
			throws BucketException {
		BucketManager.ensureNotIterable(objectToSave);
		String bucketName = this.bucketManager.getBucketByEntity(objectToSave
				.getClass());

        callCountIncr(bucketName, "insert");

		final CouchbaseClient client = this.bucketManager.get(bucketName);

		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() throws InterruptedException,
					ExecutionException {
				String json = GsonUtil.toJson(objectToSave);
				return client.add(objectToSave.getCouchbaseKey(), 0, json).get();
			}
		});
	}

	/**
	 * 新增JSON结构数据.
	 * 
	 * @param batchToSave
	 *            要保存的对象数组, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public final void insert(final Collection<BucketEntity> batchToSave)
			throws BucketException {
		for (final BucketEntity aBatchToSave : batchToSave) {
			insert(aBatchToSave);
		}
	}

	/**
	 * 更新JSON结构数据.
	 * 
	 * @param objectToSave
	 *            要保存的对象数组, 每个对象需从({@link BucketEntity}继承.
	 * @param deletedExpired
	 *            若对象是删除状态的话，是否物理删除数据, deletedExpired=true则物理删除.
	 * @throws BucketException
	 */
	private String _save(final BucketEntity objectToSave,
			final Boolean deletedExpired) throws BucketException {
		BucketManager.ensureNotIterable(objectToSave);

		String bucketName = this.bucketManager.getBucketByEntity(objectToSave
				.getClass());
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() throws InterruptedException,
					ExecutionException {
				if (deletedExpired && objectToSave.isDeleted()) {
					client.touch(objectToSave.getCouchbaseKey(), BucketManager.EXP_DELETED);
					return true;
				} else {
					String json = GsonUtil.toJson(objectToSave);
					return client.set(objectToSave.getCouchbaseKey(), 0, json).get();
				}
			}
		});
        return bucketName;
	}

	/**
	 * 更新JSON结构数据.
	 * 
	 * @param objectToSave
	 *            要保存的对象数组, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void save(final BucketEntity objectToSave) throws BucketException {
		String bucketName = this._save(objectToSave, false);
        callCountIncr(bucketName, "save");
	}

	/**
	 * 更新JSON结构数据.
	 * 
	 * @param batchToSave
	 *            要保存的对象数组, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void save(final Collection<BucketEntity> batchToSave)
			throws BucketException {
		for (final BucketEntity aBatchToSave : batchToSave) {
			save(aBatchToSave);
		}
	}

	/**
	 * 删除记录(逻辑删除)
	 * 
	 * @param objectToSave
	 *            要删除的对象, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void remove(final BucketEntity objectToSave) throws BucketException {
		BucketManager.ensureNotIterable(objectToSave);
		objectToSave.setDeleteAt(new Date());
		objectToSave.setDeleted(true);
		String bucketName = this._save(objectToSave, false);
        callCountIncr(bucketName, "remove");
	}

	/**
	 * 删除记录(逻辑删除)
	 * 
	 * @param batchToSave
	 *            要删除的对象, 每个对象需从({@link BucketEntity}继承.
	 * @param batchToSave
	 * @throws BucketException
	 */
	public void remove(final Collection<BucketEntity> batchToSave)
			throws BucketException {
		for (final BucketEntity aBatchToSave : batchToSave) {
			remove(aBatchToSave);
		}
	}

	/**
	 * 删除记录(逻辑删除)
	 * 
	 * @param objectToSave
	 *            要删除的对象, 每个对象需从({@link BucketEntity}继承.
	 * @param expired
	 *            是否同时设置记录过期时间，让Couchbase自动物理删除记录.
	 * @throws BucketException
	 */
	public void remove(final BucketEntity objectToSave, boolean expired)
			throws BucketException {
		BucketManager.ensureNotIterable(objectToSave);
		objectToSave.setDeleteAt(new Date());
		objectToSave.setDeleted(true);
		String bucketName = this._save(objectToSave, expired);
        callCountIncr(bucketName, "remove");
	}

	/**
	 * 删除记录(逻辑删除)
	 * 
	 * @param batchToSave
	 *            要删除的对象, 每个对象需从({@link BucketEntity}继承.
	 * @param expired
	 *            是否同时设置记录过期时间，让Couchbase自动物理删除记录.
	 * @throws BucketException
	 */
	public void remove(final Collection<BucketEntity> batchToSave,
			boolean expired) throws BucketException {
		for (final BucketEntity aBatchToSave : batchToSave) {
			remove(aBatchToSave, expired);
		}
	}

	/**
	 * 替换记录
	 * 
	 * @param objectToSave
	 *            要替换的对象, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void update(final BucketEntity objectToSave) throws BucketException {
		BucketManager.ensureNotIterable(objectToSave);

		String bucketName = this.bucketManager.getBucketByEntity(objectToSave
				.getClass());
		final CouchbaseClient client = this.bucketManager.get(bucketName);

        execute(new BucketCallback<Boolean>() {
            @Override
            public Boolean doInBucket() throws InterruptedException,
                    ExecutionException {
                String json = GsonUtil.toJson(objectToSave);
                return client.replace(objectToSave.getCouchbaseKey(), 0, json).get();
            }
        });

        callCountIncr(bucketName, "update");
	}

	/**
	 * 替换记录
	 * 
	 * @param batchToSave
	 *            要替换的对象, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void update(final Collection<BucketEntity> batchToSave)
			throws BucketException {
		for (final BucketEntity aBatchToSave : batchToSave) {
			save(aBatchToSave);
		}
	}

	/**
	 * 物理删除记录.
	 * 
	 * @param objectToRemove
	 *            要删除的对象, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void erase(final BucketEntity objectToRemove) throws BucketException {
		BucketManager.ensureNotIterable(objectToRemove);

		String bucketName = this.bucketManager.getBucketByEntity(objectToRemove
				.getClass());
		final CouchbaseClient client = this.bucketManager.get(bucketName);

		execute(new BucketCallback<OperationFuture<Boolean>>() {
            @Override
            public OperationFuture<Boolean> doInBucket() {
                return client.delete(objectToRemove.getCouchbaseKey());
            }
        });

        callCountIncr(bucketName, "erase");
	}

	/**
	 * 物理删除记录.
	 * 
	 * @param batchToRemoves
	 *            要删除的对象, 每个对象需从({@link BucketEntity}继承.
	 * @throws BucketException
	 */
	public void erase(final Collection<BucketEntity> batchToRemoves)
			throws BucketException {
		if (batchToRemoves == null || batchToRemoves.size() == 0) {
			return;
		}
		for (BucketEntity item : batchToRemoves) {
			this.erase(item);
		}
	}

	/**
	 * 查询视图.
	 * 
	 * @param entityClass
	 *            视图返回记录对应的实体类, 需要继承{@link BucketEntity}
	 * @param viewName
	 *            视图名称
	 * @param query
	 *            查询语句({@link Query}
	 * @return 返回对象数组List<T>
	 * @throws BucketException
	 */
	public <T> List<T> findByView(final Class<T> entityClass,
			final String viewName, final Query query) throws BucketException {

        String bucketName = this.bucketManager.getBucketByEntity(entityClass);
        callCountIncr(bucketName, "findByView");
		final Timer.Context context = MetricCollectorImpl.current().getTimer(this.getClass(), "findByView");
		try {
			if (!query.willIncludeDocs()) {
				query.setIncludeDocs(true);
			}
			if (query.willReduce()) {
				query.setReduce(false);
			}

			final ViewResponse response = queryView(entityClass, viewName,
					query);

			if (response.getErrors() != null && response.getErrors().size() > 0) {
				logger.error("findByView. viewName:{} query:{} error:{}",
						viewName, query, response.getErrors());
			}

			final List<T> result = new ArrayList<T>(response.size());
			for (final ViewRow row : response) {
				T item = GsonUtil.asT(entityClass, (String) row.getDocument());
				result.add(item);
			}

			return result;
		}catch(Exception ex){
			throw new BucketException(ex);
		}
		finally {
			context.stop();
		}
	}

    public <T> List<T> findByView(final Class<T> entityClass,
                                  final String viewName, final Query query, final Integer page, final Integer limit) throws BucketException {

        String bucketName = this.bucketManager.getBucketByEntity(entityClass);
        callCountIncr(bucketName, "findByViewPaging");
        final Timer.Context context = MetricCollectorImpl.current().getTimer(this.getClass(), "findByViewPaging");
        try {
            if (!query.willIncludeDocs()) {
                query.setIncludeDocs(true);
            }
            if (query.willReduce()) {
                query.setReduce(false);
            }

            Integer skip = (page <=0 ? 0 : (page - 1) * limit);
            query.setSkip(skip);
            query.setLimit(limit);

            final ViewResponse response = queryView(entityClass, viewName,
                    query);

            if (response.getErrors() != null && response.getErrors().size() > 0) {
                logger.error("findByView. viewName:{} query:{} error:{}",
                        viewName, query, response.getErrors());
            }

            final List<T> items = new ArrayList<T>(response.size());
            for (final ViewRow row : response) {
                T item = GsonUtil.asT(entityClass, (String) row.getDocument());
                items.add(item);
            }

            return items;

        }catch(Exception ex){
            throw new BucketException(ex);
        }
        finally {
            context.stop();
        }
    }

    public <T> Integer countByView(final Class<T> entityClass,
                                  final String viewName, final Query query) throws BucketException {

        String bucketName = this.bucketManager.getBucketByEntity(entityClass);
        callCountIncr(bucketName, "countByView");
        final Timer.Context context = MetricCollectorImpl.current().getTimer(this.getClass(), "countByView");
        try {

            query.setIncludeDocs(false);
            query.setReduce(true);

            final ViewResponse response = queryView(entityClass, viewName,
                    query);

            if (response.getErrors() != null && response.getErrors().size() > 0) {
                logger.error("findByView. viewName:{} query:{} error:{}",
                        viewName, query, response.getErrors());
            }

            Integer total = 0;

            for (final ViewRow row : response) {
                String value = row.getValue();
                Map<String, Object> temp = GsonUtil.convertJson2Map(value);
                Iterator<String> itor = temp.keySet().iterator();
                while (itor.hasNext()) {
                    String key = itor.next();
                    Object num = temp.get(key);
                    total += (Integer) num;
                }
            }

            return total;

        }catch(Exception ex){
            throw new BucketException(ex);
        }
        finally {
            context.stop();
        }
    }

	/**
	 * 按主键查询记录.
	 * 
	 * @param entityClass
	 *            记录对应的实体类, 需要继承{@link BucketEntity}
	 * @param id
	 *            主键id
	 * @return 返回对象<T>
	 * @throws BucketException
	 */
	public <T> T findById(final Class<T> entityClass, final Long id)
			throws BucketException {
		String bucketName = this.bucketManager.getBucketByEntity(entityClass);
        callCountIncr(bucketName, "findById");
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		final String key = BucketManager.getCKeyByEntity(entityClass, id);
		return execute(new BucketCallback<T>() {
			@Override
			public T doInBucket() {
				Object temp = client.get(key);
				if (temp == null) {
					return null;
				}
				return GsonUtil.asT(entityClass, (String) temp);
			}
		});
	}

	/**
	 * 按主键数组查询记录.
	 * 
	 * @param entityClass
	 *            记录对应的实体类, 需要继承{@link BucketEntity}
	 * @param ids
	 *            主键ids
	 * @return 返回对象<T>
	 * @throws BucketException
	 */
	public <T> List<T> findByIds(final Class<T> entityClass,
			final List<Long> ids) throws BucketException {
        String bucketName = this.bucketManager
                .getBucketByEntity(entityClass);

        callCountIncr(bucketName, "findByIds");
		final Timer.Context context = MetricCollectorImpl.current().getTimer(this.getClass(), "findByIds");
		try {

			final CouchbaseClient client = this.bucketManager.get(bucketName);
			final List<String> keys = new ArrayList<String>();
			for (Long id : ids) {
				keys.add(BucketManager.getCKeyByEntity(entityClass, id));
			}
			return execute(new BucketCallback<List<T>>() {
				@Override
				public List<T> doInBucket() {
					Map<String, Object> results = client.getBulk(keys);
					if (results == null || results.size() == 0) {
						return new ArrayList<T>();
					}
					List<T> list = new ArrayList<T>();
					for (Long id : ids) {
						Object temp = results.get(String.valueOf(id));
						if (temp == null) {
							list.add(null);
						} else {
							T item = GsonUtil.asT(entityClass, (String) temp);
							list.add(item);
						}
					}

					return list;
				}
			});
		}catch(Exception ex){
			throw new BucketException(ex);
		} finally {
			context.stop();
		}

	}

	/**
	 * 查询视图.
	 * 
	 * @param clzz
	 *            记录对应的实体类, 需要继承{@link BucketEntity}
	 * @param viewName
	 *            视图名称.
	 * @param query
	 *            查询语句{@link Query}
	 * @return JSON结果值{@link ViewResponse}
	 * @throws BucketException
	 */
	public ViewResponse queryView(Class<?> clzz, final String viewName,
			final Query query) throws BucketException {
		final String designName = clzz.getSimpleName();
		String bucketName = this.bucketManager.getBucketByEntity(clzz);

        callCountIncr(bucketName+":"+viewName, "queryView");
        final Timer.Context context = MetricCollectorImpl.current().getTimer(this.getClass(), bucketName+":"+viewName+":queryView");
        try {

            final CouchbaseClient client = this.bucketManager.get(bucketName);

            return execute(new BucketCallback<ViewResponse>() {
                @Override
                public ViewResponse doInBucket() {
                    final View view = client.getView(designName, viewName);
                    return client.query(view, query);
                }
            });
        } finally {
            context.stop();
        }
    }

	/**
	 * 读取统计用途的视图.
	 * 
	 * @param entityClass
	 * @param viewName
	 * @param query
	 * @return 返回统计Map<String, Long>
	 * @throws BucketException
	 */
	public Map<String, Integer> statView(Class<?> entityClass,
			final String viewName, final Query query) throws BucketException {
        String bucketName = this.bucketManager.getBucketByEntity(entityClass);
        callCountIncr(bucketName, "statView");
		final Timer.Context context = MetricCollectorImpl.current().getTimer(this.getClass(), bucketName+":"+viewName+":statView");
		try {
			query.setIncludeDocs(false);
			query.setReduce(true);
	
			final ViewResponse response = queryView(entityClass, viewName, query);
	
			if (response.getErrors() != null && response.getErrors().size() > 0) {
				logger.error("findByView. viewName:{} query:{} error:{}", viewName,
						query, response.getErrors());
			}
	
			Map<String, Integer> stats = new HashMap<String, Integer>();
	
			for (final ViewRow row : response) {
				String value = row.getValue();
				Map<String, Object> temp = GsonUtil.convertJson2Map(value);
				Iterator<String> itor = temp.keySet().iterator();
				while (itor.hasNext()) {
					String key = itor.next();
					Object num = temp.get(key);
					stats.put(key, (Integer) num);
				}
				// 只取第1条记录.
				break;
			}
	
			return stats;
		}catch(Exception ex){
			throw new BucketException(ex);
		} finally {
			context.stop();
		}
	}

	/**
	 * 判断某个Key是否存在.
	 * 
	 * @param bucketName
	 *            bucket名称
	 * @param key
	 *            记录key
	 * @return true or false
	 * @throws BucketException
	 */
	public boolean exists(final String bucketName, final String key)
			throws BucketException {
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		return execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() {
				return client.get(key) != null;
			}
		});
	}

	/**
	 * 物理删除记录.
	 * 
	 * @param bucketName
	 *            bucket名称.
	 * @param key
	 *            记录key
	 * @throws BucketException
	 */
	public void erase(final String bucketName, final String key)
			throws BucketException {
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() {
				client.delete(key);
				return true;
			}
		});
	}

	/**
	 * 修改记录的过期时间.
	 * 
	 * @param bucketName
	 *            bucket名称
	 * @param key
	 *            记录key
	 * @param exp
	 *            新的过期时间.
	 * @throws BucketException
	 */
	public void touch(final String bucketName, final String key, final int exp)
			throws BucketException {
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		execute(new BucketCallback<Boolean>() {
			@Override
			public Boolean doInBucket() {
				client.touch(key, exp);
				return true;
			}
		});
	}

    /**
     * 同步DesignDocument到Couchbase
     * @param bucketName
     * @param ddoc
     * @throws BucketException
     */
	public void syncDDocViews(String bucketName, DesignDocument ddoc)
			throws BucketException {
		final CouchbaseClient client = this.bucketManager.get(bucketName);
		try {
			client.asyncCreateDesignDoc(ddoc);
		} catch (UnsupportedEncodingException e) {
			throw new BucketException("syncDDocViews", e);
		}
	}

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
