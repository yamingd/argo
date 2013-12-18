package com.argo.elasticsearch.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.indices.IndexMissingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import com.argo.elasticsearch.proxy.GenericInvocationHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


public abstract class ElasticsearchAbstractClientFactoryBean extends ElasticsearchAbstractFactoryBean 
	implements FactoryBean<Client>,	InitializingBean, DisposableBean {

	protected static Log logger = LogFactory.getLog(ElasticsearchAbstractClientFactoryBean.class);

	protected Client client;
	protected Client proxyfiedClient;

	protected boolean rebuildMapping = false;
	
	protected boolean rebuildTemplate = true;
	
	protected boolean mergeMapping = true;
	
	protected boolean mergeSettings = true;
	
	protected String[] mappings;

	protected String jsonFileExtension = ".json";

	protected String indexSettingsFileName = "_settings.json";

    protected String updateIndexSettingsFileName = "_update_settings.json";

	protected String templateDir = "_template";

	/**
	 * Implement this method to build a client
	 * @return ES Client
	 * @throws Exception if something goes wrong
	 */
	abstract protected Client buildClient() throws Exception;
		
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Starting ElasticSearch client");
		
		if (async) {
			Assert.notNull(taskExecutor);
			Future<Client> future = taskExecutor.submit(new Callable<Client>() {
				@Override
				public Client call() throws Exception {
					return initialize();
				}
			});
			proxyfiedClient = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(),
					new Class[]{Client.class}, new GenericInvocationHandler(future));

		} else {
			client = initialize();
		}
	}


	private Client initialize() throws Exception {
		client = buildClient();
		
		Map map = this.config.getPolicy();
		this.rebuildMapping = (Boolean) map.get("rebuild_mapping");
		this.rebuildTemplate = (Boolean) map.get("rebuild_template");
		this.mergeSettings = (Boolean) map.get("merge_settings");
		
		List templates = this.config.get(List.class, "templates");
		
		scanMappings();
		
		initTemplates(templates);	
		initMappings();
		initAliases();

		return client;
	}

	@Override
	public void destroy() throws Exception {
		try {
			logger.info("Closing ElasticSearch client");
			if (client != null) {
				client.close();
			}
		} catch (final Exception e) {
			logger.error("Error closing ElasticSearch client: ", e);
		}
	}

	@Override
	public Client getObject() throws Exception {
		return async ? proxyfiedClient : client;
	}

	@Override
	public Class<Client> getObjectType() {
		return Client.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Init templates if needed.
	 * <p>
	 * Note that you can force to recreate template using
	 *
	 * @throws Exception
	 */
	private void initTemplates(List templates) throws Exception {
		if (templates != null && templates.size() > 0) {
			for (Object temp : templates) {
				String template = (String)temp;
				Assert.hasText(template, "Can not read template in ["
						+ template
						+ "]. Check that templates is not empty.");
				createTemplate(template, rebuildTemplate);
			}
		}
	}
	
	/**
	 * convention over configuration 
	 */
	private void scanMappings() {
		if (mappings == null || mappings.length == 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Automatic discovery is activated. Looking for definition files in classpath under " + this.config.getClassPathRoot() + ".");
			}
			
			ArrayList<String> autoMappings = new ArrayList<String>();
			// Let's scan our resources
			PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
			Resource resourceRoot = pathResolver.getResource(this.config.getClassPathRoot());
			try {
				Resource[] resources = pathResolver.getResources("classpath:"+this.config.getClassPathRoot() + "/**/*"+jsonFileExtension);
				for (int i = 0; i < resources.length; i++) {
					String relPath = resources[i].getURI().toString().substring(resourceRoot.getURI().toString().length());
					logger.info("scan Mapping, relPath:" + relPath);
					// If relPath starts with / we must ignore first char
					if (relPath.startsWith("/")) {
						relPath = relPath.substring(1);
					}
					
					// ignore _settings.json and _update_settings.json files (as they are not really mappings)
					// ignore _template dir
					if (!relPath.startsWith(templateDir)) {
                        if (relPath.endsWith(indexSettingsFileName)) {
                        } else if (relPath.endsWith(updateIndexSettingsFileName)) {
                        } else {
                            relPath = relPath.replace(jsonFileExtension, "");
                            autoMappings.add(relPath);
                        }
						
						if (logger.isDebugEnabled()) {
							logger.debug("Automatic discovery found " + relPath + " json file in classpath under " + this.config.getClassPathRoot() + ".");
						}
					}
				}
				
				mappings = (String[]) autoMappings.toArray(new String[autoMappings.size()]);

			} catch (IOException e) {
				if (!logger.isTraceEnabled() && logger.isDebugEnabled()) {
					logger.debug("Automatic discovery does not succeed for finding json files in classpath under " + this.config.getClassPathRoot() + ".");
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Automatic discovery does not succeed for finding json files in classpath under " + this.config.getClassPathRoot() + ".", e);
				}
			}
		}
	}

	/**
	 * Init mapping if needed.
	 * @throws Exception
	 */
	private void initMappings() throws Exception {
		// We extract indexes and mappings to manage from mappings definition
		if (mappings != null && mappings.length > 0) {
			Map<String, Collection<String>> indexes = new HashMap<String, Collection<String>>();
			
			for (int i = 0; i < mappings.length; i++) {
				String indexmapping = mappings[i];
				String[] indexmappingsplitted = indexmapping.split("/");
				String index = indexmappingsplitted[0];
                logger.info("index:"+index+", mapping:"+indexmapping);
                if (index == null){
                	throw new Exception("Can not read index in [" + indexmapping +
                        "]. Check that mappings contains only indexname/mappingname elements.");
                }
                // We add the mapping in the collection of its index
                if (!indexes.containsKey(index)) {
                    indexes.put(index, new ArrayList<String>());
                }

                if (indexmappingsplitted.length > 1) {
                    String mapping = indexmappingsplitted[1];
                    indexes.get(index).add(mapping);
                }
			}
			
			// Let's initialize indexes and mappings if needed
			for (String index : indexes.keySet()) {
				if (!isIndexExist(index)) {
					createIndex(index);
				} else {
					if (mergeSettings) {
						mergeIndexSettings(index);
					}
				}
				
				Collection<String> mappings = indexes.get(index);
				for (Iterator<String> iterator = mappings.iterator(); iterator
						.hasNext();) {
					String type = iterator.next();
					pushMapping(index, type, rebuildMapping, mergeMapping);
                    logger.info("create mapping ok. index="+index+", type="+type);
				}
			}
		}
	}

	/**
	 * Init aliases if needed.
	 * @throws Exception 
	 */
	private void initAliases() throws Exception {
        Map map = this.config.get(Map.class, "alias");
        if(map == null){
            return;
        }
        for (Object key : map.keySet()){
            String index = (String)key;
            String value = (String)map.get(key);
            String[] alias = value.split(",");
            if (alias.length == 0){
                throw new Exception("Can not read mapping in [" + index +
                        "]. Check that aliases contains only alias1,alias2 elements.");
            }
            if (!this.isIndexExist(index)){
                 logger.error("index doesn't exists. can't create alias for it. index="+index);
                continue;
            }
            logger.info("create Alias: index="+index);
            for (String item : alias){
                createAlias(item, index);
            }
        }
	}

	/**
	 * Check if client is still here !
	 * @throws Exception
	 */
	private void checkClient() throws Exception {
		if (client == null) {
			throw new Exception("ElasticSearch client doesn't exist. Your factory is not properly initialized.");
		}
	}
	
	/**
	 * Create an alias if needed
	 * @param alias
	 * @param index
	 * @throws Exception
	 */
    private void createAlias(String alias, String index) throws Exception {
		if (logger.isTraceEnabled()){
			logger.trace("createAlias("+alias+","+index+")");
		}
		
		checkClient();
		
		IndicesAliasesResponse response = client.admin().indices().prepareAliases().addAlias(index, alias).execute().actionGet();
		if (!response.isAcknowledged()){
			throw new Exception("Could not define alias [" + alias + "] for index [" + index + "].");
		}
		if (logger.isTraceEnabled()){
			logger.trace("/createAlias("+alias+","+index+")");
		}
	}

	/**
	 * Create a template if needed
	 * 
	 * @param template template name
	 * @param force    force recreate template
	 * @throws Exception
	 */
	private void createTemplate(String template, boolean force)
			throws Exception {
		if (logger.isTraceEnabled()){
			logger.trace("createTemplate(" + template + ")");
		}
		
		checkClient();

		// If template already exists and if we are in force mode, we delete the template
		if (force && isTemplateExist(template)) {
			if (logger.isDebugEnabled()){
				logger.debug("Force remove template [" + template + "]");
			}
			// Remove template in ElasticSearch !
			client.admin()
					.indices().prepareDeleteTemplate(template).execute()
					.actionGet();
		}

		// Read the template json file if exists and use it
		String source = readTemplate(template);
		if (source != null) {
			if (logger.isTraceEnabled()){
				logger.trace("Template [" + template + "]=" + source);
			}
			// Create template
			final PutIndexTemplateResponse response = client.admin().indices()
					.preparePutTemplate(template).setSource(source).execute()
					.actionGet();
			if (!response.isAcknowledged()) {
				throw new Exception("Could not define template [" + template
						+ "].");
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Template [" + template
							+ "] successfully created.");
				}
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("No template definition for [" + template
						+ "]. Ignoring.");
			}
		}

		if (logger.isTraceEnabled()){
			logger.trace("/createTemplate(" + template + ")");
		}
	}
    
	/**
	 * Check if an index already exists
	 * @param index Index name
	 * @return true if index already exists
	 * @throws Exception
	 */
    private boolean isIndexExist(String index) throws Exception {
		checkClient();
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}
    
    /**
	 * Check if a mapping already exists in an index
	 * @param index Index name
	 * @param type Mapping name
	 * @return true if mapping exists
	 */
	private boolean isMappingExist(String index, String type) {
        IndexMetaData imd = null;
        try {
            ClusterState cs = client.admin().cluster().prepareState().setFilterIndices(index).execute().actionGet().getState();
            imd = cs.getMetaData().index(index);
        } catch (IndexMissingException e) {
            // If there is no index, there is no mapping either
        }

        if (imd == null) return false;

		MappingMetaData mdd = imd.mapping(type);

		if (mdd != null) return true;
		return false;
	}

	/**
	 * Check if a template already exists
	 * 
	 * @param template template name
	 * @return true if template exists
	 */
	private boolean isTemplateExist(String template) {
		ClusterState cs = client.admin().cluster().prepareState()
				.setFilterIndexTemplates(template).execute().actionGet()
				.getState();
		final IndexTemplateMetaData mdd = cs.getMetaData().templates()
				.get(template);

		if (mdd != null) return true;
		return false;
	}
	
	/**
	 * Define a type for a given index and if exists with its mapping definition
	 * @param index Index name
	 * @param type Type name
	 * @param force Force rebuild the type : <b>Caution</b> : if true, all your datas for
	 * this type will be erased. Use only for developpement or continuous integration
	 * @param merge Merge existing mappings
	 * @throws Exception
	 */
	private void pushMapping(String index, String type, boolean force, boolean merge) throws Exception {
		if (logger.isTraceEnabled()){
			logger.trace("pushMapping("+index+","+type+","+force+")");
		}

		checkClient();

        boolean mappingExist = isMappingExist(index, type);

		// If type already exists and if we are in force mode, we delete the type and its mapping
		if (force && mappingExist) {
			if (logger.isDebugEnabled()) {
                logger.debug("Force remove old type and mapping ["+index+"]/["+type+"]");
            }
			// Remove mapping and type in ElasticSearch !
			client.admin().indices()
				.prepareDeleteMapping(index)
				.setType(type)
				.execute().actionGet();

            mappingExist = false;
		}
		
		// If type does not exist, we create it
		if (merge || !mappingExist) {
			if (logger.isDebugEnabled()) {
				if (mappingExist) {
					logger.debug("Updating mapping ["+index+"]/["+type+"].");
				} else {
					logger.debug("Mapping ["+index+"]/["+type+"] doesn't exist. Creating it.");
				}
			}
			// Read the mapping json file if exists and use it
			String source = readMapping(index, type);
			if (source != null) {
				if (logger.isTraceEnabled()){
                    logger.trace("Mapping for ["+index+"]/["+type+"]="+source);
                }
				// Create type and mapping
				PutMappingResponse response = client.admin().indices()
					.preparePutMapping(index)
					.setType(type)
					.setSource(source)
					.execute().actionGet();			
				if (!response.isAcknowledged()) {
					throw new Exception("Could not define mapping for type ["+index+"]/["+type+"].");
				} else {
					if (logger.isDebugEnabled()) {
						if (mappingExist) {
							logger.debug("Mapping definition for ["+index+"]/["+type+"] succesfully merged.");
						} else {
							logger.debug("Mapping definition for ["+index+"]/["+type+"] succesfully created.");
						}
					}
				}
			} else {
				logger.warn("No mapping definition for ["+index+"]/["+type+"]. Ignoring.");
			}
		} else {
			if (logger.isDebugEnabled()){
                logger.debug("Mapping ["+index+"]/["+type+"] already exists and mergeMapping is not set.");
            }
		}
		if (logger.isTraceEnabled()){
            logger.trace("/pushMapping("+index+","+type+","+force+")");
        }
	}

	/**
	 * Create a new index in Elasticsearch
	 * @param index Index name
	 * @throws Exception
	 */
	private void createIndex(String index) throws Exception {
		if (logger.isTraceEnabled()){
            logger.trace("createIndex("+index+")");
        }
		if (logger.isDebugEnabled()){
            logger.debug("Index " + index + " doesn't exist. Creating it.");
        }
		
		checkClient();
		
		CreateIndexRequestBuilder cirb = client.admin().indices().prepareCreate(index);

		// If there are settings for this index, we use it. If not, using Elasticsearch defaults.
		String source = readIndexSettings(index);
		if (source != null) {
			if (logger.isTraceEnabled()){
                logger.trace("Found settings for index "+index+" : " + source);
            }
			cirb.setSettings(source);
		}
		
		CreateIndexResponse createIndexResponse = cirb.execute().actionGet();
		if (!createIndexResponse.isAcknowledged()){
            throw new Exception("Could not create index ["+index+"].");
        }
		if (logger.isTraceEnabled()){
            logger.trace("/createIndex("+index+")");
        }

	}

	/**
	 * Create a new index in ElasticSearch
	 * @param index Index name
	 * @throws Exception
	 */
	private void mergeIndexSettings(String index) throws Exception {
		if (logger.isTraceEnabled()){
			logger.trace("mergeIndexSettings("+index+")");
		}
		if (logger.isDebugEnabled()){
			logger.debug("Index " + index + " already exists. Trying to merge settings.");
		}
		
		checkClient();

        // If there are settings for this index, we use it. If not, using Elasticsearch defaults.
        String source = readUpdateIndexSettings(index);
        if (source != null) {
            if (logger.isTraceEnabled()){
            	logger.trace("Found settings for index "+index+" : " + source);
            }

            CloseIndexResponse closeIndexResponse = client.admin().indices().prepareClose(index).execute().actionGet();
            if (!closeIndexResponse.isAcknowledged()){
            	throw new Exception("Could not close index ["+index+"].");
            }

            client.admin().indices().prepareUpdateSettings(index).setSettings(source).execute().actionGet();

            OpenIndexResponse openIndexResponse = client.admin().indices().prepareOpen(index).execute().actionGet();
            if (!openIndexResponse.isAcknowledged()){
            	throw new Exception("Could not open index ["+index+"].");
            }
		}
		

		if (logger.isTraceEnabled()){
			logger.trace("/mergeIndexSettings("+index+")");
		}
	}

	/**
	 * Read the mapping for a type.<br>
	 * Shortcut to readFileInClasspath(classpathRoot + "/" + index + "/" + mapping + jsonFileExtension);
	 * @param index Index name
	 * @param type Type name
	 * @return Mapping if exists. Null otherwise.
	 * @throws Exception
	 */
	private String readMapping(String index, String type) throws Exception {
		return readFileInClasspath(this.config.getClassPathRoot() + "/" + index + "/" + type + jsonFileExtension);
	}	

	/**
	 * Read the template.<br>
	 * Shortcut to readFileInClasspath(classpathRoot + "/" + templateDir + "/" + template + jsonFileExtension);
	 * 
	 * @param template Template name
	 * @return Template if exists. Null otherwise.
	 * @throws Exception
	 */
	private String readTemplate(String template) throws Exception {
		return readFileInClasspath(this.config.getClassPathRoot() + "/" + templateDir + "/" + template + jsonFileExtension);
	}
	
	/**
	 * Read settings for an index.<br>
	 * Shortcut to readFileInClasspath(classpathRoot + "/" + index + "/" + indexSettingsFileName);
	 * @param index Index name
	 * @return Settings if exists. Null otherwise.
	 * @throws Exception
	 */
	public String readIndexSettings(String index) throws Exception {
		return readFileInClasspath(this.config.getClassPathRoot() + "/" + index + "/" + indexSettingsFileName);
	}

    /**
     * Read updatable settings for an index.<br>
     * Shortcut to readFileInClasspath(classpathRoot + "/" + index + "/" + updateIndexSettingsFileName);
     * @param index Index name
     * @return Settings if exists. Null otherwise.
     * @throws Exception
     */
    public String readUpdateIndexSettings(String index) throws Exception {
        return readFileInClasspath(this.config.getClassPathRoot() + "/" + index + "/" + updateIndexSettingsFileName);
    }

    /**
     * Read a file in classpath and return its content. If the file is not found, the error is logged, but null
     * is returned so that the user is aware of what happened.
     *
     * @param url File URL Example : /es/twitter/_settings.json
     * @return File content or null if file doesn't exist
     */
    public static String readFileInClasspath(String url) throws Exception {
        StringBuilder bufferJSON = new StringBuilder();

        BufferedReader br = null;
        logger.info("reading file: url=" + url);
        try {
            ClassPathResource classPathResource = new ClassPathResource(url);
            InputStreamReader ipsr = new InputStreamReader(classPathResource.getInputStream());
            br = new BufferedReader(ipsr);
            String line;

            while ((line = br.readLine()) != null) {
                bufferJSON.append(line);
            }
        } catch (Exception e) {
            logger.debug(String.format("Failed to load file from url: %s: %s", url, e.getMessage()));
            return null;
        } finally {
            if (br != null) br.close();
        }

        return bufferJSON.toString();
    }


}
