#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Definisce il plugin e i suoi metodi per renderli accessibili da JavaScript.
// Assicurati che il nome "SumupCapacitor" corrisponda esattamente a quello
// usato nel tuo file TypeScript (in src/index.ts).
CAP_PLUGIN(SumupCapacitor, "SumupCapacitor",
    CAP_PLUGIN_METHOD(login, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(isLoggedIn, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(getCurrentMerchant, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(logout, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(prepareForCheckout, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(openCardReaderPage, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(checkout, CAPPluginReturnPromise);
)