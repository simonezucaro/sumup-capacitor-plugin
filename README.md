`markdown
# sumup-capacitor-plugin

SumUp SDK Capacitor Plugin

## Prerequisites

* Registered for a merchant account via SumUp’s country website (or received a test account)
* Received a SumUp card terminal (Solo, Air, Air Lite or PIN+ Terminal)
* Requested an **Affiliate (Access) Key** via the SumUp Dashboard for Developers


* In **android/build.gradle** add the SumUp Maven repository:

```groovy
allprojects {
    repositories {
        maven { url 'https://maven.sumup.com/releases' }
        // … other repositories …
    }
}
````

* In **android/app/build.gradle** add the SumUp SDK dependency:

```
dependencies {
    implementation 'com.sumup:merchant-sdk:5.0.3'
    // … other dependencies …
}
```

* In **variables.gradle** (or `gradle.properties`) ensure:
  minSdkVersion = 26

* **SDK requirements**:

  * Android SDK: `minSdkVersion` 26 or later
  * Target SDK: 31 or later
  * Android Gradle Plugin (AGP): 7.3.0 or later
  * Kotlin: 1.7.21 or later
  * Java: 11 or later

## Install

```
npm install sumup-capacitor-plugin
npx cap sync
```

## Sample App
Coming soon...


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

