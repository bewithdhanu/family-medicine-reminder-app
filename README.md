# 💊 Family Medicine Reminder & Tracker

> **A comprehensive medication management app for elderly parents with intelligent reminders, insulin tracking, and family launcher features**

[![Android](https://img.shields.io/badge/Android-Kotlin-green?logo=android)](https://kotlinlang.org/)
[![Backend](https://img.shields.io/badge/Backend-FastAPI-009688?logo=fastapi)](https://fastapi.tiangolo.com/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🌟 Overview

A tablet-optimized medication reminder and tracking application specifically designed for elderly parents. Features intelligent insulin management with glucose monitoring, multi-user support, and a simplified launcher interface for quick communication access.

### 🎯 Key Features

- 👥 **Multi-User Support** - Track medications for any family member with personalized profiles
- 💊 **Three Medicine Types**
  - 💊 **Tablets** - Standard medication tracking
  - 💉 **Injections** - Same as tablets
  - 🩸 **Insulin** - Advanced glucose monitoring with dosage suggestions
- ⏰ **Smart Alert System** - Full-screen alerts with medicine photos and audio notifications
- 🔄 **Auto-Snooze Logic** - 1 min → 10 min snooze × 3 → Auto-mark as pending
- 📊 **Insulin Analytics** - Daily/weekly/monthly glucose and insulin tracking
- 🌐 **Bilingual** - Telugu (తెలుగు) & English support
- 🎨 **Elderly-Friendly UI** - Large fonts, clean design, tablet-optimized
- 📱 **Launcher Features** - Quick access to phone & WhatsApp calls
- 🌙 **Auto Theme** - Follows system light/dark mode

## 📸 Screenshots

*Coming soon...*

## 🏗️ Architecture

### Frontend
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM
- **Database:** Room
- **Reminders:** AlarmManager / WorkManager

### Backend
- **Framework:** FastAPI (Python)
- **Database:** PostgreSQL
- **Containerization:** Docker + Docker Compose
- **API Documentation:** Auto-generated Swagger/OpenAPI

## 🚀 Quick Start

### Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Start services with Docker Compose:**
```bash
docker-compose up --build
```

3. **Access API:**
- API: http://localhost:8001
- Documentation: http://localhost:8001/docs
- Health Check: http://localhost:8001/health

### Android App Setup

1. **Open in Android Studio:**
```bash
# Open the project folder in Android Studio
```

2. **Sync Gradle:**
- Wait for Gradle sync to complete

3. **Run the app:**
- Select device/emulator
- Click Run ▶️

## 📚 Documentation

- [Backend Setup Guide](BACKEND_SETUP.md)
- [API Test Results](backend/API_TEST_RESULTS.md)
- [Backend API Documentation](backend/README.md)
- [Requirements Document](requirements.md)
- [Project Rules](.cursorrules)

## 🔧 API Endpoints

### Users
- `POST /api/users/` - Create user
- `GET /api/users/` - List all users
- `PUT /api/users/{id}` - Update user
- `POST /api/users/{id}/upload-photo` - Upload photo

### Medicines
- `POST /api/medicines/` - Add medicine
- `GET /api/medicines/` - List medicines (filter by user)
- `PUT /api/medicines/{id}` - Update medicine
- `POST /api/medicines/{id}/upload-image` - Upload medicine image

### Reminders & Logs
- `POST /api/reminders/` - Create reminder
- `POST /api/reminders/logs` - Log medicine intake
- `GET /api/reminders/logs/missed` - Get missed medicines
- `PUT /api/reminders/logs/{id}` - Update status (snooze/taken)

### Insulin Tracking
- `POST /api/insulin/` - Record glucose & insulin
- `GET /api/insulin/suggest-dosage` - Get dosage suggestion
- `GET /api/insulin/daily` - Daily logs
- `GET /api/insulin/weekly` - Weekly statistics
- `GET /api/insulin/monthly` - Monthly statistics

## 📊 Database Schema

```
users (id, name, photo_url, timestamps)
├── medicines (id, user_id, name, type, dosage, image_url, ...)
│   └── reminders (id, medicine_id, scheduled_time, ...)
│       └── medicine_logs (id, user_id, medicine_id, status, taken_at, snooze_count, ...)
└── insulin_logs (id, user_id, glucose_reading, insulin_dosage, suggested_dosage, ...)
```

## 💉 Insulin Management

### Glucose-Based Dosage Suggestions

| Glucose Level | Suggested Insulin | Status |
|--------------|------------------|--------|
| < 70 mg/dL | 0 units | Low - No insulin |
| 70-120 mg/dL | 2 units | Normal range |
| 120-180 mg/dL | 4 units | Slightly elevated |
| 180-250 mg/dL | 6 units | High |
| > 250 mg/dL | 8 units | Very high |

⚠️ **Disclaimer:** This is a simplified algorithm. Always consult healthcare professionals for actual medical decisions.

## 🔔 Alert System

1. **Initial Alert** - Full screen with medicine image
2. **Wait 1 minute** - For user response
3. **Snooze 10 minutes** - If not responded (repeat 3 times)
4. **Auto-mark pending** - After 3 snoozes (30 minutes)
5. **Manual snooze** - 30 minutes option available

## 🌍 Language Support

- **Telugu (తెలుగు)** - Primary language
- **English** - Secondary language

All user-facing strings available in both languages using Android string resources.

## 🎨 UI/UX Features

- ✅ Tablet-optimized layout (10"+ screens)
- ✅ Extra-large fonts for elderly users
- ✅ Clean, minimal interface
- ✅ Medicine photos on alerts
- ✅ Auto light/dark theme
- ✅ Material 3 dynamic colors (Android 12+)

## 🛠️ Tech Stack

### Android
- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- DataStore (preferences)
- AlarmManager / WorkManager
- Coil (image loading)
- Retrofit (API calls)

### Backend
- Python 3.11
- FastAPI
- SQLAlchemy
- PostgreSQL 16
- Pydantic
- Uvicorn
- Docker & Docker Compose

## 📝 Development

### Prerequisites
- Android Studio (latest)
- JDK 11+
- Docker Desktop
- Python 3.11+ (optional, for local backend dev)

### Project Structure
```
medicine-tracker/
├── app/                    # Android app
│   ├── src/main/
│   │   ├── java/          # Kotlin source files
│   │   └── res/           # Resources
│   └── build.gradle.kts
├── backend/               # FastAPI backend
│   ├── app/
│   │   ├── models.py     # Database models
│   │   ├── schemas.py    # Pydantic schemas
│   │   ├── routers/      # API endpoints
│   │   └── main.py       # FastAPI app
│   ├── docker-compose.yml
│   └── requirements.txt
├── requirements.md        # Detailed requirements
├── .cursorrules          # Project rules
└── README.md
```

## 🧪 Testing

Backend APIs fully tested:
- ✅ 31+ endpoints tested
- ✅ All CRUD operations working
- ✅ Insulin dosage calculations verified
- ✅ Statistics aggregations correct
- ✅ Multi-user filtering working

See [API Test Results](backend/API_TEST_RESULTS.md) for detailed test report.

## 🚦 Current Status

- ✅ Backend API - **Production Ready**
- ✅ Database Schema - **Complete**
- ✅ Docker Setup - **Working**
- ✅ API Documentation - **Available**
- 🚧 Android App - **In Development**
- 📋 UI/UX Design - **Planned**

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

Built with ❤️ for elderly parents who need reliable medication management.

## 🙏 Acknowledgments

- Designed for elderly care and family health management
- Inspired by the need for simple, reliable medication tracking
- Built with modern Android and Python best practices

---

**⚠️ Medical Disclaimer:** This application is for medication tracking and reminder purposes only. It does not provide medical advice. Always consult healthcare professionals for medical decisions, especially regarding insulin dosage.

---

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Check the [documentation](backend/README.md)
- Review [API docs](http://localhost:8001/docs) (when backend is running)

---

**Made with 💊 for better health management**

