package com.argo.core.freemarker.directives;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;


/**
 */
public class BlockDirective implements TemplateDirectiveModel {

    public static final String BLOCK_NAME_PARAMETER = "name";

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        String blockName = BlockDirectiveUtils.getBlockName(env, params, BLOCK_NAME_PARAMETER);
        PutType putType = getPutType(env, blockName);
        String bodyResult = BlockDirectiveUtils.getBodyResult(body);

        Writer out = env.getOut();

        String putContents = getPutContents(env, blockName);

        putType.write(out, bodyResult, putContents);
    }

    private PutType getPutType(Environment env, String blockName) throws TemplateException {
        SimpleScalar putTypeScalar = (SimpleScalar) env.getVariable(BlockDirectiveUtils.getBlockTypeVarName(blockName));
        if (putTypeScalar == null) {
            return PutType.APPEND;
        }

        return PutType.valueOf(putTypeScalar.getAsString());
    }

    private String getPutContents(Environment env, String blockName) throws TemplateModelException {
        SimpleScalar putContentsModel = (SimpleScalar) env.getVariable(BlockDirectiveUtils.getBlockContentsVarName(blockName));
        String putContents = "";
        if (putContentsModel != null) {
            putContents = putContentsModel.getAsString();
        }
        return putContents;
    }
}
