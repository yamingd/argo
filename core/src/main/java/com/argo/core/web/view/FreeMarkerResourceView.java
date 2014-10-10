package com.argo.core.web.view;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by yaming_deng on 2014/10/10.
 */
public class FreeMarkerResourceView extends FreeMarkerView {

    protected Logger logger = LoggerFactory.getLogger(FreeMarkerResourceView.class);

    @Override
    protected Template getTemplate(String name, Locale locale) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("locate template: " + name);
        }
        return super.getTemplate(name, locale);
    }
}
