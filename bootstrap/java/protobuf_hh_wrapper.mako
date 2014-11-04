//
// TS{{_tbi.entityName}}.hh
// {{_project_}}
//

#import <Foundation/Foundation.h>
#import <Realm/Realm.h>
#import "PBObjcWrapper.hh"
{% for col in _tbi.refImport() %}
#import "TS{{col}}.hh"
{% endfor %}

@interface TS{{_tbi.entityName}} : RLMObject<PBObjcWrapper>

{% for col in _tbi.columns %}
// {{col.comment}}
@property {{col.ios_type_ref}} {{col.name}};

{% endfor %}

{% for col in _tbi.refs %}
@property {{col.ref_obj.protobufRefAs(col.ref_type)}} {{col.ref_varName}};
{% endfor %}

@end

RLM_ARRAY_TYPE(TS{{_tbi.entityName}})