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
```

---

# 📱 Android Setup

### ✅ Requirements

| Requirement           | Version         |
| --------------------- | --------------- |
| Android SDK           | minSdkVersion 26 |
| Target SDK            | 31 or higher    |
| Android Gradle Plugin | 7.3.0 or later  |
| Kotlin                | 1.7.21 or later |
| Java                  | 11 or later     |

---

### 🔧 Configuration

1.  **Add SumUp Maven repository**
    In `android/build.gradle`:

    ```groovy
    allprojects {
        repositories {
            maven { url '[https://maven.sumup.com/releases](https://maven.sumup.com/releases)' }
            // ... other repositories ...
        }
    }
    ```

2.  **Add SumUp SDK dependency**
    In `android/app/build.gradle`:

    ```groovy
    dependencies {
        implementation 'com.sumup:merchant-sdk:5.0.3'
        // ... other dependencies ...
    }
    ```

3.  **Set minimum SDK version**
    In `gradle.properties` or `variables.gradle`:

    ```groovy
    minSdkVersion = 26
    ```

---

# 🍏 iOS Setup

### ✅ Requirements

| Requirement | Version          |
| ----------- | ---------------- |
| Xcode       | 14.x or later    |
| iOS SDK     | 13.0 or later    |
| Swift       | 5.0 or later     |
| CocoaPods   | 1.11.x or later  |

---

### 🔧 Configuration

1.  **Add SumUp SDK Dependency**
    The Capacitor plugin should already contain a `.podspec` file. Ensure it includes the `SumUpSDK` dependency. In `YourPluginName.podspec`:

    ```ruby
    s.dependency 'SumUpSDK'
    ```

2.  **Install Pods**
    After verifying the dependency, run the sync command from your project's root directory. This will install the SDK into your iOS project.

    ```bash
    npx cap sync ios
    ```

3.  **Configure Info.plist**
    The SumUp SDK requires several keys in your app's `Info.plist` file to function correctly. Open `ios/App/App/Info.plist` and add the following keys inside the main `<dict>` tag:

    ```xml
    <key>LSApplicationQueriesSchemes</key>
    <array>
        <string>sumup</string>
    </array>
    <key>CFBundleURLTypes</key>
    <array>
        <dict>
            <key>CFBundleURLSchemes</key>
            <array>
                <string>com.example.app.sumup</string>
            </array>
        </dict>
    </array>
    <key>NSBluetoothAlwaysUsageDescription</key>
    <string>This app needs Bluetooth access to connect to SumUp card readers.</string>
    ```
    -   `LSApplicationQueriesSchemes` allows your app to check if the SumUp app is installed.
    -   `CFBundleURLTypes` registers a unique URL scheme so that the SumUp app (or a web flow) can redirect back to your application after an operation. **You must replace `com.example.app.sumup` with a unique identifier for your app.**
    -   `NSBluetoothAlwaysUsageDescription` is the message shown to the user when the app requests Bluetooth permission to connect to a card reader.

---

# 🧪 Sample App

https://github.com/simonezucaro/sumup-capacitor-plugin/tree/main/demo-sumup-app

---

# 📚 API Reference

<docgen-index>

* [`setup(...)`](#setup)
* [`login()`](#login)
* [`isLoggedIn()`](#isloggedin)
* [`getCurrentMerchant()`](#getcurrentmerchant)
* [`logout()`](#logout)
* [`prepareForCheckout()`](#prepareforcheckout)
* [`openCardReaderPage()`](#opencardreaderpage)
* [`checkout(...)`](#checkout)

</docgen-index>

<docgen-api>
### setup(...)

```typescript
setup(options: { affiliateKey: string; }) => Promise<{ message: string; }>
```

Inizializza il SumUp SDK. Da chiamare una volta prima di altre operazioni.

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | `{ affiliateKey: string; }` |

**Returns:** `Promise<{ message: string; }>`

--------------------

### login()

```typescript
login() => Promise<{ resultCode: number; message: string; }>
```

Avvia il flow di login.

**Returns:** `Promise<{ resultCode: number; message: string; }>`

--------------------

### isLoggedIn()

```typescript
isLoggedIn() => Promise<{ isLoggedIn: boolean; }>
```

Controlla se l'utente è attualmente loggato.

**Returns:** `Promise<{ isLoggedIn: boolean; }>`

--------------------

### getCurrentMerchant()

```typescript
getCurrentMerchant() => Promise<{ merchantCode: string; currency: string; }>
```

Recupera i dettagli del merchant corrente.

**Returns:** `Promise<{ merchantCode: string; currency:string; }>`

--------------------

### logout()

```typescript
logout() => Promise<void>
```

Esegue il logout.

--------------------

### prepareForCheckout()

```typescript
prepareForCheckout() => Promise<void>
```

Prepara il terminale per il checkout (warm-up).

--------------------

### openCardReaderPage()

```typescript
openCardReaderPage() => Promise<{ resultCode: number; message: string; }>
```

Apre la schermata delle impostazioni del lettore di carte.

**Returns:** `Promise<{ resultCode: number; message: string; }>`

--------------------

### checkout(...)

```typescript
checkout(options: { amount: number; currency?: string; title?: string; foreignTransactionId?: string; }) => Promise<{ resultCode: number; message: string; transactionCode?: string; }>
```

Esegue un pagamento.

| Param         | Type                                                                                                |
| ------------- | --------------------------------------------------------------------------------------------------- |
| **`options`** | `{ amount: number; currency?: string; title?: string; foreignTransactionId?: string; }` |

**Returns:** `Promise<{ resultCode: number; message: string; transactionCode?: string; }>`

--------------------

</docgen-api>