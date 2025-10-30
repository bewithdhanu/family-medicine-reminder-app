from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import func, extract
from typing import List
from datetime import datetime, timedelta
from app.database import get_db
from app.models import InsulinLog, User, MedicineType
from app.schemas import InsulinLogCreate, InsulinLogResponse

router = APIRouter()

def calculate_insulin_dosage(glucose_reading: float) -> float:
    """
    Simple insulin dosage calculation based on glucose reading
    This is a simplified algorithm - real-world usage should consult medical professionals
    
    NOTE: This is only used for INSULIN type medicines.
    TABLET and INJECTION types do NOT require glucose readings.
    """
    if glucose_reading < 70:
        return 0.0  # Low blood sugar - no insulin needed
    elif glucose_reading < 120:
        return 2.0  # Normal range - minimal insulin
    elif glucose_reading < 180:
        return 4.0  # Slightly elevated
    elif glucose_reading < 250:
        return 6.0  # High
    else:
        return 8.0  # Very high - maximum suggested dose

@router.post("/", response_model=InsulinLogResponse, status_code=201)
def create_insulin_log(log: InsulinLogCreate, db: Session = Depends(get_db)):
    """Record insulin intake with glucose reading"""
    # Verify user exists
    user = db.query(User).filter(User.id == log.user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    # If suggested dosage not provided, calculate it
    if log.suggested_dosage is None:
        log.suggested_dosage = calculate_insulin_dosage(log.glucose_reading)
    
    db_log = InsulinLog(**log.model_dump())
    db.add(db_log)
    db.commit()
    db.refresh(db_log)
    return db_log

@router.get("/", response_model=List[InsulinLogResponse])
def get_insulin_logs(
    user_id: int = None,
    db: Session = Depends(get_db)
):
    """Get all insulin logs with optional user filter"""
    query = db.query(InsulinLog)
    
    if user_id:
        query = query.filter(InsulinLog.user_id == user_id)
    
    return query.order_by(InsulinLog.recorded_at.desc()).all()

@router.get("/daily", response_model=List[InsulinLogResponse])
def get_daily_insulin_logs(
    user_id: int,
    date: str = None,
    db: Session = Depends(get_db)
):
    """Get insulin logs for a specific day"""
    if date:
        target_date = datetime.fromisoformat(date).date()
    else:
        target_date = datetime.now().date()
    
    start_of_day = datetime.combine(target_date, datetime.min.time())
    end_of_day = datetime.combine(target_date, datetime.max.time())
    
    return db.query(InsulinLog).filter(
        InsulinLog.user_id == user_id,
        InsulinLog.recorded_at >= start_of_day,
        InsulinLog.recorded_at <= end_of_day
    ).order_by(InsulinLog.recorded_at).all()

@router.get("/weekly")
def get_weekly_insulin_stats(
    user_id: int,
    db: Session = Depends(get_db)
):
    """Get weekly insulin statistics"""
    week_ago = datetime.now() - timedelta(days=7)
    
    logs = db.query(InsulinLog).filter(
        InsulinLog.user_id == user_id,
        InsulinLog.recorded_at >= week_ago
    ).all()
    
    if not logs:
        return {
            "user_id": user_id,
            "period": "weekly",
            "total_entries": 0,
            "avg_glucose": 0,
            "avg_insulin": 0,
            "min_glucose": 0,
            "max_glucose": 0
        }
    
    glucose_readings = [log.glucose_reading for log in logs]
    insulin_dosages = [log.insulin_dosage for log in logs]
    
    return {
        "user_id": user_id,
        "period": "weekly",
        "total_entries": len(logs),
        "avg_glucose": round(sum(glucose_readings) / len(glucose_readings), 2),
        "avg_insulin": round(sum(insulin_dosages) / len(insulin_dosages), 2),
        "min_glucose": min(glucose_readings),
        "max_glucose": max(glucose_readings),
        "logs": logs
    }

@router.get("/monthly")
def get_monthly_insulin_stats(
    user_id: int,
    db: Session = Depends(get_db)
):
    """Get monthly insulin statistics"""
    month_ago = datetime.now() - timedelta(days=30)
    
    logs = db.query(InsulinLog).filter(
        InsulinLog.user_id == user_id,
        InsulinLog.recorded_at >= month_ago
    ).all()
    
    if not logs:
        return {
            "user_id": user_id,
            "period": "monthly",
            "total_entries": 0,
            "avg_glucose": 0,
            "avg_insulin": 0,
            "min_glucose": 0,
            "max_glucose": 0
        }
    
    glucose_readings = [log.glucose_reading for log in logs]
    insulin_dosages = [log.insulin_dosage for log in logs]
    
    return {
        "user_id": user_id,
        "period": "monthly",
        "total_entries": len(logs),
        "avg_glucose": round(sum(glucose_readings) / len(glucose_readings), 2),
        "avg_insulin": round(sum(insulin_dosages) / len(insulin_dosages), 2),
        "min_glucose": min(glucose_readings),
        "max_glucose": max(glucose_readings),
        "logs": logs
    }

@router.get("/suggest-dosage")
def suggest_insulin_dosage(glucose_reading: float):
    """Get insulin dosage suggestion based on glucose reading"""
    suggested = calculate_insulin_dosage(glucose_reading)
    
    return {
        "glucose_reading": glucose_reading,
        "suggested_dosage": suggested,
        "unit": "units",
        "note": "This is a simplified suggestion. Always consult with healthcare professionals."
    }

