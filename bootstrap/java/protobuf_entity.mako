{% for m in _ms_ %}
import "{{m | capitalize}}Proto.proto";
{% endfor %}
package {{_module_}};
option java_package = "com.{{_company_}}.{{_project_}}.protobuf.{{_module_}}";
option java_multiple_files = true;

{% for _tbi in _tbis %}
message P{{_tbi.entityName}} {
    {% for col in _tbi.columns %}
    optional {{col.protobuf_type}} {{col.name}} = {{ col.index + 1}};
    {% endfor %}

    {% set count = _tbi.columns | length %}
    {% for col in _tbi.refs %}
{{col.ref_type}} {{_tbm_[col.ref_obj.name]}}.P{{col.ref_obj.entityName}} {{col.ref_varName}} = {{ count + 1}};
    {% set count = count +1 %}
    {% endfor %}
}


{% endfor %}