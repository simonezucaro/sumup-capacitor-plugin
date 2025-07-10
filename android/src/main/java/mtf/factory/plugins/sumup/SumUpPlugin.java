package mtf.factory.plugins.sumup;

import android.content.Intent;
import android.os.Bundle;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.sumup.merchant.reader.api.SumUpAPI;
import com.sumup.merchant.reader.api.SumUpLogin;
import com.sumup.merchant.reader.api.SumUpPayment;
import com.sumup.merchant.reader.api.SumUpState;
import com.sumup.merchant.reader.models.Merchant;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 * SumUpPlugin is the native Android implementation for the Capacitor SumUp SDK plugin.
 * It handles all interactions with the SumUp Merchant SDK, including login, payments,
 * and managing merchant information.
 */
@CapacitorPlugin(
  name = "SumupCapacitor",
  requestCodes = {
    SumUpPlugin.REQUEST_CODE_LOGIN,
    SumUpPlugin.REQUEST_CODE_PAYMENT,
    SumUpPlugin.REQUEST_CODE_CARD_READER_PAGE,
  }
)
public class SumUpPlugin extends Plugin {

  // Request codes used to identify results from SumUp activities.
  protected static final int REQUEST_CODE_LOGIN = 1;
  protected static final int REQUEST_CODE_PAYMENT = 2;
  protected static final int REQUEST_CODE_CARD_READER_PAGE = 4;

  // Stores the SumUp Affiliate Key (API Key) after it has been provided via the setup method.
  private String affiliateKey;

  /**
   * Called when the plugin is first loaded. Initializes the SumUp SDK state with the
   * application context.
   */
  @Override
  public void load() {
    super.load();
    SumUpState.init(getContext());
  }

  /**
   * Initializes the SumUp SDK with the provided Affiliate Key.
   * This method must be called once before any other methods that require authentication.
   * @param call The plugin call object containing the 'affiliateKey'.
   */
  @PluginMethod
  public void setup(PluginCall call) {
    String key = call.getString("affiliateKey");
    if (key == null || key.isEmpty()) {
      call.reject("affiliateKey mancante");
      return;
    }
    // Store the key for later use in login and other methods.
    this.affiliateKey = key;
    JSObject ret = new JSObject();
    ret.put("message", "SumUp SDK inizializzato per Android.");
    call.resolve(ret);
  }

  /**
   * Opens the SumUp login screen.
   * Requires the `setup` method to have been called first.
   * @param call The plugin call object. The result will be sent back asynchronously.
   */
  @PluginMethod
  public void login(PluginCall call) {
    // Ensure the setup method has been called and the affiliate key is available.
    if (this.affiliateKey == null || this.affiliateKey.isEmpty()) {
      call.reject("SDK non inizializzato. Chiamare prima il metodo setup().");
      return;
    }
    // Save the call to be resolved later in handleOnActivityResult.
    saveCall(call);
    // Use the stored affiliate key to build the login request.
    SumUpLogin login = SumUpLogin.builder(this.affiliateKey).build();
    SumUpAPI.openLoginActivity(getActivity(), login, REQUEST_CODE_LOGIN);
  }

  /**
   * Checks if a merchant is currently logged in.
   * @param call The plugin call object.
   */
  @PluginMethod
  public void isLoggedIn(PluginCall call) {
    boolean logged = SumUpAPI.isLoggedIn();
    JSObject ret = new JSObject().put("isLoggedIn", logged);
    call.resolve(ret);
  }

  /**
   * Retrieves information about the currently logged-in merchant.
   * @param call The plugin call object.
   */
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

  /**
   * Logs out the current merchant.
   * @param call The plugin call object.
   */
  @PluginMethod
  public void logout(PluginCall call) {
    SumUpAPI.logout();
    call.resolve();
  }

  /**
   * Warms up the card reader to speed up the checkout process.
   * @param call The plugin call object.
   */
  @PluginMethod
  public void prepareForCheckout(PluginCall call) {
    SumUpAPI.prepareForCheckout();
    call.resolve();
  }

  /**
   * Opens the card reader settings page.
   * @param call The plugin call object. The result will be sent back asynchronously.
   */
  @PluginMethod
  public void openCardReaderPage(PluginCall call) {
    saveCall(call);
    SumUpAPI.openCardReaderPage(getActivity(), REQUEST_CODE_CARD_READER_PAGE);
  }

  /**
   * Initiates a payment process.
   * @param call The plugin call object containing payment details like amount, currency, etc.
   */
  @PluginMethod
  public void checkout(PluginCall call) {
    // A security check to ensure a user is logged in before attempting a payment.
    if (!SumUpAPI.isLoggedIn()) {
      call.reject("Utente non loggato. Eseguire il login prima del checkout.");
      return;
    }

    Double amount = call.getDouble("amount");
    if (amount == null) {
      call.reject("amount mancante");
      return;
    }

    // Read optional parameters from the call.
    String currency = call.getString("currency", "EUR");
    String title = call.getString("title", null);
    String email = call.getString("receiptEmail", null);
    String sms = call.getString("receiptSMS", null);
    String txId = call.getString("foreignTransactionId", null);
    JSObject info = call.getObject("additionalInfo");
    boolean retryEnabled = call.getBoolean("retryEnabled", false);
    int retryInterval = call.getInt("retryInterval", 2000);
    int retryTimeout = call.getInt("retryTimeout", 60000);

    // Save the call to be resolved later in handleOnActivityResult.
    saveCall(call);

    // Build the payment request using the provided options.
    SumUpPayment.Builder builder = SumUpPayment
      .builder()
      .total(new BigDecimal(amount.toString()))
      .currency(SumUpPayment.Currency.valueOf(currency))
      .configureRetryPolicy(retryInterval, retryTimeout, retryEnabled);

    if (title != null) builder.title(title);
    if (email != null) builder.receiptEmail(email);
    if (sms != null) builder.receiptSMS(sms);
    if (txId != null) builder.foreignTransactionId(txId);

    // Add any additional key-value information to the payment.
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

  /**
   * Handles the results from activities started by the SumUp SDK (e.g., login, payment).
   * This method is the central point for receiving asynchronous responses from SumUp.
   *
   * @param requestCode The integer request code originally supplied to startActivityForResult().
   * @param resultCode The integer result code returned by the child activity.
   * @param data An Intent, which can return result data to the caller.
   */
  @Override
  protected void handleOnActivityResult(
    int requestCode,
    int resultCode,
    Intent data
  ) {
    super.handleOnActivityResult(requestCode, resultCode, data);
    // Retrieve the saved plugin call that initiated this activity.
    PluginCall saved = getSavedCall();
    if (saved == null) {
      return;
    }

    Bundle extras = data != null ? data.getExtras() : null;

    switch (requestCode) {
      // Handle results for both login and card reader settings page.
      case REQUEST_CODE_LOGIN:
      case REQUEST_CODE_CARD_READER_PAGE:
        if (extras != null) {
          int code = extras.getInt(SumUpAPI.Response.RESULT_CODE);
          String msg = extras.getString(SumUpAPI.Response.MESSAGE);
          JSObject ret = new JSObject()
            .put("resultCode", code)
            .put("message", msg);
          saved.resolve(ret);
        } else {
          // This typically happens if the user cancels the operation.
          saved.reject("Operazione annullata");
        }
        break;
      // Handle the result from a payment.
      case REQUEST_CODE_PAYMENT:
        if (extras != null) {
          int code = extras.getInt(SumUpAPI.Response.RESULT_CODE);
          String msg = extras.getString(SumUpAPI.Response.MESSAGE);
          JSObject ret = new JSObject()
            .put("resultCode", code)
            .put("message", msg)
            .put(
              "transactionCode",
              extras.getString(SumUpAPI.Response.TX_CODE)
            )
            .put(
              "receiptSent",
              extras.getBoolean(SumUpAPI.Response.RECEIPT_SENT)
            );
          // Note: TX_INFO is a Parcelable object and would require custom conversion if needed.
          saved.resolve(ret);
        } else {
          // This typically happens if the user cancels the payment.
          saved.reject("Pagamento annullato");
        }
        break;
    }
  }
}
