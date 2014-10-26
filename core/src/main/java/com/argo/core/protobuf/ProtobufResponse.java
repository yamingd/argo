package com.argo.core.protobuf;

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
        builder.setCode(200).setTotal(0);
    }

    public ProtobufMessage build() {
        return builder.build();
    }

    public ProtobufMessage.Builder getBuilder() {
        return builder;
    }
}
