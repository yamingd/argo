package com.{{_company_}}.{{_module_}};

import com.argo.core.base.BaseEntity;
import java.util.Date;

/**
 * Created by $User on {{now.strftime('%Y-%m-%d %H:%M')}}.
 */
public class {{_entity_}} extends BaseEntity {
    
    {% for col in _cols_ %}
    /**
     * {{col.comment}}
     * {{col.defaultTips}}
     */
    private {{col.java_type}} {{col.name}};
    {% endfor %}

    @Override
    public String getPK() {
        return null;
    }

    {% for col in _cols_ %}
    /**
     * {{col.comment}}
     * {{col.defaultTips}}
     */
    public {{col.java_type}} get{{ col.capName }}(){
        return this.{{ col.name }};
    }
    public void set{{col.capName}}({{col.java_type}} {{col.name}}){
        this.{{col.name}} = {{col.name}};
    }
    {% endfor %}
}