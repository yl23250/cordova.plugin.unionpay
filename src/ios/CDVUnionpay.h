
#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import "UPPayPlugin.h"

@interface CDVUnionpay : CDVPlugin<UPPayPluginDelegate> {
   
   
}

@property(nonatomic,strong)NSString *currentCallbackId;
@property(nonatomic,strong)NSString *mMode;

- (void)pay:(CDVInvokedUrlCommand*)command;

- (void)UPPayPluginResult:(NSString *)result;

@end
