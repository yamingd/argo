package com.{{_company_}}.{{_project_}}.{{_module_}};

{% for _entity_ in _entitys_ %}
import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_entity_}};
{% endfor %}

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by $User on {{now.strftime('%Y-%m-%d %H:%M')}}.
 */
public class {{_moduleC_}}Mappers {
	
{% for _entity_ in _entitys_ %}

    public static final RowMapper<{{_entity_}}> {{_entity_}}_ROWMAPPER = new BeanPropertyRowMapper<{{_entity_}}>(
            {{_entity_}}.class);

{% endfor %}
}
