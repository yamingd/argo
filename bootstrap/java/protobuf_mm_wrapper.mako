//
// TS{{_tbi.entityName}}.mm
// {{_project_}}
//

#import "TS{{_tbi.entityName}}.hh"
#import "P{{_tbi.entityName}}.pb.hh"

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
        {{_module_}}::P{{_tbi.entityName}}* item = [self deserialize:data];
        //TODO:
        
        // c++->objective C
        self.protocolData = data;
        //TODO:
        
    }
    return self;
}

#pragma mark private
 
-(const std::string) serializedProtocolBufferAsString {
    {{_module_}}::P{{_tbi.entityName}} *message = new {{_module_}}::P{{_tbi.entityName}};
    // objective c->c++
    // TODO
    
    // c++->protocol buffer
    // TODO
    
    std::string ps = message->SerializeAsString();
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