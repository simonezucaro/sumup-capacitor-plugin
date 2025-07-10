#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Registra il plugin e tutti i suoi metodi per renderli accessibili da JavaScript.
CAP_PLUGIN(SumupCapacitorPlugin, "SumupCapacitorPlugin",
    // ✅ RIGA MANCANTE AGGIUNTA QUI
    CAP_PLUGIN_METHOD(setup, CAPPluginReturnPromise);

    CAP_PLUGIN_METHOD(login, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(isLoggedIn, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(getCurrentMerchant, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(logout, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(prepareForCheckout, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(openCardReaderPage, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(checkout, CAPPluginReturnPromise);
)