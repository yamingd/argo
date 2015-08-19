package com.argo.core.protobuf;

import com.argo.core.web.Enums;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufHttpMessageConverter extends AbstractHttpMessageConverter<Message> {

    private static Logger log = LoggerFactory.getLogger(ProtobufHttpMessageConverter.class);

    public static String LS = System.getProperty("line.separator");

    private ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();

    private static final ConcurrentHashMap<Class<?>, Method> newBuilderMethodCache =
            new ConcurrentHashMap<Class<?>, Method>();

    public static final String securityTag = "protobuf-encrypt";

    public ProtobufHttpMessageConverter() {
        this(null);
    }

    public ProtobufHttpMessageConverter(ExtensionRegistryInitializer registryInitializer) {
        super(Enums.PROTOBUF);
        initializeExtentionRegistry(registryInitializer);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    @Override
    protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        try {
            Method m = getNewBuilderMessageMethod(clazz);
            Message.Builder builder = (Message.Builder) m.invoke(clazz);
            InputStream is = inputMessage.getBody();
            builder.mergeFrom(is, extensionRegistry);
            Message msg = builder.build();
            if (logger.isDebugEnabled()){
                logger.debug("read: " + msg);
            }
            return msg;
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Unable to convert inputMessage to Proto object", e);
        }
    }

    @Override
    protected void writeInternal(Message message, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
//        if (logger.isDebugEnabled()) {
//            logger.debug(message);
//        }

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
        Object xsecurity = request.getAttribute(securityTag);
        //logger.debug("securityTag: " + xsecurity+", request:" + request);
        byte[] bytes = message.toByteArray();
        if (xsecurity != null){
            //加密数据
            int len = (Integer)xsecurity;
            if (len > 0) {

                Random random = new Random();
                byte[] arr = new byte[len];
                random.nextBytes(arr);

                outputMessage.getBody().write(arr);
                logger.debug("securityTag: " + xsecurity +", arr:" + arr);
            }
        }

        FileCopyUtils.copy(bytes, outputMessage.getBody());

    }

    @Override
    protected MediaType getDefaultContentType(Message message) {
        return Enums.PROTOBUF;
    }

    @Override
    protected Long getContentLength(Message message, MediaType contentType) {
        return (long) message.toByteArray().length;
    }

    private Method getNewBuilderMessageMethod(Class<? extends Message> clazz) throws NoSuchMethodException {
        Method m = newBuilderMethodCache.get(clazz);
        if (m == null) {
            m = clazz.getMethod("newBuilder");
            newBuilderMethodCache.put(clazz, m);
        }
        return m;
    }

    private void initializeExtentionRegistry(ExtensionRegistryInitializer registryInitializer) {
        if (registryInitializer != null) {
            registryInitializer.initializeExtensionRegistry(extensionRegistry);
        }
    }

    private String convertInputStreamToString(InputStream io) {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(io));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line).append(LS);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to obtain an InputStream", e);

        }
        return sb.toString();
    }

    private void setProtoHeader(final HttpOutputMessage response, final Message message) {
        String protoFileName;
        String protoMessageName;
        try {
            protoFileName = message.getDescriptorForType().getFile().getName();
            response.getHeaders().set(Enums.X_PROTOBUF_SCHEMA_HEADER, protoFileName);

            protoMessageName = message.getDescriptorForType().getFullName();
            response.getHeaders().set(Enums.X_PROTOBUF_MESSAGE_HEADER, protoMessageName);
        } catch (NullPointerException e) {
            // Shouldn't happen in running environment
            // Catching and ignoring it doesn't damage the response
            log.error("Exception caught while trying to set the ProtoHeader", e);
        }
    }
}
