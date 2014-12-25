//
// TS{{_tbi.entityName}}.mm
// {{_project_}}
//

#import "TS{{_tbi.entityName}}.hh"
#import "{{_moduleC_}}Proto.pb.hh"
#import "PBObjc.hh"

@interface TS{{_tbi.entityName}} ()

@property (nonatomic, strong)NSData* protocolData;

@end

@implementation TS{{_tbi.entityName}}

-(instancetype) initWithProtocolData:(NSData*) data {
    return [self initWithData:data];
}
-(instancetype) initWithProtocolObj:(google::protobuf::Message*) pbmsg {
    // c++
    {{_module_}}::P{{_tbi.entityName}}* pbobj = ({{_module_}}::P{{_tbi.entityName}}*)pbmsg;
    //
{% for col in _tbi.columns %}
    if(pbobj->has_{{col.protobuf_name}}()){
        const {{col.cpp_type}} {{col.name}} = pbobj->{{col.protobuf_name}}();
        self.{{col.name}} = [PBObjc {{col.cpp_objc}}:{{col.name}}];
    }
{% endfor %}
    //
{% for col in _tbi.refs %}
{% if col.ref_type == 'repeated' %}
    for(int i=0; i<pbobj->{{col.ref_varName}}_size();i++){
        {{_tbm_[col.ref_obj.name]}}::P{{col.ref_obj.entityName}}* pbref = pbobj->mutable_{{col.ref_varName | lower}}(i);
        [self.{{col.ref_varName}} addObject:[[TS{{col.ref_obj.entityName}} alloc] initWithProtocolObj: pbref]];
    }
{% else %}
    if(pbobj->has_{{col.ref_varName | lower}}()){
        {{_tbm_[col.ref_obj.name]}}::P{{col.ref_obj.entityName}}* pbref = pbobj->mutable_{{col.ref_varName | lower}}();
        self.{{col.ref_varName}} = [[TS{{col.ref_obj.entityName}} alloc] initWithProtocolObj: pbref];
    }
{% endif %}
{% endfor %}
    //
    return self;
} 
-(NSData*) getProtocolData {
    return self.protocolData;
}

-(instancetype) initWithData:(NSData*) data {
    
    if(self = [super init]) {
        // c++
        {{_module_}}::P{{_tbi.entityName}}* pbmsg = [self deserialize:data];
        //
        self = [self initWithProtocolObj:pbmsg];
        // c++->objective C
        self.protocolData = data;
    }
    return self;
}

-(NSMutableDictionary*)asDict{
    NSMutableDictionary* ret = [[NSMutableDictionary alloc] init];
    {% for col in _tbi.columns %}
[ret setObject:{{col.ios_value}} forKey:@"{{col.name}}"];
    {% endfor %}
return ret;
}

{% if _tbi.pkCol %}
+ (NSString*)primaryKey{
    return @"{{_tbi.pkCol.name}}";
}
{% endif %}
+ (NSArray*)ignoredProperties{
    return @[@"protocolData"];
}

#pragma mark private
 
-(const std::string) serializedProtocolBufferAsString {
    {{_module_}}::P{{_tbi.entityName}} *pmsg = new {{_module_}}::P{{_tbi.entityName}};
    // objective c->c++
    // 
    {% for col in _tbi.columns %}
    const {{col.cpp_type}} {{col.name}} = [PBObjc {{col.objc_cpp}}:self.{{col.name}}];
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