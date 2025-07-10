import { Component, OnInit } from '@angular/core'; // Import OnInit for lifecycle hooks
import { RouterOutlet } from '@angular/router';
import {
  SumupService,
  PaymentOptions,
  PaymentResult
} from './sumup.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit { // Implement OnInit to use its lifecycle hooks
  // State variables to display results in the template
  loginResult: { resultCode: number; message: string } | null = null;
  loggedIn = false;
  merchant: { merchantCode: string; currency: string } | null = null;
  checkoutResult: PaymentResult | null = null;
  readerResult: { resultCode: number; message: string } | null = null;
  
  // A flag to enable buttons only after a successful setup
  isSumupReady = false;

  constructor(private sumup: SumupService) {}

  // ngOnInit is executed when the component is initialized
  ngOnInit() {
    this.doSetup();
  }

  // NEW METHOD: Executes the setup on startup
  async doSetup() {
    try {
      // Call setup once with your API key
      await this.sumup.setup('sup_afk_V3XgW1VFygZqgAo2jqHkIfMQDAAjQs0c');
      this.isSumupReady = true;
      console.log('✅ SumUp SDK is ready.');
      // After setup, immediately check if the user is already logged in
      await this.doCheckLoggedIn();
      if (this.loggedIn) {
        await this.doGetMerchant();
      }
    } catch (err) {
      this.isSumupReady = false;
      console.error('❌ SumUp setup failed:', err);
      // You could display an error to the user here
    }
  }

  // MODIFIED METHOD: No longer receives the API key
  async doLogin() {
    try {
      this.loginResult = await this.sumup.login();
      // After login, update the state
      await this.doCheckLoggedIn();
      if (this.loggedIn) {
        await this.doGetMerchant();
      }
    } catch (err) {
      console.error('Login error:', err);
    }
  }

  // 2) Check if logged in
  async doCheckLoggedIn() {
    try {
      this.loggedIn = await this.sumup.isLoggedIn();
    } catch (err) {
      console.error('Check login error:', err);
    }
  }

  // 3) Get merchant data
  async doGetMerchant() {
    try {
      this.merchant = await this.sumup.getCurrentMerchant();
    } catch (err) {
      console.error('Get merchant error:', err);
    }
  }

  // 4) Logout
  async doLogout() {
    try {
      await this.sumup.logout();
      // Reset all state variables
      this.loggedIn = false;
      this.merchant = null;
      this.loginResult = null;
      console.log('Logout successful.');
    } catch (err) {
      console.error('Logout error:', err);
    }
  }

  // 5) Prepare for checkout
  async doPrepare() {
    try {
      await this.sumup.prepareForCheckout();
      console.log('Terminal ready for checkout');
    } catch (err) {
      console.error('Prepare error:', err);
    }
  }

  // 6) Open Card Reader Page
  async doOpenReader() {
    try {
      this.readerResult = await this.sumup.openCardReaderPage();
    } catch (err) {
      console.error('Open reader error:', err);
    }
  }

  // 7) Checkout / payment
  async doCheckout() {
    const opts: PaymentOptions = {
      amount: 1.23,
      currency: 'EUR',
      title: 'Taxi Ride',
      foreignTransactionId: 'order-' + Date.now(),
      // The 'additionalInfo' and retry options are only handled on Android
      // additionalInfo: { orderId: '1234', from: 'Rome', to: 'Milan' },
      // retryEnabled: true,
      // retryInterval: 2000,
      // retryTimeout: 60000,
    };

    try {
      this.checkoutResult = await this.sumup.checkout(opts);
    } catch (err) {
      console.error('Checkout error:', err);
    }
  }
}
