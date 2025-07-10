export interface SumupCapacitorPlugin {
  /**
   * Initializes the SumUp SDK. Must be called once before any other operations.
   */
  setup(options: { affiliateKey: string }): Promise<{ message: string }>;

  /**
   * Starts the login flow.
   */
  login(): Promise<{ resultCode: number; message: string }>;

  /**
   * Checks if the user is currently logged in.
   */
  isLoggedIn(): Promise<{ isLoggedIn: boolean }>;
  
  /**
   * Retrieves the details of the current merchant.
   */
  getCurrentMerchant(): Promise<{ merchantCode: string; currency: string }>;
  
  /**
   * Performs a logout.
   */
  logout(): Promise<void>;
  
  /**
   * Prepares the terminal for checkout (warm-up).
   */
  prepareForCheckout(): Promise<void>;
  
  /**
   * Opens the card reader settings screen.
   */
  openCardReaderPage(): Promise<{ resultCode: number; message: string }>;
  
  /**
   * Performs a payment.
   */
  checkout(options: {
    amount: number;
    currency?: string;
    title?: string;
    foreignTransactionId?: string;
  }): Promise<{
    resultCode: number;
    message: string;
    transactionCode?: string;
  }>;
}
