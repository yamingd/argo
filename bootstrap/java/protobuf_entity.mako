package {{_module_}};
option java_package = "com.{{_company_}}.{{_project_}}.protobuf.{{_module_}}";
option java_multiple_files = true;

{% for _tbi in _tbis %}
message P{{_tbi.entityName}} {
    {% for col in _tbi.columns %}
    optional {{col.protobuf_type}} {{col.name}} = {{ col.index + 1}};
    {% endfor %}
}
{% endfor %}