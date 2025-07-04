export interface SumupCapacitorPlugin {
  login(options: { affiliateKey: string }): Promise<{ resultCode: number, message: string }>;
  isLoggedIn(): Promise<{ isLoggedIn: boolean }>;
  getCurrentMerchant(): Promise<{ merchantCode: string, currency: string }>;
  logout(): Promise<void>;
  prepareForCheckout(): Promise<void>;
  openCardReaderPage(): Promise<{ resultCode: number, message: string }>;
  checkout(options: {
    amount: number;
    currency?: string;
    title?: string;
    receiptEmail?: string;
    receiptSMS?: string;
    foreignTransactionId?: string;
    additionalInfo?: { [key: string]: string };
    retryEnabled?: boolean;
    retryInterval?: number;
    retryTimeout?: number;
  }): Promise<{
    resultCode: number;
    message: string;
    transactionCode?: string;
    receiptSent?: boolean;
  }>;
}
