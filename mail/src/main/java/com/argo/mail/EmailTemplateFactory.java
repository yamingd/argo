package com.argo.mail;

import com.argo.core.base.BaseBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/12/22
 * Time: 21:54
 */
@Component
public class EmailTemplateFactory extends BaseBean{

    private Map<Integer, EmailTemplate> mapping = new HashMap<Integer, EmailTemplate>();

    public static EmailTemplateFactory instance = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        instance = this;
    }

    /**
     * 添加模板
     * @param template
     */
    public void add(EmailTemplate template){
        mapping.put(template.getId(), template);
    }

    /**
     * 根据模板id
     * @param templateId
     * @return
     */
    public EmailTemplate find(Integer templateId){
        return mapping.get(templateId);
    }

}
