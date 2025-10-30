# Backend Setup Guide

## 🚀 Quick Start

### 1. Start Backend Services
```bash
cd backend
docker-compose up --build
```

This will:
- Start PostgreSQL database on port 5432
- Start FastAPI backend on port 8000
- Auto-create all database tables

### 2. Access API Documentation
- **Interactive Docs**: http://localhost:8000/docs
- **Alternative Docs**: http://localhost:8000/redoc
- **Health Check**: http://localhost:8000/health

### 3. Test the API
```bash
# Create a user
curl -X POST "http://localhost:8000/api/users" \
  -H "Content-Type: application/json" \
  -d '{"name": "Rajesh"}'

# Create another user
curl -X POST "http://localhost:8000/api/users" \
  -H "Content-Type: application/json" \
  -d '{"name": "Lakshmi"}'

# Get all users
curl http://localhost:8000/api/users
```

## 📁 Backend Structure

```
backend/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI app initialization
│   ├── database.py          # Database connection
│   ├── models.py            # SQLAlchemy models
│   ├── schemas.py           # Pydantic schemas
│   └── routers/
│       ├── users.py         # User endpoints
│       ├── medicines.py     # Medicine CRUD + image upload
│       ├── reminders.py     # Reminders & logs
│       └── insulin_logs.py  # Insulin tracking
├── docker-compose.yml       # Docker services
├── Dockerfile              # Backend container
├── requirements.txt        # Python dependencies
└── README.md              # Detailed documentation
```

## 🔑 Key Features Implemented

### 1. User Management
- Create users with name and photo
- Support any family member (not limited to specific roles)
- Track individual medication per user
- Upload user photos

### 2. Medicine Management
- Add tablets/injection/insulin medicines
- **Tablet & Injection**: Same standard tracking process
- **Insulin**: Special flow with glucose readings required
- Upload medicine images
- Track dosage and instructions

### 3. Reminder System
- Schedule medication reminders
- Record intake logs (taken/missed/pending/snoozed)
- Track snooze count and timing

### 4. Insulin Tracking
- Record glucose readings
- Auto-suggest insulin dosage
- Daily/weekly/monthly statistics
- Glucose monitoring

### 5. Medicine Logs
- Track who took medicine
- Record timestamps
- View missed medicines
- Snooze functionality

## 🛠️ Useful Commands

### Stop Services
```bash
docker-compose down
```

### Stop and Clear Database
```bash
docker-compose down -v
```

### View Logs
```bash
docker-compose logs -f backend
```

### Access Database
```bash
docker-compose exec postgres psql -U medicine_user -d medicine_tracker_db
```

## 📊 Database Schema

### Tables Created:
1. **users** - User profiles with name and photo
2. **medicines** - Tablet/Injection/Insulin details
   - **Tablet & Injection**: Standard tracking
   - **Insulin**: Requires glucose readings
3. **reminders** - Scheduled medication times
4. **medicine_logs** - Actual intake records with status
5. **insulin_logs** - Glucose readings & insulin dosage (insulin type only)
6. **bookmarks** - Phone/WhatsApp contacts

## 🔗 API Endpoints Summary

### Users
- `POST /api/users` - Create user with name
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user
- `POST /api/users/{id}/upload-photo` - Upload user photo

### Medicines
- `POST /api/medicines` - Add medicine
- `GET /api/medicines?user_id={id}` - List medicines
- `PUT /api/medicines/{id}` - Update medicine
- `POST /api/medicines/{id}/upload-image` - Upload image

### Reminders & Logs
- `POST /api/reminders` - Create reminder schedule
- `POST /api/reminders/logs` - Record intake
- `GET /api/reminders/logs/missed` - Get missed medicines
- `PUT /api/reminders/logs/{id}` - Update status

### Insulin Tracking
- `POST /api/insulin` - Record glucose + insulin
- `GET /api/insulin/daily` - Daily logs
- `GET /api/insulin/weekly` - Weekly stats
- `GET /api/insulin/monthly` - Monthly stats
- `GET /api/insulin/suggest-dosage?glucose_reading={value}` - Get dosage suggestion

## 🔐 Security Notes

⚠️ **Before Production:**
1. Change `SECRET_KEY` in docker-compose.yml
2. Use strong PostgreSQL password
3. Enable HTTPS
4. Restrict CORS origins
5. Add authentication/authorization

## 🎯 Next Steps

Now that the backend is ready, you can:
1. Start the backend: `cd backend && docker-compose up`
2. Test endpoints using http://localhost:8000/docs
3. Begin Android app development
4. Connect Android app to backend API

## 💡 Development Workflow

When making changes:
1. **Backend changes**: Edit files in `backend/app/`, Docker auto-reloads
2. **Frontend changes**: Update Android app to call backend APIs
3. **Database changes**: Modify `models.py`, restart Docker

---

**Backend is ready! 🎉** Start it with `docker-compose up` and access docs at http://localhost:8000/docs

