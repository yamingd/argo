package com.argo.core.json;

import com.google.gson.JsonIOException;
import org.msgpack.MessagePack;
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

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @since 1.0
 */
public class XJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements InitializingBean {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    protected Logger logger = LoggerFactory.getLogger(XJsonHttpMessageConverter.class);

    private MessagePack messagePack;

    private boolean prefixJson = false;
    private boolean serializeNulls = false;

    /**
     * Construct a new {@code GsonHttpMessageConverter} with a default {@link com.google.gson.Gson#Gson() Gson}.
     */
    public XJsonHttpMessageConverter() {
        super(new MediaType("application", "x-json", DEFAULT_CHARSET));
    }

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     *
     * @param serializeNulls true to generate json for null values
     */
    public XJsonHttpMessageConverter(boolean serializeNulls) {
        super(new MediaType("application", "x-json", DEFAULT_CHARSET));
        this.serializeNulls = serializeNulls;
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

        Object o = messagePack.read(inputMessage.getBody(), clazz);
        return o;
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        try {
            messagePack.write(outputMessage.getBody(), o);
            outputMessage.getBody().flush();
            outputMessage.getBody().close();
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
        this.messagePack = new MessagePack();
    }

}
