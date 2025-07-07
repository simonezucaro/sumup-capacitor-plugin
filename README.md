---

````markdown
# sumup-capacitor-plugin

Capacitor Plugin for the SumUp SDK – Native card terminal integration for **Android** and **iOS**.

---

## ✅ Prerequisites (iOS&Android)

- A [SumUp Merchant Account](https://www.sumup.com)
- A supported SumUp card terminal (Solo, Solo Lite, Air, 3G, PIN+)
- An **Affiliate (Access) Key** from the [SumUp Developer Dashboard](https://developer.sumup.com)
- Add the **Application ID** to the Affiliate Key (ex. com.example.app)

---

## 📦 Install

```bash
npm install sumup-capacitor-plugin
npx cap sync
````

---

# 📱 Android Setup

### ✅ Requirements

| Requirement           | Version          |
| --------------------- | ---------------- |
| Android SDK           | minSdkVersion 26 |
| Target SDK            | 31 or higher     |
| Android Gradle Plugin | 7.3.0 or later   |
| Kotlin                | 1.7.21 or later  |
| Java                  | 11 or later      |

---

### 🔧 Configuration

1. **Add SumUp Maven repository**
   In `android/build.gradle`:

```groovy
allprojects {
    repositories {
        maven { url 'https://maven.sumup.com/releases' }
        // ... other repositories ...
    }
}
```

2. **Add SumUp SDK dependency**
   In `android/app/build.gradle`:

```groovy
dependencies {
    implementation 'com.sumup:merchant-sdk:5.0.3'
    // ... other dependencies ...
}
```

3. **Set minimum SDK version**
   In `gradle.properties` or `variables.gradle`:

```groovy
minSdkVersion = 26
```

---

# 🍏 iOS Setup

### ✅ Requirements

| Requirement    | Version          |
| -------------- | ---------------- |
| iOS Deployment | 14.0 or later    |
| Xcode          | 14.3.1 or later  |
| iOS SDK        | 16 or later      |
| Device         | Real iPhone/iPad |

---

### ⚠️ SumUp iOS SDK not included

Due to size and licensing restrictions, the `SumUpSDK.xcframework` is **not included** in this plugin by default.

You must manually download and link it.

---

### 🔧 Manual Setup

1. **Download SDK**

👉 Download `SumUpSDK.xcframework` from:
[https://sumup-developer.sumup-vercel.app/tools/sdks/ios-sdk](https://sumup-developer.sumup-vercel.app/tools/sdks/ios-sdk)

2. **Copy into plugin folder**

Place the SDK here:

```
ios/SumUpSDK/SumUpSDK.xcframework
```

3. **Add to Xcode**

Open your app with:

```bash
npx cap open ios
```

Then in Xcode:

* Go to **File > Add Files to “App”**
* Select `SumUpSDK.xcframework`
* Go to **Build Phases > Link Binary With Libraries** → Add `SumUpSDK.xcframework`
* Go to **Build Settings > Framework Search Paths** and add:

```
$(SRCROOT)/SumUpSDK
```

(Set the value to `recursive`)

---

### 🔧 Info.plist Permissions

Open `ios/App/App/Info.plist` and add:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Necessary for finding nearby SumUp card readers</string>

<key>NSBluetoothAlwaysUsageDescription</key>
<string>Used to connect to SumUp card readers</string>

<key>NSBluetoothPeripheralUsageDescription</key>
<string>Required for Bluetooth access on iOS 12 and earlier</string>
```

---

### 🧭 Orientation (iOS only)

On iPhone, SumUp SDK only supports **portrait** mode.
On iPad, all orientations are supported.

In `Info.plist` or Xcode target:

```xml
<key>UISupportedInterfaceOrientations</key>
<array>
    <string>UIInterfaceOrientationPortrait</string>
</array>
```

---

# 🧪 Sample App

Coming soon...

---

# 📚 API Reference

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
