package mtf.factory.plugins.sumup;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.sumup.merchant.reader.api.SumUpAPI;
import com.sumup.merchant.reader.api.SumUpLogin;
import com.sumup.merchant.reader.api.SumUpPayment;

import android.content.Intent;
import android.os.Bundle;

import java.math.BigDecimal;

@CapacitorPlugin(name = "SumupCapacitor")
public class SumupCapacitorPlugin extends Plugin {

  private static final int REQUEST_CODE_LOGIN = 1;
  private static final int REQUEST_CODE_PAYMENT = 2;

  private PluginCall loginCall;
  private PluginCall checkoutCall;

  public void login(PluginCall call) {
    String affiliateKey = call.getString("affiliateKey");
    if (affiliateKey == null) {
      call.reject("affiliateKey mancante");
      return;
    }
    this.loginCall = call;

    SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).build();
    SumUpAPI.openLoginActivity(getActivity(), sumupLogin, REQUEST_CODE_LOGIN);
  }

  public void checkout(PluginCall call) {
    Double amount = call.getDouble("amount");
    if (amount == null) {
      call.reject("amount mancante");
      return;
    }
    this.checkoutCall = call;

    SumUpPayment payment = SumUpPayment.builder()
      .total(new BigDecimal(amount.toString()))
      .currency(SumUpPayment.Currency.EUR)
      .build();

    SumUpAPI.checkout(getActivity(), payment, REQUEST_CODE_PAYMENT);
  }

  @Override
  protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
    super.handleOnActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_LOGIN && loginCall != null) {
      if (data != null && data.getExtras() != null) {
        int code = data.getExtras().getInt(SumUpAPI.Response.RESULT_CODE);
        String msg = data.getExtras().getString(SumUpAPI.Response.MESSAGE);
        if (code == SumUpAPI.Response.ResultCode.SUCCESSFUL) {
          loginCall.resolve();
        } else {
          loginCall.reject("Login fallito: " + msg);
        }
      } else {
        loginCall.reject("Login cancellato o errore sconosciuto");
      }
      loginCall = null;
    }

    else if (requestCode == REQUEST_CODE_PAYMENT && checkoutCall != null) {
      if (data != null && data.getExtras() != null) {
        int code = data.getExtras().getInt(SumUpAPI.Response.RESULT_CODE);
        String msg = data.getExtras().getString(SumUpAPI.Response.MESSAGE);
        if (code == SumUpAPI.Response.ResultCode.SUCCESSFUL) {
          checkoutCall.resolve();
        } else {
          checkoutCall.reject("Pagamento fallito: " + msg);
        }
      } else {
        checkoutCall.reject("Pagamento cancellato o errore sconosciuto");
      }
      checkoutCall = null;
    }
  }
}
