import Foundation
import Capacitor
import SumUpSDK

/**
 * SumupCapacitorPlugin is the native iOS implementation for the Capacitor SumUp SDK plugin.
 * It handles all interactions with the SumUp iOS SDK, including login, payments,
 * and managing merchant information.
 */
@objc(SumupCapacitorPlugin)
public class SumupCapacitorPlugin: CAPPlugin {

    // A static flag to track the initialization state of the SDK.
    // This prevents multiple setup calls and ensures the SDK is ready for use.
    private static var isInitialized = false

    /**
     * Called when the plugin is first loaded.
     */
    public override func load() {
        super.load()
        print("🔥 SumupCapacitorPlugin loaded")
    }

    /**
     * Initializes the SumUp SDK with the provided Affiliate Key.
     * This method must be called once before any other operations that require authentication.
     * @param call The plugin call object containing the 'affiliateKey'.
     */
    @objc func setup(_ call: CAPPluginCall) {
        guard let apiKey = call.getString("affiliateKey") else {
            call.reject("API key (affiliateKey) is missing")
            return
        }
        SumUpSDK.setup(withAPIKey: apiKey)
        // Set our flag to true to indicate that the setup has been completed.
        SumupCapacitorPlugin.isInitialized = true
        call.resolve(["message": "SumUp SDK initialized."])
    }

    /**
     * Opens the SumUp login screen.
     * Requires the `setup` method to have been called previously.
     * @param call The plugin call object. The result will be sent back asynchronously.
     */
    @objc func login(_ call: CAPPluginCall) {
        // Ensure that setup has been called and the key is available.
        guard SumupCapacitorPlugin.isInitialized else {
            call.reject("SDK not initialized. Please call the setup method first.")
            return
        }

        // UI operations must be performed on the main thread.
        DispatchQueue.main.async {
            guard let vc = self.bridge?.viewController else {
                call.reject("ViewController not found.")
                return
            }

            SumUpSDK.presentLogin(from: vc, animated: true) { success, error in
                if let error = error {
                    call.reject("Login failed", "LOGIN_ERROR", error, ["resultCode": 1])
                } else {
                    call.resolve([
                        "resultCode": success ? 0 : 2,
                        "message": success ? "Login successful" : "Login cancelled by user"
                    ])
                }
            }
        }
    }

    /**
     * Checks if a merchant is currently logged in.
     * @param call The plugin call object.
     */
    @objc func isLoggedIn(_ call: CAPPluginCall) {
        call.resolve(["isLoggedIn": SumUpSDK.isLoggedIn])
    }

    /**
     * Retrieves information about the currently logged-in merchant.
     * @param call The plugin call object.
     */
    @objc func getCurrentMerchant(_ call: CAPPluginCall) {
        guard SumUpSDK.isLoggedIn else {
            call.reject("No merchant is logged in")
            return
        }
        
        if let merchant = SumUpSDK.currentMerchant {
            call.resolve([
                "merchantCode": merchant.merchantCode ?? "",
                "currency": merchant.currencyCode ?? ""
            ])
        } else {
            call.reject("Could not retrieve merchant data.")
        }
    }

    /**
     * Logs out the current merchant.
     * @param call The plugin call object.
     */
    @objc func logout(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            SumUpSDK.logout { success, error in
                if let error = error {
                    call.reject("Logout failed", "LOGOUT_ERROR", error)
                } else {
                    if success {
                        // If logout is successful, reset the initialization flag
                        // to force a new setup on the next run.
                        SumupCapacitorPlugin.isInitialized = false
                    }
                    call.resolve()
                }
            }
        }
    }

    /**
     * Prepares the card reader to speed up the checkout process (warm-up).
     * @param call The plugin call object.
     */
    @objc func prepareForCheckout(_ call: CAPPluginCall) {
        guard SumUpSDK.isLoggedIn else {
            call.reject("User is not logged in. Cannot prepare for checkout.")
            return
        }
        SumUpSDK.prepareForCheckout()
        call.resolve()
    }

    /**
     * Opens the card reader settings screen.
     * @param call The plugin call object. The result will be sent back asynchronously.
     */
    @objc func openCardReaderPage(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            guard let vc = self.bridge?.viewController else {
                call.reject("ViewController not found.")
                return
            }

            SumUpSDK.presentCheckoutPreferences(from: vc, animated: true) { success, error in
                if let error = error {
                    call.reject("Failed to open reader page", "PREFERENCES_ERROR", error)
                } else {
                    call.resolve([
                        "resultCode": success ? 0 : 2,
                        "message": success ? "Operation completed" : "Operation cancelled"
                    ])
                }
            }
        }
    }

    /**
     * Initiates a payment process.
     * @param call The plugin call object containing payment details such as
     * amount, currency, etc.
     */
    @objc func checkout(_ call: CAPPluginCall) {
        // Security check to ensure the user is logged in before making a payment.
        guard SumupCapacitorPlugin.isInitialized && SumUpSDK.isLoggedIn else {
            call.reject("SDK not initialized or user not logged in.")
            return
        }
        
        guard let amount = call.getDouble("amount") else {
            call.reject("'amount' parameter is missing or invalid")
            return
        }
        let total = NSDecimalNumber(value: amount)
        let title = call.getString("title") ?? "Checkout"
        let currency = call.getString("currency") ?? SumUpSDK.currentMerchant?.currencyCode ?? "EUR"
        
        let request = CheckoutRequest(total: total, title: title, currencyCode: currency)
        
        if let foreignTxId = call.getString("foreignTransactionId") {
            request.foreignTransactionID = foreignTxId
        }

        DispatchQueue.main.async {
            guard let vc = self.bridge?.viewController else {
                call.reject("ViewController not found.")
                return
            }

            SumUpSDK.checkout(with: request, from: vc) { result, error in
                if let error = error {
                    call.reject("Checkout failed", "CHECKOUT_ERROR", error, ["resultCode": 1])
                    return
                }

                guard let result = result else {
                    call.reject("Invalid checkout result.", "INVALID_RESULT", nil, ["resultCode": 2])
                    return
                }

                var response: [String: Any] = [
                    "resultCode": result.success ? 0 : 2,
                    "message": result.success ? "Transaction successful" : "Transaction cancelled"
                ]
                if let txCode = result.transactionCode {
                    response["transactionCode"] = txCode
                }
                
                call.resolve(response)
            }
        }
    }
}
