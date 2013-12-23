package com.argo.core.json;

import com.argo.core.ContextConfig;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @author Roy Clarkson
 * @since 1.0
 */
public class GsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    protected Logger logger = LoggerFactory.getLogger(GsonHttpMessageConverter.class);

    private Gson gson;

    private Type type = null;

    private boolean prefixJson = false;
    private boolean serializeNulls = false;
    private String dateTimeFormat;
    /**
     * Construct a new {@code GsonHttpMessageConverter} with a default {@link Gson#Gson() Gson}.
     */
    public GsonHttpMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET));
    }

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     *
     * @param serializeNulls true to generate json for null values
     */
    public GsonHttpMessageConverter(boolean serializeNulls) {
        super(new MediaType("application", "json", DEFAULT_CHARSET));
        this.serializeNulls = serializeNulls;
    }

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     *
     * @param gson a customized {@link Gson#Gson() Gson}
     */
    public GsonHttpMessageConverter(Gson gson) {
        super(new MediaType("application", "json", DEFAULT_CHARSET));
        setGson(gson);
    }


    /**
     * Sets the {@code Gson} for this view. If not set, a default
     * {@link Gson#Gson() Gson} is used.
     * <p>Setting a custom-configured {@code Gson} is one way to take further control of the JSON serialization
     * process.
     * @throws IllegalArgumentException if gson is null
     */
    public void setGson(Gson gson) {
        Assert.notNull(gson, "'gson' must not be null");
        this.gson = gson;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    /**
     * Indicates whether the JSON output by this view should be prefixed with "{} &&". Default is false.
     * <p> Prefixing the JSON string in this manner is used to help prevent JSON Hijacking. The prefix renders the string
     * syntactically invalid as a script so that it cannot be hijacked. This prefix does not affect the evaluation of JSON,
     * but if JSON validation is performed on the string, the prefix would need to be ignored.
     */
    public void setPrefixJson(boolean prefixJson) {
        this.prefixJson = prefixJson;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        Reader json = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));

        try {
            Type typeOfT = getType();
            if (typeOfT != null) {
                return this.gson.fromJson(json, typeOfT);
            } else {
                return this.gson.fromJson(json, clazz);
            }
        } catch (JsonSyntaxException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        } catch (JsonIOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        } catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), getCharset(outputMessage.getHeaders()));

        try {
            if (this.prefixJson) {
                writer.append("{} && ");
            }
            Type typeOfSrc = getType();
            if (typeOfSrc != null) {
                this.gson.toJson(o, typeOfSrc, writer);
            } else {
                this.gson.toJson(o, writer);
            }
            writer.close();
        } catch(JsonIOException  ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }


    // helpers

    private Charset getCharset(HttpHeaders headers) {
        if (headers != null && headers.getContentType() != null
                && headers.getContentType().getCharSet() != null) {
            return headers.getContentType().getCharSet();
        }
        return DEFAULT_CHARSET;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.gson != null){
            return;
        }
        if (this.dateTimeFormat == null){
            this.dateTimeFormat = ContextConfig.DATE_TIME_FORMAT;
        }
        //创建GsonBuilder
        GsonBuilder builder = new GsonBuilder();
        //设置需要被排除的属性列表
        GsonPrefixExclusion gsonFilter = new GsonPrefixExclusion();
        builder.setExclusionStrategies(gsonFilter);
        builder.setDateFormat(this.dateTimeFormat);
        //创建Gson并进行转换
        Gson gson = builder.create();
        this.setGson(gson);
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }
}
