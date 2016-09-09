

#import "CDVUnionpay.h"
#import <Cordova/CDVViewController.h>
#import "UPPayPlugin.h"


@implementation CDVUnionpay



-(NSDictionary *)checkArgs:(CDVInvokedUrlCommand *) command{
    // check arguments
    NSDictionary *params = [command.arguments objectAtIndex:0];
    if (!params)
    {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"参数错误"] callbackId:command.callbackId];
        
        
        return nil;
    }
    return params;
}






-(void)pluginInitialize{
    CDVViewController *viewController = (CDVViewController *)self.viewController;
    self.mMode = [viewController.settings objectForKey:@"unionpay_mmode"];
}


- (void)pay:(CDVInvokedUrlCommand*)command{
    
    NSDictionary *params  = [self checkArgs:command];
    
    self.currentCallbackId = command.callbackId;
    
    @try {
        NSString *tn = params[@"tn"];
        [UPPayPlugin startPay:tn mode:self.mMode viewController:self.viewController delegate:self];
    }
    @catch (NSException *exception) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
    }
    @finally {
        
    }
}

- (void)UPPayPluginResult:(NSString *)result {
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
}

@end
