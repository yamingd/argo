package com.argo.core.mobile;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/25
 * Time: 12:49
 */
public class ProtobufResponse {

    private ProtobufMessage.Builder builder;

    public ProtobufResponse() {
        builder = ProtobufMessage.newBuilder();
    }

    public ProtobufMessage getContent() {
        return builder.build();
    }

    public ProtobufMessage.Builder getBuilder() {
        return builder;
    }
}
