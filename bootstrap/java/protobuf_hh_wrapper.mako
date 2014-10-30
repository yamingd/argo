//
// TS{{_tbi.entityName}}.hh
// {{_project_}}
//

#import <Foundation/Foundation.h>
#import <Realm/Realm.h>
#import "NSObject+Protobuf.hh"

@interface TS{{_tbi.entityName}} : RLMObject

{% for col in _tbi.columns %}
// {{col.comment}}
@property {{col.ios_type}}* {{col.name}};
{% endfor %}

-(NSMutableDictionary*)asDict;

@end

RLM_ARRAY_TYPE(TS{{_tbi.entityName}})