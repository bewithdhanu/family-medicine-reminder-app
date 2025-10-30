# Medicine Tracker Backend API

FastAPI backend for the Family Medication Reminder and Launcher Application.

## Tech Stack
- **Framework**: FastAPI
- **Database**: PostgreSQL
- **ORM**: SQLAlchemy
- **Containerization**: Docker + Docker Compose

## Quick Start

### Prerequisites
- Docker
- Docker Compose

### Running the Backend

1. **Start the services** (PostgreSQL + FastAPI):
```bash
cd backend
docker-compose up --build
```

2. **Access the API**:
   - API Base URL: http://localhost:8000
   - Interactive API Docs: http://localhost:8000/docs
   - Alternative API Docs: http://localhost:8000/redoc

3. **Stop the services**:
```bash
docker-compose down
```

4. **Stop and remove volumes** (clears database):
```bash
docker-compose down -v
```

## API Endpoints

### Users
- `POST /api/users` - Create new user with name and photo
- `GET /api/users` - Get all users
- `GET /api/users/{user_id}` - Get user by ID
- `PUT /api/users/{user_id}` - Update user details
- `POST /api/users/{user_id}/upload-photo` - Upload user photo
- `DELETE /api/users/{user_id}` - Delete user

### Medicines
- `POST /api/medicines` - Add new medicine
- `GET /api/medicines` - Get all medicines (with filters)
- `GET /api/medicines/{medicine_id}` - Get medicine details
- `PUT /api/medicines/{medicine_id}` - Update medicine
- `DELETE /api/medicines/{medicine_id}` - Deactivate medicine
- `POST /api/medicines/{medicine_id}/upload-image` - Upload medicine image

### Reminders
- `POST /api/reminders` - Create reminder schedule
- `GET /api/reminders` - Get all reminders
- `DELETE /api/reminders/{reminder_id}` - Delete reminder
- `POST /api/reminders/logs` - Record medicine intake
- `GET /api/reminders/logs` - Get medicine logs
- `PUT /api/reminders/logs/{log_id}` - Update log (mark as taken/snoozed)
- `GET /api/reminders/logs/missed` - Get missed medicines

### Insulin Logs
- `POST /api/insulin` - Record insulin with glucose reading
- `GET /api/insulin` - Get all insulin logs
- `GET /api/insulin/daily` - Get daily insulin logs
- `GET /api/insulin/weekly` - Get weekly statistics
- `GET /api/insulin/monthly` - Get monthly statistics
- `GET /api/insulin/suggest-dosage` - Get insulin dosage suggestion

## Database Schema

### Users
- id, name, photo_url, timestamps

### Medicines
- id, user_id, name, type (tablet/injection/insulin), dosage, instructions, image_url, is_active, timestamps
- **Note**: TABLET and INJECTION follow the same process. INSULIN requires glucose readings.

### Reminders
- id, medicine_id, scheduled_time, is_active, timestamps

### Medicine Logs
- id, user_id, medicine_id, reminder_id, status (pending/taken/missed/snoozed), scheduled_at, taken_at, snooze_count, notes, timestamps

### Insulin Logs
- id, user_id, medicine_log_id, glucose_reading, insulin_dosage, suggested_dosage, notes, recorded_at, timestamps

### Bookmarks
- id, name, phone_number, contact_type, is_active, timestamp

## Development

### Database Migrations
```bash
# Generate migration
docker-compose exec backend alembic revision --autogenerate -m "description"

# Apply migration
docker-compose exec backend alembic upgrade head
```

### View Logs
```bash
# All logs
docker-compose logs -f

# Backend only
docker-compose logs -f backend

# Database only
docker-compose logs -f postgres
```

### Access Database
```bash
docker-compose exec postgres psql -U medicine_user -d medicine_tracker_db
```

## Environment Variables

See `.env.example` for required environment variables.

## Notes

- Default PostgreSQL credentials are in `docker-compose.yml`
- Change `SECRET_KEY` in production
- The backend auto-creates database tables on startup
- Uploaded medicine images are stored in `/uploads` directory

