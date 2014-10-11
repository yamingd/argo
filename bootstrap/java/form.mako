package com.{{_company_}}.{{_project_}}.web.controllers.{{_module_}};

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class {{_tbi_.entityName}}Form {
    
    {% for col in _cols_ %}
    /**
     * {{col.comment}}
     * {{col.typeName}}
     */
    {{col.validate}}private {{col.java_type}} {{col.name}};
    {% endfor %}

    {% for col in _cols_ %}
    /**
     * {{col.comment}}
     */
    public {{col.java_type}} get{{ col.capName }}(){
        return this.{{ col.name }};
    }
    public void set{{col.capName}}({{col.java_type}} {{col.name}}){
        this.{{col.name}} = {{col.name}};
    }
    {% endfor %}
}