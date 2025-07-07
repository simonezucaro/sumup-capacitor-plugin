//
//  SMPSumUpSDK.h
//  SumUpSDK
//
//  Created by Felix Lamouroux on 29.01.14.
//  Copyright (c) 2014 SumUp Payments Limited. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SMPMerchant;
@class SMPCheckoutResult;
@class SMPCheckoutRequest;

NS_ASSUME_NONNULL_BEGIN

/// A common completion block used within the SumUpSDK that is called with a success flag and an optional error object.
typedef void (^SMPCompletionBlock)(BOOL success, NSError * _Nullable error);

/**
 *  The completion block type that will be used when calling checkoutWithRequest:fromViewController:completion:
 *
 *  @param result a SMPCheckoutResult that provides information about the checkout process
 *  @param error  an error object in case the checkout can not be performed
 */
typedef void (^SMPCheckoutCompletionBlock)(SMPCheckoutResult * _Nullable result, NSError * _Nullable error);

/// The SMPSumUpSDK class is your central interface with SumUp.
NS_SWIFT_NAME(SumUpSDK)
@interface SMPSumUpSDK : NSObject

/**
 *  YES if a merchant is logged in. NO otherwise.
 */
@property (class, readonly) BOOL isLoggedIn;

/// Returns a copy of the currently logged in merchant or nil if no merchant is logged in.
@property (class, readonly, nullable) SMPMerchant *currentMerchant;

/**
 *  YES if a checkout is in progress. NO otherwise.
 */
@property (class, readonly) BOOL checkoutInProgress;

/**
 *  Returns the SDK's CFBundleVersion
 */
@property(class, readonly) NSString *bundleVersion;

/**
 *  Returns the of the SDK's CFBundleShortVersionString
 */
@property(class, readonly) NSString *bundleVersionShortString;

/**
 *  Sets up the SumUpSDK for use in your app.
 *
 *  Needs to be called from the main thread at some point before starting interaction with the SDK.
 *  As this might ask for the user's location it should not necessarily be part
 *  of the app launch. Make sure to only setup once per app lifecycle.
 *
 *  If the user did not previously grant your app the permission to use her location,
 *  calling this method will prompt the user to grant such permission.
 *
 *  @param apiKey Your application's API Key for the SumUpSDK.
 *  @return YES if setup was successful. NO otherwise or if SDK has been set up before.
 */
+ (BOOL)setupWithAPIKey:(NSString *)apiKey;

#pragma mark - Authentication

/**
 *  Presents the login modally from the given view controller.
 *
 *  The login is automatically dismissed if login was successful or cancelled by the user.
 *  If error is nil and success is NO, the user cancelled the login.
 *  Errors are handled internally and usually do not need any display to the user.
 *  Does nothing if merchant is already logged in (calls completion block with success=NO, error=nil).
 *
 *  @param fromViewController The UIViewController instance from which the login should be presented modally.
 *  @param animated Pass YES to animate the transition.
 *  @param block The completion block is called after each login attempt.
 */
+ (void)presentLoginFromViewController:(UIViewController *)fromViewController
                              animated:(BOOL)animated
                       completionBlock:(nullable SMPCompletionBlock)block;

/**
 *  Logs in a merchant with an access token acquired via https://developer.sumup.com/docs/authorization/.
 *  You must implement the "Authorization code flow", the "Client credentials flow" is not supported.
 *  Make sure that no user is logged in already when calling this method.
 *
 *  @param aToken a user-scoped access token
 *  @param block  a completion block that will run after login has succeeded/failed
 */
+ (void)loginWithToken:(NSString *)aToken completion:(nullable SMPCompletionBlock)block;

/**
 *  Performs a logout of the current merchant and resets the remembered password.
 *
 *  @param block The completion block is called once the logout has finished.
 */
+ (void)logoutWithCompletionBlock:(nullable SMPCompletionBlock)block;

#pragma mark - Checkout

/**
 *  Can be called in advance when a checkout is imminent and a user is logged in.
 *  You should use this method to let the SDK know that the user is most likely starting a
 *  checkout attempt soon, e.g. when entering an amount or adding products to a shopping cart.
 *  This allows the SDK to take appropriate measures, like attempting to wake a connected card terminal.
 */
+ (void)prepareForCheckout;

/**
 *  Presents a checkout view with all necessary steps to charge a customer.
 *
 *  @param request    The SMPCheckoutRequest encapsulates all transaction relevant data such as total amount, label, etc.
 *  @param controller The UIViewController instance from which the checkout should be presented modally.
 *  @param block      The completion block will be called when the view will be dismissed.
 */
+ (void)checkoutWithRequest:(SMPCheckoutRequest *)request
         fromViewController:(UIViewController *)controller
                 completion:(nullable SMPCheckoutCompletionBlock)block;

/**
 *  Presenting checkout preferences allows the current merchant to configure the checkout options and
 *  change the card terminal. Merchants can also set up the terminal when applicable.
 *  Can only be called when a merchant is logged in and checkout is not in progress.
 *  The completion block will be executed once the preferences have been dismissed.
 *  The success parameter indicates whether the preferences have been presented.
 *  If not successful an error will be provided, see SMPSumUpSDKError.
 *
 *  @param fromViewController The UIViewController instance from which the checkout should be presented modally.
 *  @param animated           Pass YES to animate the transition.
 *  @param block              The completion block is called after the view controller has been dismissed.
 */
+ (void)presentCheckoutPreferencesFromViewController:(UIViewController *)fromViewController
                                            animated:(BOOL)animated
                                          completion:(nullable SMPCompletionBlock)block;

#pragma mark - Tap to Pay on iPhone

/**
 *  Checks whether the Tap to Pay on iPhone payment method is available for the current merchant and whether or
 *  not it requires activation to be performed via a call to
 *  `presentTapToPayActivationFromViewController:animated:completionBlock:`.
 *
 *  For the merchant to be able to use this payment method the following must be true:
 *
 *    - The feature must be available in the merchant's country
 *
 *    - It must be activated. This is where the merchant's Apple ID is linked with their SumUp account and the 
 *      iPhone is prepared to work as a card reader. As this can take a minute or so the first time, the
 *      merchant is shown a UI that introduces them to the feature as it initializes in the background.
 *
 *  The merchant must be logged in before you call this method.
 *
 *  @param availability YES if the feature is available for the current merchant and it's OK to start activation.
 *  @param isActivated  YES if activation has already been done for this device and merchant account
 */
+ (void)checkTapToPayAvailability:(void (^ _Nonnull)(BOOL isAvailable, BOOL isActivated, NSError * _Nullable error))block NS_SWIFT_NAME(checkTapToPayAvailability(completion:));

/**
 *  Performs activation for Tap to Pay on iPhone. This prepares the device, introduces the merchant to the
 *  feature and links their Apple ID to their SumUp account (which will require confirmation from the merchant.)
 *
 *  Call `checkTapToPayAvailability:` before calling this method to find out if this payment method is available
 *  and if activation is needed.
 *
 *  The merchant must be logged in before you call this method.
 *
 *  Tap to Pay on iPhone requirements:
 *
 *  - The hosting app must have the `com.apple.developer.proximity-reader.payment.acceptance`
 *    entitlement.
 *
 *  - The merchant must have an iPhone XS or later with iOS 16.4 or later (iOS 17 or later recommended.)
 *    The feature does not work with iPads.
 *
 *  @param fromViewController The UIViewController instance from which the UI should be presented modally.
 *  @param animated           Pass YES to animate the transition.
 *  @param block              The completion block is called after the view controller has been dismissed.
 */
+ (void)presentTapToPayActivationFromViewController:(UIViewController *)fromViewController
                                           animated:(BOOL)animated
                                    completionBlock:(nullable SMPCompletionBlock)block;

/**
 *  Returns the lozalized Tap To Pay on iPhone string.
 *  For iOS versions less than 16.4, will return nil.
 */
+ (NSString  * _Nonnull)tapToPayProductName;

#pragma mark - Misc

/**
 *  This method does not do anything. It will be removed in a future release.
 */
+ (void)setUINotificationsForReaderStatusEnabled:(BOOL)enabled __attribute__ ((deprecated));

#pragma mark - Error Domain and Codes

NS_SWIFT_NAME(SumUpSDKErrorDomain)
extern NSString * const SMPSumUpSDKErrorDomain;

/**
 *  The error codes returned from the SDK
 */
typedef NS_ENUM(NSInteger, SMPSumUpSDKError) {
    /// General error
    SMPSumUpSDKErrorGeneral                        = 0,
    /// The merchant's account is not activated
    SMPSumUpSDKErrorActivationNeeded               = 1,
    /// General error with the merchant's account
    SMPSumUpSDKErrorAccountGeneral                 = 20,
    /// The merchant is not logged in to their account
    SMPSumUpSDKErrorAccountNotLoggedIn             = 21,
    /// A merchant is logged in already. Call logout before logging in again.
    SMPSumUpSDKErrorAccountIsLoggedIn              = 22,
    /// Generel checkout error
    SMPSumUpSDKErrorCheckoutGeneral                = 50,
    /// Another checkout process is currently in progress.
    SMPSumUpSDKErrorCheckoutInProgress             = 51,
    /// The currency code specified in the checkout request does not match that of the current merchant.
    SMPSumUpSDKErrorCheckoutCurrencyCodeMismatch   = 52,
    /// The foreign transaction ID specified in the checkout request has already been used.
    SMPSumUpSDKErrorDuplicateForeignID             = 53,
    /// The access token is invalid. Login to get a valid access token.
    SMPSumUpSDKErrorInvalidAccessToken             = 54,
    /// The amount entered contains invalid number of decimals.
    SMPSumUpSDKErrorInvalidAmountDecimals          = 55,
    /// The processAs property of CheckoutRequest is not valid
    SMPSumUpSDKErrorInvalidProcessAs               = 56,
    /// The numberOfInstallments property of CheckoutRequest is not valid
    SMPSumUpSDKErrorInvalidNumberOfInstallments    = 57,
    /// Tap to Pay on iPhone payment method is not available for the current merchant. This may be
    /// because the payment method is not available in their country.
    SMPSumUpSDKErrorTapToPayNotAvailable           = 100,
    /// Tap to Pay on iPhone: activation is required. Call `presentTapToPayActivationFromViewController:animated:completionBlock:`.
    SMPSumUpSDKErrorTapToPayActivationNeeded       = 101,
    /// Tap to Pay on iPhone: an unspecified error occurred
    SMPSumUpSDKErrorTapToPayInternalError          = 102,
    /// Tap to Pay on iPhone requires an iPhone XS or later and does not work on iPads.
    SMPSumUpSDKErrorTapToPayMinHardwareNotMet      = 103,
    /// Tap to Pay on iPhone requires a newer version of iOS; please check the documentation for the
    /// minimum supported version.
    SMPSumUpSDKErrorTapToPayiOSVersionTooOld       = 104,
    /// Tap to Pay on iPhone has some other (unspecified) requirement(s) that are not met.
    SMPSumUpSDKErrorTapToPayRequirementsNotMet     = 105,
} NS_SWIFT_NAME(SumUpSDKError);

#pragma mark - Features

/**
 *  Returns YES if the last-used card reader, if any, supports the Tip on Card Reader feature (TCR).
 *  TCR prompts the customer directly on the card reader's display for a tip amount, rather than
 *  prompting for a tip amount on the iPhone or iPad display.
 *  This property will equal NO if no card reader has been used before. You can optionally present
 *  the Checkout Preferences screen to configure a card reader before the first transaction occurs
 *  to avoid this.
 */
@property (class, readonly) BOOL isTipOnCardReaderAvailable;

/**
 *  `YES` if the merchant's country requires the `processAs` property to be set for each transaction.
 *
 *  Warning: The transaction will fail if `isProcessAsRequired` is `YES` but `processAs`on
 *  `SMPCheckoutRequest` is `SMPProcessAsNotSet`.
 *
 *  When the payment method is card reader and `processAs` is `SMPProcessAsNotSet`,
 *  for backwards-compatibility it will be automatically set to `SMPProcessAsPromptUser`.
 *  However, `SMPProcessAsPromptUser` will be completely removed in a future version of
 *  the SDK and transactions may start failing because `processAs` was not set.
 *
 *  Please migrate your code to always set `processAs` if `isProcessAsRequired` is YES.
 */
@property (class, readonly) BOOL isProcessAsRequired;

#pragma mark - SDK Integration

/**
 *  You can use this method to test if you have integrated the SDK correctly.
 *
 *  The method will log several tests to the console. If you see any errors, please check the README for setup instructions.
 *
 *  @warning While not harmful, this method is not meant to be used in production. Use this in temporary code or in debug configurations only.
 *
 *  @return YES if the SDK is set up correctly, NO if any error was found.
 */
+ (BOOL)testSDKIntegration;

@end

NS_ASSUME_NONNULL_END
