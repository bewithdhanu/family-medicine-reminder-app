from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.database import engine
from app.models import Base
from app.routers import users, medicines, reminders, insulin_logs

# Create database tables
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Medicine Tracker API",
    description="Backend API for Family Medication Reminder and Launcher Application",
    version="1.0.0"
)

# CORS configuration for Android app
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify your Android app's origin
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(users.router, prefix="/api/users", tags=["Users"])
app.include_router(medicines.router, prefix="/api/medicines", tags=["Medicines"])
app.include_router(reminders.router, prefix="/api/reminders", tags=["Reminders"])
app.include_router(insulin_logs.router, prefix="/api/insulin", tags=["Insulin Logs"])

@app.get("/")
async def root():
    return {
        "message": "Medicine Tracker API",
        "version": "1.0.0",
        "status": "running"
    }

@app.get("/health")
async def health_check():
    return {"status": "healthy"}

