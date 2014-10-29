package com.{{_company_}}.{{_project_}}.{{_module_}};

import com.argo.core.base.BaseEntity;
import com.argo.core.annotation.EntityDef;
import com.argo.core.annotation.PK;
import java.util.Date;

/**
 * {{ _tbi_.hint }}
 * Created by $User on {{now.strftime('%Y-%m-%d %H:%M')}}.
 */
@EntityDef(table = "{{ _tbi_.name }}")
public class {{_tbi_.entityName}} extends BaseEntity {
    
    {% for col in _cols_ %}
/**
     * {{col.comment}}
     * {{col.typeName}} {{col.defaultTips}}
     */
    {{col.pkMark}}private {{col.java_type}} {{col.name}};
    {% endfor %}

    @Override
    public String getPK() {
        return {% if _pks_ %}{% for pk in _pks_ %} ":" + {{pk.name}}{% endfor %} {% else %}null{% endif %};
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