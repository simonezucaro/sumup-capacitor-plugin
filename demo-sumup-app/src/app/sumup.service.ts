import { Injectable } from '@angular/core';
// Make sure the imported plugin name is correct
import { SumupCapacitorPlugin } from 'sumup-capacitor-plugin';

// The PaymentOptions and PaymentResult interfaces remain unchanged
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

  // Add a state to know if the service is ready
  private isInitialized = false;

  constructor() {}

  /**
   * ✅ NEW METHOD: Initializes the SumUp SDK.
   * Must be called ONCE before any other operations.
   */
  async setup(affiliateKey: string): Promise<void> {
    if (this.isInitialized) {
      console.warn('SumUp SDK is already initialized.');
      return;
    }
    
    try {
      await SumupCapacitorPlugin.setup({ affiliateKey });
      this.isInitialized = true;
      console.log('✅ SumupService is ready.');
    } catch (error) {
      this.isInitialized = false;
      console.error('❌ Error during SumUp setup:', error);
      // Re-throw the error to allow the component to handle it
      throw error;
    }
  }

  /** * ✅ MODIFIED METHOD: Starts the SumUp login flow.
   * No longer requires the API key.
   */
  async login(): Promise<{ resultCode: number; message: string }> {
    if (!this.isInitialized) {
      throw new Error('SumUp is not initialized. Please call the setup() method first.');
    }
    return SumupCapacitorPlugin.login();
  }

  /** Checks if the user is already logged in */
  async isLoggedIn(): Promise<boolean> {
    if (!this.isInitialized) return false;
    const res = await SumupCapacitorPlugin.isLoggedIn();
    return res.isLoggedIn;
  }
  
  /** Performs a payment with all available options */
  async checkout(options: PaymentOptions): Promise<PaymentResult> {
    if (!this.isInitialized) {
      throw new Error('SumUp is not initialized. Please call the setup() method first.');
    }
    return SumupCapacitorPlugin.checkout(options);
  }

  // --- OTHER METHODS REMAIN UNCHANGED BUT BENEFIT FROM THE CHECK ---

  async getCurrentMerchant(): Promise<{ merchantCode: string; currency: string }> {
    if (!this.isInitialized) throw new Error('SumUp is not initialized.');
    return SumupCapacitorPlugin.getCurrentMerchant();
  }

  async logout(): Promise<void> {
    if (!this.isInitialized) throw new Error('SumUp is not initialized.');
    await SumupCapacitorPlugin.logout();
  }

  async prepareForCheckout(): Promise<void> {
    if (!this.isInitialized) throw new Error('SumUp is not initialized.');
    await SumupCapacitorPlugin.prepareForCheckout();
  }

  async openCardReaderPage(): Promise<{ resultCode: number; message: string }> {
    if (!this.isInitialized) throw new Error('SumUp is not initialized.');
    return SumupCapacitorPlugin.openCardReaderPage();
  }
}
