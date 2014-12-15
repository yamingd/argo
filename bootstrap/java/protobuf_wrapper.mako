package com.{{_company_}}.{{_project_}}.protobuf.{{_module_}};

import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_tbi.entityName}};

public class P{{_tbi.entityName}}Wrapper{
	
	public static P{{_tbi.entityName}} fromEntity({{_tbi.entityName}} item){
	    P{{_tbi.entityName}}.Builder builder = P{{_tbi.entityName}}.newBuilder();
{% for col in _tbi.columns %}
		if(item.get{{col.capName}}()!=null){
	    	builder.set{{col.capName}}(item.get{{col.capName}}(){{col.protobuf_value}});
	    }
{% endfor %}

{% for col in _tbi.refs %}
        //TODO: GET {{col.ref_obj.entityName}}
{% endfor %}

		return builder.build();
	}
}