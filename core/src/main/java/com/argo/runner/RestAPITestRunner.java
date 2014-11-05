package com.argo.runner;

import com.argo.core.ContextConfig;
import com.argo.core.configuration.SiteConfig;
import com.argo.core.json.JsonUtil;
import com.argo.core.protobuf.ProtobufMessage;
import com.argo.core.utils.TokenUtil;
import com.argo.core.web.BsonResponse;
import com.argo.core.web.JsonResponse;
import com.argo.core.web.session.SessionCookieHolder;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/5
 * Time: 23:44
 */
public class RestAPITestRunner {

    public static Logger logger = LoggerFactory.getLogger(RestAPITestRunner.class);
    public static String domain;

    static {
        try {
            System.setProperty(ContextConfig.RUNNING_ENV, "test");
            new SiteConfig().afterPropertiesSet();
            Map<String, Object> app = SiteConfig.instance.get(Map.class, "app");
            domain = app.get("domain") + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    private static URI createURI(String url, Map<String, String> params) throws Exception {
        url = domain + url;
        if (params == null || params.size() == 0) {
            return new URI(url);
        }
        URIBuilder builder = new URIBuilder(url);
        for (String key : params.keySet()) {
            builder.addParameter(key, params.get(key));
        }
        return builder.build();
    }

    /**
     *
     * @param method
     * @param url
     * @param args
     * @return
     * @throws Exception
     */
    private static HttpUriRequest createRequest(String method, String url, Map<String, String> args) throws Exception {
        HttpUriRequest request = null;
        if (method.equals(HttpGet.METHOD_NAME)) {
            request = new HttpGet(createURI(url, args));
        } else if (method.equals(HttpPost.METHOD_NAME)) {
            HttpPost httpPost = new HttpPost(createURI(url, null));
            if (args != null && args.size() > 0) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                HttpEntity entity;
                for (String key : args.keySet()) {
                    String o = args.get(key);
                    formParams.add(new BasicNameValuePair(key, o));
                }
                entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                httpPost.setEntity(entity);
            }
            request = httpPost;
        }
        if (request != null){
            prepareSession(request);
        }
        return request;
    }

    /**
     *
     * @param url
     * @param args
     * @param files
     * @return
     */
    private static HttpUriRequest createFileRequest(String url, Map<String, String> args, Map<String, File> files) throws Exception {
        HttpUriRequest request = null;
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        if (args != null && args.size() > 0) {
            for (String key : args.keySet()) {
                entity.addPart(key, new StringBody(args.get(key), ContentType.TEXT_PLAIN.withCharset(Consts.UTF_8)));
            }
        }
        if (files != null && files.size() > 0) {
            for (String key : files.keySet()) {
                entity.addPart(key, new FileBody(files.get(key)));
            }
        }
        HttpPost httpPost = new HttpPost(createURI(url, null));
        httpPost.setEntity(entity.build());
        request = httpPost;
        return request;
    }

    private static byte[] consumeAsBytes(HttpResponse response) {
        HttpEntity entity = null;
        InputStream stream = null;
        try {
            entity = response.getEntity();
            stream = entity.getContent();
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (stream == null){
            return null;
        }

        try {
            byte[] bytes = IOUtils.toByteArray(stream);
            return bytes;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     *
     * @param response
     * @return
     */
    private static String consumeResponse(HttpResponse response) {
        HttpEntity entity = null;
        InputStream stream = null;
        try {
            entity = response.getEntity();
            stream = entity.getContent();
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (stream == null){
            return null;
        }

        StringBuffer document = new StringBuffer();
        String line = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, Consts.UTF_8));
            while ((line = reader.readLine()) != null) {
                document.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return document.toString();
    }

    protected static void prepareSession(HttpUriRequest request) {
        String cookieId = SessionCookieHolder.getAuthCookieId();
        String userId = getCurrentUserId();
        try {
            if (userId != null) {
                String signed = TokenUtil.createSignedValue(cookieId, userId);
                if (isMobile()) {
                    request.setHeader(cookieId, signed);
                } else {
                    request.setHeader("Cookie", cookieId + "=" + signed);
                }
            }
            configHttpHeader(request);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected static void configHttpHeader(HttpUriRequest request){
        //TODO: implement this in subclass
    }

    protected static String getCurrentUserId(){
        //TODO: implement this in subclass
        return null;
    }

    protected static boolean isMobile(){
        //TODO: implement this in subclass
        return false;
    }



    /**
     *
     * @param url
     * @param args
     * @return
     * @throws Exception
     */
    protected String getUrlView(String url, Map<String, String> args) throws Exception {
        HttpUriRequest request = createRequest(HttpGet.METHOD_NAME, url, args);
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

        String body = consumeResponse(httpResponse);
        return body;
    }

    /**
     *
     * @param url
     * @param args
     * @return
     * @throws Exception
     */
    protected JsonResponse getJson(String url, Map<String, String> args) throws Exception {
        HttpUriRequest request = createRequest(HttpGet.METHOD_NAME, url, args);
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

        String body = consumeResponse(httpResponse);
        logger.info(body);
        return JsonUtil.asT(JsonResponse.class, body);
    }

    /**
     *
     * @param url
     * @param args
     * @return
     */
    protected BsonResponse getBson(String url, Map<String, String> args) throws Exception {
        HttpUriRequest request = createRequest(HttpGet.METHOD_NAME, url, args);
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

        byte[] body = consumeAsBytes(httpResponse);
        logger.info("body length: " + body.length);
        return JsonUtil.asT(BsonResponse.class, body);
    }

    protected ProtobufMessage getProtobuf(String url, Map<String, String> args) throws Exception {
        HttpUriRequest request = createRequest(HttpGet.METHOD_NAME, url, args);
        request.setHeader("Accept", "application/x-protobuf");
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

        byte[] body = consumeAsBytes(httpResponse);
        logger.info("body length: " + body.length);
        return ProtobufMessage.parseFrom(body);
    }

    /**
     *
     * @param url
     * @param args
     * @return
     * @throws Exception
     */
    protected JsonResponse postForm(String url, Map<String, Object> args) throws Exception {
        Map<String, File> files = new HashMap<String, File>();
        Map<String, String> params = new HashMap<String, String>();
        for (String name : args.keySet()){
            Object v = args.get(name);
            if (v instanceof File){
                files.put(name, (File)v);
            }else{
                params.put(name, v + "");
            }
        }
        if (files.size() > 0){
            HttpUriRequest request = createFileRequest(url, params, files);
            // When
            HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

            assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

            String body = consumeResponse(httpResponse);
            logger.info(body);
            return JsonUtil.asT(JsonResponse.class, body);

        }else{

            HttpUriRequest request = createRequest(HttpPost.METHOD_NAME, url, params);
            // When
            HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

            assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

            String body = consumeResponse(httpResponse);
            logger.info(body);
            return JsonUtil.asT(JsonResponse.class, body);
        }
    }

    /**
     *
     * @param url
     * @param args
     * @return
     */
    protected ProtobufMessage postFormProtobuf(String url, Map<String, Object> args) throws Exception {
        Map<String, File> files = new HashMap<String, File>();
        Map<String, String> params = new HashMap<String, String>();
        for (String name : args.keySet()){
            Object v = args.get(name);
            if (v instanceof File){
                files.put(name, (File)v);
            }else{
                params.put(name, v + "");
            }
        }
        if (files.size() > 0){
            HttpUriRequest request = createFileRequest(url, params, files);
            // When
            HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

            assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

            byte[] body = consumeAsBytes(httpResponse);
            logger.info("body length: " + body.length);
            return ProtobufMessage.parseFrom(body);

        }else{

            HttpUriRequest request = createRequest(HttpPost.METHOD_NAME, url, params);
            // When
            HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

            assert httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

            byte[] body = consumeAsBytes(httpResponse);
            logger.info("body length: " + body.length);
            return ProtobufMessage.parseFrom(body);
        }
    }

}
