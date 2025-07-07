import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {
  SumupService,
  PaymentOptions,
  PaymentResult
} from './sumup.service';
import { NgIf } from '@angular/common';

/**
 * The root component of the SumUp demo application.
 * 
 * This component manages the main application state and provides methods to interact with the SumUp payment service,
 * including authentication, merchant data retrieval, payment processing, and card reader operations.
 * 
 * @remarks
 * - Handles login/logout and session state with SumUp.
 * - Retrieves merchant information and manages payment transactions.
 * - Prepares the terminal for checkout and opens the card reader interface.
 * - Stores and exposes the results of each operation for UI feedback.
 * 
 * @example
 * ```typescript
 * // Example usage in a template:
 * <button (click)="doLogin()">Login</button>
 * <button (click)="doCheckout()">Pay</button>
 * ```
 * 
 * @property loginResult - Stores the result of the login operation, including result code and message.
 * @property loggedIn - Indicates whether the user is currently authenticated with SumUp.
 * @property merchant - Contains merchant details such as merchant code and currency.
 * @property checkoutResult - Holds the result of the last payment transaction.
 * @property readerResult - Stores the result of opening the card reader page.
 * 
 * @method doLogin - Initiates the login process with SumUp using a predefined API key.
 * @method doCheckLoggedIn - Checks if the user is currently logged in to SumUp.
 * @method doGetMerchant - Retrieves the current merchant's information from SumUp.
 * @method doLogout - Logs out the current user and resets session-related state.
 * @method doPrepare - Prepares the terminal for a checkout operation.
 * @method doOpenReader - Opens the card reader interface and stores the result.
 * @method doCheckout - Initiates a payment transaction with predefined options.
 * 
 * @dependency SumupService - Service responsible for communicating with the SumUp SDK or API.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  // Properties to store results and state
  loginResult: { resultCode: number; message: string } | null = null;
  loggedIn = false;
  merchant: { merchantCode: string; currency: string } | null = null;
  checkoutResult: PaymentResult | null = null;
  readerResult: { resultCode: number; message: string } | null = null;

  constructor(private _sumupService: SumupService) {}

  // 1) Login
  async doLogin() {
    try {
      this.loginResult = await this._sumupService.login('sup_afk_V3XgW1VFygZqgAo2jqHkIfMQDAAjQs0c');
    } catch (err) {
      console.error('Error login:', err);
    }
  }

  // 2) Check if logged in
  async doCheckLoggedIn() {
    try {
      this.loggedIn = await this._sumupService.isLoggedIn();
    } catch (err) {
      console.error('Error check login:', err);
    }
  }

  // 3) Get Merchant Information
  async doGetMerchant() {
    try {
      this.merchant = await this._sumupService.getCurrentMerchant();
    } catch (err) {
      console.error('Error getMerchant:', err);
    }
  }

  // 4) Logout
  async doLogout() {
    try {
      await this._sumupService.logout();
      this.loggedIn = false;
      this.merchant = null;
    } catch (err) {
      console.error('Error logging out:', err);
    }
  }

  // 5) Prepare for checkout
  async doPrepare() {
    try {
      await this._sumupService.prepareForCheckout();
      console.log('Terminal ready for checkout');
    } catch (err) {
      console.error('Error preparing for checkout:', err);
    }
  }

  // 6) Open Card Reader Page
  async doOpenReader() {
    try {
      this.readerResult = await this._sumupService.openCardReaderPage();
    } catch (err) {
      console.error('Error opening card reader:', err);
    }
  }

  // 7) Checkout / payment
  async doCheckout() {
    const opts: PaymentOptions = {
      amount: 1.23,
      currency: 'EUR',
      title: 'Taxi Ride',
      receiptEmail: 'cliente@example.com',
      receiptSMS: '+391234567890',
      foreignTransactionId: 'order-' + Date.now(),
      additionalInfo: { orderId: '1234', from: 'Rome', to: 'Milan' },
      retryEnabled: true,
      retryInterval: 2000,
      retryTimeout: 60000,
    };

    try {
      this.checkoutResult = await this._sumupService.checkout(opts);
    } catch (err) {
      console.error('Error during checkout:', err);
    }
  }

}
