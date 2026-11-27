# Spending

Spending is a modern, offline-first Android personal finance and expense tracking application built entirely with Kotlin, Jetpack Compose, and Material 3. The app enables users to track daily expenses, visualize financial reports, manage multi-currency transactions with dynamic exchange rates, and protect their privacy with biometric security.

The codebase strictly adheres to **Clean Architecture** principles across a multi-module setup (`:app`, `:data`, `:domain`), ensuring high testability, scalability, and strict separation of concerns.

---

## ✨ Features

- **Offline-First & Auto-Sync**: Log transactions locally instantly using Room Database. Changes automatically sync to Cloud Firestore when network connectivity is restored.
- **Dynamic Multi-Currency & Rate Healing**: Fetch live exchange rates and calculate multi-currency transactions locally. Automatically heals missing historical rates in the background.
- **Comprehensive Expense Reports**: Interactive period selector (Weekly, Monthly, Yearly, Custom Date Range) with category breakdowns, percentage charts, and net balance summaries.
- **Flexible Authentication**: Sign in using Google Authentication or Email/Password. Supports password recovery via email.
- **App Lock & Biometric Security**: Secure app access using Fingerprint, Face, or Device Credentials. Automatically locks when backgrounded and requires authentication to disable.
- **Screen Privacy Protection**: Toggle `FLAG_SECURE` to block screenshots and hide app contents in recent app previews.
- **Customizable Notifications**: Set daily expense logging reminders with a custom Material 3 time picker and weekly summary notifications.
- **Dynamic Dark & Light Themes**: Seamlessly follows System, Dark, or Light themes with custom color palettes.

---

## 🏗️ Architecture

JetSpending is structured as a 3-module Clean Architecture codebase following the **MVVM (Model-View-ViewModel)** design pattern with unidirectional data flow (UDF):

```
                       ┌─────────────────────────┐
                       │  :app (Presentation)    │
                       │  Jetpack Compose, UI    │
                       └────────────┬────────────┘
                                    │
                                    ▼
                       ┌─────────────────────────┐
                       │    :domain (Core)       │
                       │  Use Cases, Models      │
                       └────────────▲────────────┘
                                    │
                       ┌────────────┴────────────┐
                       │     :data (Data)        │
                       │  Room, DataStore,       │
                       │  Retrofit, Firebase     │
                       └─────────────────────────┘
```

* **`:app` (Presentation Layer)**: Houses Jetpack Compose UI screens, state hoisting (`UiState`), ViewModels, Navigation Graphs (`AppNavHost`), and system integrations (Biometric, Window Flags).
* **`:domain` (Domain Layer)**: Pure Kotlin module with zero Android framework dependencies. Defines data models (`Transaction`, `User`, `AppCurrency`), repository interfaces, and focused Use Cases (`SignInUseCase`, `SignUpUseCase`, `GetTransactionsUseCase`, etc.).
* **`:data` (Data Layer)**: Implements repository interfaces using Room Database, DataStore Preferences, Retrofit REST API, and Firebase (Auth + Firestore). Handles background alarm scheduling (`AlarmManager`, `BootReceiver`) and offline sync.

---

## 🛠️ Tech Stack & Libraries

* **Language**: Kotlin
* **UI Framework**: Jetpack Compose & Material 3
* **Architecture**: Clean Architecture (Multi-module) + MVVM + Unidirectional Data Flow
* **Dependency Injection**: Hilt (Dagger)
* **Local Storage**:
  * **Room**: Local SQLite relational database with Paging 3 support.
  * **Preferences DataStore**: Reactive type-safe user settings and session caching.
* **Networking**: Retrofit 2, Gson, OkHttp
* **Cloud & Backend**: Firebase Authentication & Cloud Firestore
* **Asynchronous Programming**: Kotlin Coroutines & `Flow` / `StateFlow`
* **Security & Biometrics**: AndroidX Biometric API & Window `FLAG_SECURE`
* **Notifications**: `AlarmManager` with `BroadcastReceiver` and `NotificationManagerCompat`
* **Image Loading**: Coil 3 (Compose)

---

## 📁 Project Module Breakdown

```text
JetSpending/
├── app/                  # UI, Compose Screens, ViewModels, MainActivity, Hilt DI
│   ├── ui/
│   │   ├── screen/       # Dashboard, Reports, Transactions, Settings, Auth, Security
│   │   ├── components/   # Reusable Material 3 Compose UI components
│   │   ├── theme/        # Material 3 Color scheme, Typography, Theme definition
│   │   └── main/         # MainActivity, AppNavHost, MainViewModel
├── domain/               # Pure Kotlin Core Domain
│   ├── model/            # Transaction, User, Currency, NotificationPreferences
│   ├── repository/       # Repository Interfaces (IUserRepository, ITransactionRepository, etc.)
│   └── usecase/          # Granular Use Cases (SignInUseCase, GetTransactionsUseCase, etc.)
└── data/                 # Data Sources & Repositories
    ├── repository/       # Repository implementations (UserRepository, AuthenticationRepository, etc.)
    ├── source/
    │   ├── local/        # Room Database, DAOs, UserPreferences DataStore
    │   └── remote/       # Retrofit ApiService, Firebase Firestore & Auth
    └── notification/     # AlarmManager Scheduler, BootReceiver, ReminderReceiver
```

---

## 🚀 Getting Started

### Prerequisites

* **Android Studio**: Ladybug or newer
* **JDK**: 17 or higher
* **Android Device / Emulator**: Android 7.1 (API Level 25) or higher
* **Firebase Project**: Configured for Google Sign-In and Cloud Firestore

### Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/KevinFrederick/Spending.git
   cd Spending
   ```

2. **Configure Local Properties**:
   Create a `local.properties` file in the root directory (if not present) and add your Google Web Client ID for Authentication:
   ```properties
   GOOGLE_WEB_CLIENT_ID=your_google_web_client_id_here
   ```

3. **Configure Firebase**:
   * Create an Android App in your [Firebase Console](https://console.firebase.google.com/).
   * Enable **Email/Password** and **Google** sign-in providers in Firebase Authentication.
   * Enable **Cloud Firestore** database.
   * Download `google-services.json` and place it in the `app/` directory.

4. **Build and Run**:
   * Open the project in Android Studio.
   * Sync Gradle files (`Gradle Sync`).
   * Select an emulator or connected device and press **Run (Shift + F10)**.

---
