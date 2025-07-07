import { Injectable } from '@angular/core';
import { SumupCapacitor } from 'sumup-capacitor-plugin';

export interface PaymentOptions {
  amount: number;
  currency?: string;
  title?: string;
  receiptEmail?: string;
  receiptSMS?: string;
  foreignTransactionId?: string;
  additionalInfo?: Record<string, string>;
  retryEnabled?: boolean;
  retryInterval?: number;
  retryTimeout?: number;
}

export interface PaymentResult {
  resultCode: number;
  message: string;
  transactionCode?: string;
  receiptSent?: boolean;
}

@Injectable({ providedIn: 'root' })
export class SumupService {
  /** Starts the SumUp login flow */
  async login(affiliateKey: string): Promise<{ resultCode: number; message: string }> {
    return SumupCapacitor.login({ affiliateKey });
  }

  /** Checks if the user is already logged in */
  async isLoggedIn(): Promise<boolean> {
    const res = await SumupCapacitor.isLoggedIn();
    return res.isLoggedIn;
  }

  /** Retrieves the current merchant details */
  async getCurrentMerchant(): Promise<{ merchantCode: string; currency: string }> {
    return SumupCapacitor.getCurrentMerchant();
  }

  /** Logs out the user */
  async logout(): Promise<void> {
    await SumupCapacitor.logout();
  }

  /** Prepares the terminal for checkout (warm-up) */
  async prepareForCheckout(): Promise<void> {
    await SumupCapacitor.prepareForCheckout();
  }

  /** Opens the card reader screen */
  async openCardReaderPage(): Promise<{ resultCode: number; message: string }> {
    return SumupCapacitor.openCardReaderPage();
  }

  /** Performs a payment with all available options */
  async checkout(options: PaymentOptions): Promise<PaymentResult> {
    return SumupCapacitor.checkout(options);
  }
}
