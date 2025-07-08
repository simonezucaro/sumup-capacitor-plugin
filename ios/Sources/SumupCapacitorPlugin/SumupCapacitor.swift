import Foundation
import Capacitor
import SumUpSDK

@objc(SumupCapacitor)
public class SumupCapacitor: CAPPlugin {

  @objc func login(_ call: CAPPluginCall) {
    guard let key = call.getString("affiliateKey") else {
      call.reject("affiliateKey mancante"); return
    }
    SumUpSDK.setup(withAPIKey: key)
    guard let vc = bridge?.viewController else {
      call.reject("No viewController"); return
    }
    SumUpSDK.presentLogin(from: vc, animated: true) { success, error in
      if let e = error {
        call.reject(e.localizedDescription)
      } else {
        call.resolve([
          "resultCode": success ? 0 : 2,
          "message": success ? "Login riuscito" : "Login annullato"
        ])
      }
    }
  }

  @objc func isLoggedIn(_ call: CAPPluginCall) {
    let logged = SumUpSDK.isLoggedIn()
    call.resolve(["isLoggedIn": logged])
  }

  @objc func getCurrentMerchant(_ call: CAPPluginCall) {
    guard let m = SumUpSDK.currentMerchant() else {
      call.reject("Nessun merchant loggato"); return
    }
    call.resolve([
      "merchantCode": m.merchantCode,
      "currency":     m.currencyCode
    ])
  }

  @objc func logout(_ call: CAPPluginCall) {
    SumUpSDK.logout { success, error in
      if let e = error {
        call.reject(e.localizedDescription)
      } else {
        call.resolve()
      }
    }
  }

  @objc func prepareForCheckout(_ call: CAPPluginCall) {
    SumUpSDK.prepareForCheckout()
    call.resolve()
  }

  @objc func openCardReaderPage(_ call: CAPPluginCall) {
    guard let vc = bridge?.viewController else {
      call.reject("No viewController"); return
    }
    SumUpSDK.presentCardReaderSelection(from: vc, animated: true) { _, error in
      if let e = error {
        call.reject(e.localizedDescription)
      } else {
        call.resolve([
          "resultCode": 0,
          "message":    "Lettore selezionato"
        ])
      }
    }
  }

  @objc func checkout(_ call: CAPPluginCall) {
    guard let vc = bridge?.viewController else {
      call.reject("No viewController"); return
    }
    guard let amt = call.getDouble("amount") else {
      call.reject("amount mancante"); return
    }
    let total = NSDecimalNumber(value: amt)
    let curr  = call.getString("currency")
             ?? SumUpSDK.currentMerchant()?.currencyCode
             ?? "EUR"

    let request = SMPCheckoutRequest.request(
      withTotal: total,
      title:    call.getString("title"),
      currencyCode: curr
    )

    // eventuali foreignTransactionId, receiptEmail, ecc. se supportati…

    SumUpSDK.checkout(with: request, from: vc) { result, error in
      if let e = error {
        call.reject(e.localizedDescription)
        return
      }
      guard let r = result else {
        call.reject("Nessun risultato dal checkout")
        return
      }
      var res: [String: Any] = [
        "resultCode": r.success ? 0 : 2,
        "message":    r.success ? "Transazione OK" : "Transazione annullata"
      ]
      if let tx = r.transactionCode {
        res["transactionCode"] = tx
      }
      res["receiptSent"] = r.receiptSent
      call.resolve(res)
    }
  }
}
