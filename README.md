# JetSpending

JetSpending is a modern, offline-first Android personal finance and expense tracking application. Built entirely with Kotlin and Jetpack Compose, the app helps users manage their daily expenses, visualize their spending habits, and handle multi-currency transactions seamlessly.

The project strictly adheres to **Clean Architecture** principles, ensuring a scalable, testable, and maintainable codebase separated into Presentation, Domain, and Data layers.

## ✨ Features

* **Offline-First Capabilities:** Logs transactions locally using Room Database, automatically syncing with Firebase Firestore when network connectivity is restored.

* **Smart Multi-Currency Support:** Pulls daily exchange rates via a remote API and caches them locally. Converts currencies instantly without requiring an API call for every transaction.

* **Comprehensive Reports Screen:** Visualizes spending habits with interactive bar charts and category-based percentage breakdowns in a dedicated reporting view.

* **Comprehensive Authentication:** Secure your financial data with Google Sign-In, standard Email/Password, or local device Biometric/Fingerprint authentication.

* **Scheduled Reminders:** Local notification system (surviving device reboots) to remind users to log their daily expenses.

* **Flexible Filtering:** Filter transactions by custom date ranges, income/expense types, and custom categories.

## 🏗️ Architecture

JetSpending follows the **Clean Architecture** pattern, utilizing the MVVM (Model-View-ViewModel) design pattern in the presentation layer. The project is divided into three distinct modules:

* **`app` (Presentation Layer):** Contains all Jetpack Compose UI elements, ViewModels, and UI States (e.g., `UiState`, `TransactionState`).

* **`domain` (Domain Layer):** The core business logic hub containing pure Kotlin models (e.g., `Transaction`, `Category`), repository interfaces, and Use Cases/Interactors.

* **`data` (Data Layer):** Manages data fetching, saving, and caching. Implements the Single Source of Truth pattern using Room, DataStore, Retrofit, and Firebase.

## 🛠️ Tech Stack

* **Language:** Kotlin

* **UI:** Jetpack Compose

* **Architecture:** Clean Architecture + MVVM

* **Local Database:** Room

* **Preferences:** Preferences Datastore

* **Networking:** Retrofit & OkHttp

* **Backend & Sync:** Firebase Authentication & Firestore

* **Asynchronous Programming:** Kotlin Coroutines & Flow

* **Security:** AndroidX Biometric API

## 🚀 Getting Started

### Prerequisites

* Android Studio

* JDK 17

* A Firebase Project (for Authentication and Firestore)

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/KevinFrederick/Spending.git
   ```

2. **Open the project** in Android Studio.

3. **Configure Firebase:**

   * Register your app in your Firebase console.

   * Download the `google-services.json` file and place it in the `app/` directory.

4. **Build and Run:**

   * Sync the project with Gradle files.

   * Select an emulator or physical device and click Run.
