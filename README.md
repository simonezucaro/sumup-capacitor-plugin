# sumup-capacitor-plugin

SumUp SDK Capacitor Plugin

## Install

```bash
npm install sumup-capacitor-plugin
npx cap sync
```

## API

<docgen-index>

* [`login(...)`](#login)
* [`isLoggedIn()`](#isloggedin)
* [`getCurrentMerchant()`](#getcurrentmerchant)
* [`logout()`](#logout)
* [`prepareForCheckout()`](#prepareforcheckout)
* [`openCardReaderPage()`](#opencardreaderpage)
* [`checkout(...)`](#checkout)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### login(...)

```typescript
login(options: { affiliateKey: string; }) => Promise<{ resultCode: number; message: string; }>
```

| Param         | Type                                   |
| ------------- | -------------------------------------- |
| **`options`** | <code>{ affiliateKey: string; }</code> |

**Returns:** <code>Promise&lt;{ resultCode: number; message: string; }&gt;</code>

--------------------


### isLoggedIn()

```typescript
isLoggedIn() => Promise<{ isLoggedIn: boolean; }>
```

**Returns:** <code>Promise&lt;{ isLoggedIn: boolean; }&gt;</code>

--------------------


### getCurrentMerchant()

```typescript
getCurrentMerchant() => Promise<{ merchantCode: string; currency: string; }>
```

**Returns:** <code>Promise&lt;{ merchantCode: string; currency: string; }&gt;</code>

--------------------


### logout()

```typescript
logout() => Promise<void>
```

--------------------


### prepareForCheckout()

```typescript
prepareForCheckout() => Promise<void>
```

--------------------


### openCardReaderPage()

```typescript
openCardReaderPage() => Promise<{ resultCode: number; message: string; }>
```

**Returns:** <code>Promise&lt;{ resultCode: number; message: string; }&gt;</code>

--------------------


### checkout(...)

```typescript
checkout(options: { amount: number; currency?: string | undefined; title?: string | undefined; receiptEmail?: string | undefined; receiptSMS?: string | undefined; foreignTransactionId?: string | undefined; additionalInfo?: { [key: string]: string; } | undefined; retryEnabled?: boolean | undefined; retryInterval?: number | undefined; retryTimeout?: number | undefined; }) => Promise<{ resultCode: number; message: string; transactionCode?: string; receiptSent?: boolean; }>
```

| Param         | Type                                                                                                                                                                                                                                                               |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`options`** | <code>{ amount: number; currency?: string; title?: string; receiptEmail?: string; receiptSMS?: string; foreignTransactionId?: string; additionalInfo?: { [key: string]: string; }; retryEnabled?: boolean; retryInterval?: number; retryTimeout?: number; }</code> |

**Returns:** <code>Promise&lt;{ resultCode: number; message: string; transactionCode?: string; receiptSent?: boolean; }&gt;</code>

--------------------

</docgen-api>
