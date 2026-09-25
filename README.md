# MyMarketPlace 🛒

**MyMarketPlace** is a modern, offline-first Android Marketplace application built following **Clean Architecture** principles and modern Android development best practices. It utilizes **Jetpack Compose**, **Hilt**, **Room**, **WorkManager**, **Retrofit**, **Coil**, and **Jetpack Security**.

---

## 🏗 Architecture Overview

The app strictly follows **Clean Architecture** with a unidirectional data flow (UDF) MVI/MVVM pattern:

```
                  ┌─────────────────────────────────────────┐
                  │            Presentation Layer           │
                  │  (Jetpack Compose UI, ViewModels, State) │
                  └────────────────────┬────────────────────┘
                                       │ Observes StateFlow
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │               Domain Layer              │
                  │     (Use Cases, Models, Repositories)   │
                  └────────────────────┬────────────────────┘
                                       │ Implements
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │                Data Layer               │
                  │  (Room SSOT DB, WorkManager, Retrofit)  │
                  └─────────────────────────────────────────┘
```

### Key Architectural Pillars
- **Offline-First Single Source of Truth (SSOT)**: Local Room DB is the primary source of truth for the UI layer. The UI continuously observes Room via `Flow`/`StateFlow`.
- **Background Synchronization**: `WorkManager` handles deferrable background sync (`SyncProductsWorker`) and image uploads (`UploadImageWorker`), ensuring execution across app restarts.
- **Conflict Resolution**: Implements a last-write-wins / timestamp-based merge strategy for offline changes.

---

## 🛠 Tech Stack & Libraries

| Category | Library / Tool | Description |
| :--- | :--- | :--- |
| **UI** | [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative UI framework |
| **Design** | [Material 3](https://developer.android.com/jetpack/compose/designsystems/material3) | Modern Material Design components |
| **Navigation** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) | Type-safe in-app screen navigation |
| **Dependency Injection** | [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) | Standardized DI solution for Android |
| **Local Database** | [Room](https://developer.android.com/training/data-storage/room) | SQLite abstraction layer with reactive Flow support |
| **Network** | [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson) | Mock REST API endpoints (200+ items, sync, create, update) |
| **Background Processing** | [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) | Reliable background synchronization & upload queue |
| **Image Caching** | [Coil](https://coil-kt.github.io/coil/) | Native Compose image loading with automatic memory/disk caching |
| **Security** | [Jetpack Security](https://developer.android.com/topic/security/data) | `EncryptedSharedPreferences` for AES-256 encrypted storage |
| **Hardware / Native** | Camera & Photo Picker | Native `TakePicture` and `PickVisualMedia` with runtime permissions |

---

## 📁 Package Structure

```
com.example.mymarketplace/
├── MarketPlaceApplication.kt        # Application class (@HiltAndroidApp)
├── MainActivity.kt                  # Entry point Activity (@AndroidEntryPoint)
├── domain/                          # Pure Kotlin domain logic
│   ├── model/                       # Product, ProductCategory
│   ├── repository/                  # Repository interfaces
│   └── usecase/                     # Modular Use Cases
├── data/                            # Data access implementations
│   ├── local/                       # Room Database, DAOs, Entities, Security
│   ├── remote/                      # Retrofit API, DTOs, Mock REST Service
│   ├── repository/                  # Repository implementations (SSOT)
│   ├── sync/                        # Network connectivity & sync manager
│   └── worker/                      # WorkManager sync workers
├── di/                              # Hilt Dependency Injection modules
└── presentation/                    # Jetpack Compose UI
    ├── splash/                      # Animated Splash Screen
    ├── product_list/                # Listings grid with search & category filters
    ├── product_detail/              # Details & Camera/Gallery photo attachment
    ├── favorites/                   # Bookmarked products screen
    └── navigation/                  # Screen routes & NavHost setup
```

---

## ✨ Features

1. **Animated Splash Screen**:
   - Smooth scale and fade branding transition on app launch.

2. **200+ Mock Listings & Search**:
   - Displays 200 listings in a responsive Compose grid with real-time search and category filtering.

3. **Live Sync Status Banner**:
   - Displays network connectivity and sync status (*"Online • Synchronized with Cloud"* vs *"Offline Mode • Persistence Active"*).

4. **Camera & Gallery Image Attachment**:
   - Take photos with the device Camera or select from Gallery with runtime `CAMERA` permission handling and FileProvider support.
   - Displays a background upload badge (`UploadImageWorker`) when offline.

5. **Efficient Image Caching**:
   - Downsampled thumbnails in lists to optimize memory and CPU usage via Coil.

6. **Favorites Management**:
   - Bookmark products and persist bookmarked status locally in Room database.

7. **Encrypted Storage**:
   - Uses `EncryptedSharedPreferences` for auth tokens and user session data.

---

## 🧪 Testing & Building

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Build Debug APK
```bash
./gradlew assembleDebug
```

---

## 📄 License
This project is open-source under the MIT License.
