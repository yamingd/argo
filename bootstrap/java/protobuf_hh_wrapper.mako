//
// TS{{_tbi.entityName}}.hh
// {{_project_}}
//

#import <Foundation/Foundation.h>
#import "TSProtocolBufferWrapper.hh"

@interface TS{{_tbi.entityName}} : TSProtocolBufferWrapper

{% for col in _tbi.columns %}
// {{col.comment}}
@property (nonatomic,strong) {{col.ios_type}}* {{col.name}};
{% endfor %}

@end