import Capacitor
import SumUpSDK

@objc(SumupCapacitor)
public class SumupCapacitor: CAPPlugin {

    @objc func login(_ call: CAPPluginCall) {
        guard let affiliateKey = call.getString("affiliateKey") else {
            call.reject("affiliateKey mancante")
            return
        }

        SumUpSDK.login(affiliateKey: affiliateKey, from: self.bridge?.viewController) { success, error in
            if success {
                call.resolve(["resultCode": 0, "message": "Login success"])
            } else {
                call.reject(error?.localizedDescription ?? "Errore login")
            }
        }
    }

    @objc func isLoggedIn(_ call: CAPPluginCall) {
        call.resolve(["isLoggedIn": SumUpSDK.isLoggedIn])
    }

    @objc func getCurrentMerchant(_ call: CAPPluginCall) {
        guard let merchant = SumUpSDK.currentMerchant else {
            call.reject("Merchant non trovato")
            return
        }

        call.resolve([
            "merchantCode": merchant.merchantCode ?? "",
            "currency": merchant.currencyCode ?? ""
        ])
    }

    @objc func logout(_ call: CAPPluginCall) {
        SumUpSDK.logout()
        call.resolve()
    }

    @objc func checkout(_ call: CAPPluginCall) {
        guard let amount = call.getDouble("amount") else {
            call.reject("amount mancante")
            return
        }

        let currency = call.getString("currency") ?? "EUR"
        let request = SMPCheckoutRequest(total: NSDecimalNumber(value: amount), currency: currency)

        SumUpSDK.checkout(request, from: self.bridge?.viewController) { result, error in
            if let res = result {
                call.resolve([
                    "resultCode": res.resultCode,
                    "message": res.errorMessage ?? "",
                    "transactionCode": res.transactionCode ?? "",
                    "receiptSent": res.receiptSent
                ])
            } else {
                call.reject(error?.localizedDescription ?? "Errore durante il pagamento")
            }
        }
    }
}
