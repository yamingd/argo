package com.argo.core.freemarker.directives;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.Map;


/**
 */
public class PutDirective implements TemplateDirectiveModel {
    public static final String PUT_DATA_PREFIX = PutDirective.class.getCanonicalName() + ".";
    public static final String PUT_BLOCK_NAME_PARAMETER = "block";
    public static final String PUT_TYPE_PARAMETER = "type";

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String blockName = BlockDirectiveUtils.getBlockName(env, params, PUT_BLOCK_NAME_PARAMETER);
        PutType putType = getPutType(params);
        String bodyResult = BlockDirectiveUtils.getBodyResult(body);

        env.setVariable(BlockDirectiveUtils.getBlockContentsVarName(blockName), new SimpleScalar(bodyResult));
        env.setVariable(BlockDirectiveUtils.getBlockTypeVarName(blockName), new SimpleScalar(putType.name()));
    }

    private PutType getPutType(Map params) {
        SimpleScalar putTypeScalar = (SimpleScalar) params.get(PUT_TYPE_PARAMETER);
        PutType putType = null;
        if (putTypeScalar != null) {
            putType = PutType.valueOf(putTypeScalar.getAsString().toUpperCase());
        }

        if (putType == null) {
            putType = PutType.APPEND;
        }
        return putType;
    }
}