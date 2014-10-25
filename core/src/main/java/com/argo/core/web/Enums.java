package com.argo.core.web;

import org.springframework.http.MediaType;

import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/3
 * Time: 12:42
 */
public class Enums {

    public final static String APPLICATION_BJSON_VALUE = "application/bson";

    public final static MediaType APPLICATION_BJSON = MediaType.valueOf(APPLICATION_BJSON_VALUE);

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);
    public static final String PROTOBUF_VALUE = "application/x-protobuf";
    public static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
    public static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";
}
