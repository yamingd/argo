//
// TS{{_tbi.entityName}}.mm
// {{_project_}}
//

#import "TS{{_tbi.entityName}}.hh"
#import "{{_moduleC_}}Proto.pb.hh"

@interface TS{{_tbi.entityName}} ()

@property (nonatomic, strong)NSData* protocolData;

@end

@implementation TS{{_tbi.entityName}}

-(instancetype) initWithProtocolData:(NSData*) data {
    return [self initWithData:data];
}
 
-(NSData*) getProtocolData {
    return self.protocolData;
}

-(instancetype) initWithData:(NSData*) data {
    
    if(self = [super init]) {
        // c++
        {{_module_}}::P{{_tbi.entityName}}* pitem = [self deserialize:data];
        //
        {% for col in _tbi.columns %}
if(pitem->has_{{col.protobuf_name}}()){
            const {{col.cpp_type}} {{col.name}} = pitem->{{col.protobuf_name}}();
            self.{{col.name}} = [self {{col.cpp_objc}}:{{col.name}}];
        }
        {% endfor %}

        // c++->objective C
        self.protocolData = data;
    }
    return self;
}

-(NSMutableDictionary*)asDict{
    NSMutableDictionary* ret = [[NSMutableDictionary alloc] init];
    {% for col in _tbi.columns %}
[ret setObject:self.{{col.name}} forKey:@"{{col.name}}"];
    {% endfor %}
return ret;
}

#pragma mark private
 
-(const std::string) serializedProtocolBufferAsString {
    {{_module_}}::P{{_tbi.entityName}} *pmsg = new {{_module_}}::P{{_tbi.entityName}};
    // objective c->c++
    // 
    {% for col in _tbi.columns %}
    const {{col.cpp_type}} {{col.name}} = [self {{col.objc_cpp}}:self.{{col.name}}];
    {% endfor %}

    // c++->protocol buffer
    {% for col in _tbi.columns %}
    pmsg->set_{{col.protobuf_name}}({{col.name}});
    {% endfor %}
    
    std::string ps = pmsg->SerializeAsString();
    return ps;
}
 
#pragma mark private methods
- ({{_module_}}::P{{_tbi.entityName}} *)deserialize:(NSData *)data {
    int len = [data length];
    char raw[len];
    {{_module_}}::P{{_tbi.entityName}} *resp = new {{_module_}}::P{{_tbi.entityName}};
    [data getBytes:raw length:len];
    resp->ParseFromArray(raw, len);
    return resp;
}

@end