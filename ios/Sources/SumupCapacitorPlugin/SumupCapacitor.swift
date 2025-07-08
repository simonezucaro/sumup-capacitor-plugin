import Foundation
import Capacitor
import SumUpSDK

@objc(SumupCapacitor)
public class SumupCapacitor: CAPPlugin {
  
  public override func load() {
    super.load()
    print("🔥 SumupCapacitor.load() chiamato")
    // Qui puoi mettere un breakpoint oppure loggare lo stato di bridge/pluginId
  }

  // 1) Login
  @objc func login(_ call: CAPPluginCall) {
    guard let key = call.getString("affiliateKey") else {
      call.reject("affiliateKey mancante")
      return
    }
    SumUpSDK.setup(withAPIKey: key)
    guard let vc = bridge?.viewController else {
      call.reject("No viewController")
      return
    }
    SumUpSDK.presentLogin(from: vc, animated: true) { success, error in
      if let e = error {
        call.resolve(["resultCode": 1, "message": e.localizedDescription])
      } else {
        call.resolve([
          "resultCode": success ? 0 : 2,
          "message": success ? "Login riuscito" : "Login annullato"
        ])
      }
    }
  }

  // 2) isLoggedIn
  @objc func isLoggedIn(_ call: CAPPluginCall) {
    call.resolve(["isLoggedIn": SumUpSDK.isLoggedIn])
  }

  // 3) getCurrentMerchant
  @objc func getCurrentMerchant(_ call: CAPPluginCall) {
    if let m = SumUpSDK.currentMerchant {
      call.resolve([
        "merchantCode": m.merchantCode ?? "",
        "currency":    m.currencyCode  ?? ""
      ])
    } else {
      call.reject("Nessun merchant loggato")
    }
  }

  // 4) logout
  @objc func logout(_ call: CAPPluginCall) {
    SumUpSDK.logout { (success: Bool, logoutError: Error?) in
      // 1) Se c’è un errore (secondo parametro), chiami reject
      if let error = logoutError {
        call.reject(error.localizedDescription)
        return
      }
      // 2) Altrimenti, resolve
      call.resolve()
    }
  }

  // 5) prepareForCheckout
  @objc func prepareForCheckout(_ call: CAPPluginCall) {
    SumUpSDK.prepareForCheckout()
    call.resolve()
  }

  // 6) openCardReaderPage
  @objc func openCardReaderPage(_ call: CAPPluginCall) {
  guard let vc = bridge?.viewController else {
    call.reject("No viewController")
    return
  }

  SumUpSDK.presentCheckoutPreferences(from: vc, animated: true) { (success: Bool, presentationError: Error?) in
  // 1) Se *presentationError* non è nil, è un Error → usa reject con localizedDescription
  if let error = presentationError {
    call.reject(error.localizedDescription)
    return
  }

  // 2) Altrimenti rispondi con il bool di successo
  let code    = success ? 0 : 2
  let message = success
    ? "Selezione terminale OK"
    : "Selezione annullata"

  call.resolve([
    "resultCode": code,
    "message":    message
  ])
}

}


  // 7) checkout
  @objc func checkout(_ call: CAPPluginCall) {
    guard let vc = bridge?.viewController else {
      call.reject("No viewController")
      return
    }
    guard let amount = call.getDouble("amount") else {
      call.reject("Parametri invalidi: amount mancante")
      return
    }
    let total = NSDecimalNumber(value: amount)

    let currency = call.getString("currency")
      ?? SumUpSDK.currentMerchant?.currencyCode
      ?? "EUR"

    let request = CheckoutRequest(
      total: total,
      title: call.getString("title"),
      currencyCode: currency
    )

    if let txId = call.getString("foreignTransactionId") {
      request.foreignTransactionID = txId
    }

    // Nota: iOS SDK non supporta receiptEmail, receiptSMS, additionalInfo, retry*
    SumUpSDK.checkout(with: request, from: vc) { result, error in
      if let e = error {
        call.resolve(["resultCode": 1, "message": e.localizedDescription])
        return
      }
      guard let r = result else {
        call.resolve(["resultCode": 2, "message": "Nessun risultato dal checkout"])
        return
      }
      var resp: [String: Any] = [
        "resultCode": r.success ? 0 : 2,
        "message":    r.success ? "Transazione OK" : "Transazione annullata"
      ]
      if let tx = r.transactionCode {
        resp["transactionCode"] = tx
      }
      // Se vuoi esporre receiptSent, dipende da cosa espone il tuo binding:
      // if let receipt = r.receiptSent { resp["receiptSent"] = receipt }
      call.resolve(resp)
    }
  }
}
