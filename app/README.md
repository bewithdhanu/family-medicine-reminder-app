# 📱 Medicine Tracker - Android App

Family Medication Reminder and Launcher Application for elderly parents.

---

## 🚀 Quick Start

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9+

### Setup (2 minutes)

1. **Clone the repository**
```bash
git clone https://github.com/bewithdhanu/your-repo-name.git
cd your-repo-name
```

2. **Configure API credentials**
```bash
# Copy template
cp local.properties.example local.properties

# Edit local.properties and add your API details
```

3. **Sync & Build**
- Open project in Android Studio
- Wait for Gradle sync
- Click Run ▶️

---

## ⚙️ Configuration

### ⚠️ IMPORTANT: API Configuration Required!

This app **requires** `local.properties` with API configuration:

```properties
# local.properties (create this file)
API_BASE_URL=https://your-api-domain.com/
API_KEY=your-api-key-here
```

See [CONFIGURATION.md](../CONFIGURATION.md) for detailed setup.

---

## 🏗️ Architecture

### MVVM Pattern
```
UI (Compose)
    ↓
ViewModel
    ↓
Repository
    ↓
Data Sources (API + Room DB)
```

### Layers

#### 1. **Data Layer** (`data/`)
- `model/` - Data models matching API
- `remote/` - API service & networking
- `local/` - Room database (coming soon)
- `repository/` - Data coordination

#### 2. **Domain Layer** (coming soon)
- Use cases
- Business logic

#### 3. **Presentation Layer** (`ui/`)
- Compose UI components
- ViewModels
- Navigation

---

## 🔐 Security Features

### ✅ No Hardcoded Secrets
- API endpoint from `BuildConfig`
- API key from `BuildConfig`
- Both loaded from gitignored `local.properties`

### ✅ Automatic Authentication
```kotlin
// AuthInterceptor automatically adds API key
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", ApiConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}
```

### ✅ Encrypted Storage (planned)
- EncryptedSharedPreferences for sensitive data
- Android Keystore for secure key storage

---

## 📦 Tech Stack

### Core
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM
- **DI:** Manual (Koin/Hilt coming soon)

### Networking
- **HTTP Client:** Retrofit + OkHttp
- **Serialization:** Gson
- **API Auth:** Custom interceptor with API key

### Storage
- **Preferences:** DataStore
- **Database:** Room (coming soon)
- **Secure Storage:** EncryptedSharedPreferences

### Background Tasks
- **Reminders:** AlarmManager (coming soon)
- **Work:** WorkManager

### Image Loading
- **Library:** Coil

---

## 📂 Project Structure

```
app/src/main/java/in/bewithdhanu/medicinetracker/
├── data/
│   ├── model/
│   │   └── Models.kt           # API data models
│   ├── remote/
│   │   ├── ApiConfig.kt        # Configuration (from BuildConfig)
│   │   ├── ApiService.kt       # Retrofit API interface
│   │   ├── AuthInterceptor.kt  # Adds API key to requests
│   │   └── RetrofitClient.kt   # Network setup
│   ├── local/                  # Room DB (coming soon)
│   └── repository/             # Repository pattern (coming soon)
├── domain/                     # Use cases (coming soon)
├── ui/
│   ├── theme/                  # Material 3 theme
│   ├── screens/                # Compose screens (coming soon)
│   └── components/             # Reusable components (coming soon)
└── MainActivity.kt             # App entry point
```

---

## 🌐 API Integration

### Configured Automatically
```kotlin
// All requests automatically authenticated
val apiService = RetrofitClient.apiService

// Get users (API key added automatically)
val users = apiService.getUsers()
```

### Available Endpoints

#### Users
- `getUsers()` - List all users
- `getUser(id)` - Get user details
- `createUser(request)` - Create user
- `updateUser(id, request)` - Update user
- `deleteUser(id)` - Delete user

#### Medicines
- `getMedicines(userId?, isActive?)` - List medicines
- `getMedicine(id)` - Get medicine details
- `createMedicine(request)` - Create medicine
- `updateMedicine(id, request)` - Update medicine
- `deleteMedicine(id)` - Delete medicine

#### Reminders
- `getReminders(medicineId?)` - List reminders
- `createReminder(request)` - Create reminder
- `deleteReminder(id)` - Delete reminder

#### Medicine Logs
- `getMedicineLogs(userId?, medicineId?)` - Get logs
- `getMissedMedicines(userId?)` - Get missed
- `createMedicineLog(request)` - Log medicine intake
- `updateMedicineLog(id, request)` - Update log

#### Insulin
- `getInsulinLogs(userId?)` - Get insulin logs
- `getDailyInsulinLogs(userId, date?)` - Daily logs
- `getWeeklyInsulinStats(userId)` - Weekly stats
- `getMonthlyInsulinStats(userId)` - Monthly stats
- `suggestInsulinDosage(glucoseReading)` - Get dosage suggestion
- `createInsulinLog(request)` - Record insulin

---

## 🎨 Design System

### Material 3 Dynamic Colors
- Auto light/dark theme
- System-aware color scheme
- Android 12+ dynamic colors

### Typography
- **Larger fonts** for elderly users
- Telugu & English support
- Clear, readable text

### UI/UX Principles
- **Clean** - Minimal interface
- **Accessible** - Large touch targets
- **Tablet-optimized** - Landscape support
- **Medicine photos** - Visual aids

---

## 🌍 Language Support

### Supported Languages
- 🇮🇳 Telugu (te) - Primary
- 🇬🇧 English (en) - Secondary

### Implementation
```kotlin
// All strings in resources
strings.xml (English)
strings-te.xml (Telugu)

// Access in code
stringResource(R.string.app_name)
```

---

## 🔔 Features (Planned)

### ✅ Implemented
- [x] Secure API configuration
- [x] Network layer with authentication
- [x] Data models for all entities
- [x] Material 3 theme with dark mode

### 🚧 In Progress
- [ ] User management UI
- [ ] Medicine management UI
- [ ] Reminder setup UI
- [ ] Dashboard/Home screen

### 📋 Planned
- [ ] AlarmManager for reminders
- [ ] Full-screen alert dialogs
- [ ] Audio notifications
- [ ] Camera/Gallery for medicine photos
- [ ] Insulin tracking with glucose
- [ ] Communication launcher (Phone/WhatsApp)
- [ ] Room database for offline support
- [ ] WorkManager for background sync
- [ ] Widget for quick access

---

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

---

## 🚨 Common Issues

### "API_BASE_URL not configured"
**Solution:** Create `local.properties` with API configuration. See [CONFIGURATION.md](../CONFIGURATION.md).

### "Unresolved reference: BuildConfig"
**Solution:** Sync Gradle project in Android Studio.

### API calls return 401
**Solution:** Verify `API_KEY` in `local.properties` matches your backend.

---

## 📚 Documentation

- [Configuration Guide](../CONFIGURATION.md) - API setup
- [Project Rules](../.cursorrules) - Development guidelines
- [Backend API](../backend/README.md) - API documentation
- [Security Guide](../backend/SECURITY.md) - Security details

---

## 🤝 Contributing

1. Never commit `local.properties`
2. Never hardcode API_BASE_URL or API_KEY
3. Follow Material 3 design guidelines
4. Support both Telugu and English
5. Test on tablets
6. Consider elderly users (larger fonts, simple UI)

---

## 📞 Support

- **Backend API:** https://your-api-domain.com/
- **Repository:** https://github.com/bewithdhanu/your-repo-name

---

**Built with ❤️ for family**

