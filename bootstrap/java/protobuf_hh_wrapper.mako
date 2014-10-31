//
// TS{{_tbi.entityName}}.hh
// {{_project_}}
//

#import <Foundation/Foundation.h>
#import <Realm/Realm.h>
#import "PBObjcWrapper.hh"

@interface TS{{_tbi.entityName}} : RLMObject<PBObjcWrapper>

{% for col in _tbi.columns %}
// {{col.comment}}
@property {{col.ios_type_ref}} {{col.name}};
{% endfor %}

@end

RLM_ARRAY_TYPE(TS{{_tbi.entityName}})