//
//  TS{{_tbi.entityName}}Service.h
//  {{_Project_}}
//
//  Created by Yaming on {{now.strftime('%Y-%m-%d %H:%M')}}.
//  Copyright (c) {{now.strftime('%Y')}} {{_company_}}.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TS{{_tbi.entityName}}.hh"

@interface TS{{_tbi.entityName}}Service : TSServiceBase

+(void)findAll:(int)page block:(void (^)(id response, NSError* error))block;

+(void)add:(TS{{_tbi.entityName}}*)item block:(void (^)(id response, NSError* error))block;

+(void)remove:(TS{{_tbi.entityName}}*)item block:(void (^)(id response, NSError* error))block;

+(void)save:(TS{{_tbi.entityName}}*)item block:(void (^)(id response, NSError* error))block;

+(void)findById:(int)itemId block:(void (^)(id response, NSError* error))block;

+(void)findBys:(NSMutableDictionary*)params page:(int)page block:(void (^)(id response, NSError* error))block;

@end
