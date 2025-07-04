package mt.factory.plugins.sumuplugin;

import android.content.Intent;
import android.os.Bundle;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.sumup.merchant.reader.api.SumUpState;
import com.sumup.merchant.reader.api.SumUpAPI;
import com.sumup.merchant.reader.api.SumUpLogin;
import com.sumup.merchant.reader.api.SumUpPayment;
import com.sumup.merchant.reader.models.TransactionInfo;
import com.sumup.merchant.reader.models.Merchant;
import com.sumup.merchant.reader.api.SumUpAPI.Response;

import java.math.BigDecimal;
import java.util.Iterator;

@CapacitorPlugin(
  name = "SumupCapacitor",
  requestCodes = {
    SumUpPlugin.REQUEST_CODE_LOGIN,
    SumUpPlugin.REQUEST_CODE_PAYMENT,
    SumUpPlugin.REQUEST_CODE_CARD_READER_PAGE
  }
)
public class SumUpPlugin extends Plugin {

  protected static final int REQUEST_CODE_LOGIN             = 1;
  protected static final int REQUEST_CODE_PAYMENT           = 2;
  protected static final int REQUEST_CODE_CARD_READER_PAGE  = 4;

  @Override
  public void load() {
    super.load();
    SumUpState.init(getContext());
  }

  // — LOGIN —
  @PluginMethod
  public void login(PluginCall call) {
    String affiliateKey = call.getString("affiliateKey");
    if (affiliateKey == null) {
      call.reject("affiliateKey mancante");
      return;
    }
    saveCall(call);
    SumUpLogin login = SumUpLogin.builder(affiliateKey).build();
    SumUpAPI.openLoginActivity(getActivity(), login, REQUEST_CODE_LOGIN);
  }

  // — STATO DEL MERCHANT —
  @PluginMethod
  public void isLoggedIn(PluginCall call) {
    boolean logged = SumUpAPI.isLoggedIn();
    JSObject ret = new JSObject().put("isLoggedIn", logged);
    call.resolve(ret);
  }

  @PluginMethod
  public void getCurrentMerchant(PluginCall call) {
    if (!SumUpAPI.isLoggedIn()) {
      call.reject("Not logged in");
      return;
    }
    Merchant m = SumUpAPI.getCurrentMerchant();
    JSObject ret = new JSObject()
      .put("merchantCode", m.getMerchantCode())
      .put("currency", m.getCurrency().getIsoCode());
    call.resolve(ret);
  }

  // — LOGOUT —
  @PluginMethod
  public void logout(PluginCall call) {
    SumUpAPI.logout();
    call.resolve();
  }

  // — PREPARAZIONE TERMINALE —
  @PluginMethod
  public void prepareForCheckout(PluginCall call) {
    SumUpAPI.prepareForCheckout();
    call.resolve();
  }

  // — APRI CARD READER PAGE —
  @PluginMethod
  public void openCardReaderPage(PluginCall call) {
    saveCall(call);
    SumUpAPI.openCardReaderPage(getActivity(), REQUEST_CODE_CARD_READER_PAGE);
  }

  // — PAGAMENTO AVANZATO —
  @PluginMethod
  public void checkout(PluginCall call) {
    Double amount = call.getDouble("amount");
    if (amount == null) {
      call.reject("amount mancante");
      return;
    }
    // optional parameters
    String currency = call.getString("currency", "EUR");
    String title = call.getString("title", null);
    String email = call.getString("receiptEmail", null);
    String sms   = call.getString("receiptSMS", null);
    String txId  = call.getString("foreignTransactionId", null);
    JSObject info = call.getObject("additionalInfo");
    boolean retryEnabled = call.getBoolean("retryEnabled", false);
    int retryInterval = call.getInt("retryInterval", 2000);
    int retryTimeout  = call.getInt("retryTimeout", 60000);

    saveCall(call);

    SumUpPayment.Builder builder = SumUpPayment.builder()
      .total(new BigDecimal(amount.toString()))
      .currency(SumUpPayment.Currency.valueOf(currency))
      .configureRetryPolicy(retryInterval, retryTimeout, retryEnabled);

    if (title   != null) builder.title(title);
    if (email   != null) builder.receiptEmail(email);
    if (sms     != null) builder.receiptSMS(sms);
    if (txId    != null) builder.foreignTransactionId(txId);

    if (info != null) {
      Iterator<String> keys = info.keys();
      while (keys.hasNext()) {
        String k = keys.next();
        builder.addAdditionalInfo(k, info.getString(k));
      }
    }

    SumUpPayment payment = builder.build();
    SumUpAPI.checkout(getActivity(), payment, REQUEST_CODE_PAYMENT);
  }

  @Override
  protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
    super.handleOnActivityResult(requestCode, resultCode, data);
    PluginCall saved = getSavedCall();
    if (saved == null) {
      return;
    }

    Bundle extras = data != null ? data.getExtras() : null;

    switch (requestCode) {
      case REQUEST_CODE_LOGIN:
      case REQUEST_CODE_CARD_READER_PAGE:
        if (extras != null) {
          int code = extras.getInt(Response.RESULT_CODE);
          String msg = extras.getString(Response.MESSAGE);
          JSObject ret = new JSObject()
            .put("resultCode", code)
            .put("message", msg);
          saved.resolve(ret);
        } else {
          saved.reject("Operazione annullata");
        }
        break;

      case REQUEST_CODE_PAYMENT:
        if (extras != null) {
          int code = extras.getInt(Response.RESULT_CODE);
          String msg = extras.getString(Response.MESSAGE);
          JSObject ret = new JSObject()
            .put("resultCode", code)
            .put("message", msg)
            .put("transactionCode", extras.getString(Response.TX_CODE))
            .put("receiptSent", extras.getBoolean(Response.RECEIPT_SENT));
          // TX_INFO è un Parcelable, convertire se serve
          saved.resolve(ret);
        } else {
          saved.reject("Pagamento annullato");
        }
        break;
    }
  }
}
