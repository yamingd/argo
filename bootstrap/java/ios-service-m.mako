//
//  TS{{_tbi.entityName}}Service.m
//  {{_Project_}}
//
//  Created by Yaming on {{now.strftime('%Y-%m-%d %H:%M')}}.
//  Copyright (c) {{now.strftime('%Y')}} {{_company_}}.com. All rights reserved.
//

#import "TS{{_tbi.entityName}}Service.h"

@implementation TS{{_tbi.entityName}}Service

+(void)findAll:(int)page block:(void (^)(id response, NSError* error))block{
    NSString* url = [NSString stringWithFormat:@"/m/{{_tbi.mvc_url()}}/all/%d", page];
    [[APIClient sharedClient] query:url params:nil block:^(TSAppResponse* response, NSError *error) {
        if (!error && response.code == 200) {
            [response parseData:[TS{{_tbi.entityName}} class]];
        }
        block(response, error);
    }];
}

+(void)add:(TS{{_tbi.entityName}}*)item block:(void (^)(id response, NSError* error))block{
    NSString* url = @"/m/{{_tbi.mvc_url()}}/create";
    NSDictionary* params = [item asDict];
    [[APIClient sharedClient] postForm:url params:params block:^(TSAppResponse* response, NSError *error) {
        if (!error && response.code == 200) {
            [response parseData:[TS{{_tbi.entityName}} class]];
            block([response.data objectAtIndex:0], nil);
        }else{
            block(response, error);
        }
    }];
}

+(void)remove:(TS{{_tbi.entityName}}*)item block:(void (^)(id response, NSError* error))block{
    NSString* url = [NSString stringWithFormat:@"/m/{{_tbi.mvc_url()}}/remove/%d", item.id];
    [[APIClient sharedClient] postForm:url params:nil block:^(TSAppResponse* response, NSError *error) {
        block(response, error);
    }];
}

+(void)save:(TS{{_tbi.entityName}}*)item block:(void (^)(id response, NSError* error))block{
    NSString* url = [NSString stringWithFormat:@"/m/{{_tbi.mvc_url()}}/save/%d", item.id];
    NSDictionary* params = [item asDict];
    [[APIClient sharedClient] postForm:url params:params block:^(TSAppResponse* response, NSError *error) {
        if (!error && response.code == 200) {
            [response parseData:[TS{{_tbi.entityName}} class]];
            block([response.data objectAtIndex:0], nil);
        }else{
            block(response, error);
        }
    }];
}

+(void)findById:(int)itemId block:(void (^)(id response, NSError* error))block{
    NSString* url = [NSString stringWithFormat:@"/m/{{_tbi.mvc_url()}}/view/%d", itemId];
    [[APIClient sharedClient] query:url params:nil block:^(TSAppResponse* response, NSError *error) {
        if (!error && response.code == 200) {
            [response parseData:[TS{{_tbi.entityName}} class]];
            block([response.data objectAtIndex:0], nil);
        }else{
            block(response, error);
        }
    }];
}

+(void)findBys:(NSMutableDictionary*)params page:(int)page block:(void (^)(id response, NSError* error))block{
    NSString* url = [NSString stringWithFormat:@"/m/{{_tbi.mvc_url()}}/find/%d", page];
    [[APIClient sharedClient] query:url params:params block:^(TSAppResponse* response, NSError *error) {
        if (!error && response.code == 200) {
            [response parseData:[TS{{_tbi.entityName}} class]];
        }
        block(response, error);
    }];
}

@end
