// ABBYY® Mobile Capture © 2019 ABBYY Production LLC.
// ABBYY is a registered trademark or a trademark of ABBYY Software Ltd.

#import <UIKit/UIKit.h>
#import <AbbyyRtrSDK/AbbyyRtrSDK.h>

NS_ASSUME_NONNULL_BEGIN

@class RTRDocumentManager;

@interface RTRMultipageScenarioStorage : NSObject

@property (nonatomic, readonly) NSInteger imagesCount;
@property (nonatomic, readonly) NSInteger currentImageIndex;
@property (nonatomic, readonly) RTREngine* engine;
@property (nonatomic, readonly) RTRDocumentManager* manager;

- (instancetype)initWithEngine:(RTREngine*)engine manager:(RTRDocumentManager*)manager;
- (instancetype)init __unavailable;

- (UIImage*)imageForIndex:(NSInteger)index;
- (void)imageCaptured:(UIImage*)image;
- (void)deleteImageAt:(NSInteger)index;
- (void)retakeImageAt:(NSInteger)index;
- (void)sessionClosedSuccessfully;
- (void)confirmChanges;
- (void)sessionCancelled;

@end

NS_ASSUME_NONNULL_END