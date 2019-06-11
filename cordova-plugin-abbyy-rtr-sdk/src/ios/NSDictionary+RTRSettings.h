/// ABBYY® Mobile Capture © 2019 ABBYY Production LLC.
/// ABBYY is a registered trademark or a trademark of ABBYY Software Ltd.

#import <UIKit/UIKit.h>
#import <AbbyyRtrSDK/AbbyyRtrSDK.h>
#import "RTRPlugin.h"

NS_ASSUME_NONNULL_BEGIN

@interface NSDictionary (RTRSettings)

- (UIInterfaceOrientationMask)rtr_orientationMaskForKey:(NSString*)key;

- (RTRCoreAPIExportCompressionLevel)rtr_exportCompressionLevelForKey:(NSString*)key;

- (RTRCoreAPIPdfExportCompressionType)rtr_exportCompressionTypeForKey:(NSString*)key;

- (RTRImageCaptureEncodingType)rtr_exportTypeForKey:(NSString*)key;

- (RTRImageCaptureDestintationType)rtr_destinationTypeForKey:(NSString*)key;

@end

NS_ASSUME_NONNULL_END