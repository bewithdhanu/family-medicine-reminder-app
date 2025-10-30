from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from app.database import get_db
from app.models import Reminder, Medicine, MedicineLog
from app.schemas import (
    ReminderCreate, ReminderResponse,
    MedicineLogCreate, MedicineLogUpdate, MedicineLogResponse
)

router = APIRouter()

# Reminder endpoints
@router.post("/", response_model=ReminderResponse, status_code=201)
def create_reminder(reminder: ReminderCreate, db: Session = Depends(get_db)):
    """Create a new reminder for a medicine"""
    medicine = db.query(Medicine).filter(Medicine.id == reminder.medicine_id).first()
    if not medicine:
        raise HTTPException(status_code=404, detail="Medicine not found")
    
    db_reminder = Reminder(**reminder.model_dump())
    db.add(db_reminder)
    db.commit()
    db.refresh(db_reminder)
    return db_reminder

@router.get("/", response_model=List[ReminderResponse])
def get_reminders(medicine_id: int = None, db: Session = Depends(get_db)):
    """Get all reminders, optionally filtered by medicine"""
    query = db.query(Reminder)
    if medicine_id:
        query = query.filter(Reminder.medicine_id == medicine_id)
    return query.filter(Reminder.is_active == True).all()

@router.delete("/{reminder_id}", status_code=204)
def delete_reminder(reminder_id: int, db: Session = Depends(get_db)):
    """Delete (deactivate) a reminder"""
    reminder = db.query(Reminder).filter(Reminder.id == reminder_id).first()
    if not reminder:
        raise HTTPException(status_code=404, detail="Reminder not found")
    
    reminder.is_active = False
    db.commit()
    return None

# Medicine Log endpoints
@router.post("/logs", response_model=MedicineLogResponse, status_code=201)
def create_medicine_log(log: MedicineLogCreate, db: Session = Depends(get_db)):
    """Record medicine intake (taken, missed, pending, etc.)"""
    db_log = MedicineLog(**log.model_dump())
    db.add(db_log)
    db.commit()
    db.refresh(db_log)
    return db_log

@router.get("/logs", response_model=List[MedicineLogResponse])
def get_medicine_logs(
    user_id: int = None,
    medicine_id: int = None,
    db: Session = Depends(get_db)
):
    """Get medicine logs with optional filters"""
    query = db.query(MedicineLog)
    
    if user_id:
        query = query.filter(MedicineLog.user_id == user_id)
    if medicine_id:
        query = query.filter(MedicineLog.medicine_id == medicine_id)
    
    return query.order_by(MedicineLog.scheduled_at.desc()).all()

@router.put("/logs/{log_id}", response_model=MedicineLogResponse)
def update_medicine_log(
    log_id: int,
    log_update: MedicineLogUpdate,
    db: Session = Depends(get_db)
):
    """Update medicine log (e.g., mark as taken, snooze)"""
    log = db.query(MedicineLog).filter(MedicineLog.id == log_id).first()
    if not log:
        raise HTTPException(status_code=404, detail="Medicine log not found")
    
    for key, value in log_update.model_dump(exclude_unset=True).items():
        setattr(log, key, value)
    
    db.commit()
    db.refresh(log)
    return log

@router.get("/logs/missed", response_model=List[MedicineLogResponse])
def get_missed_medicines(user_id: int = None, db: Session = Depends(get_db)):
    """Get all missed medicines"""
    from app.models import ReminderStatus
    
    query = db.query(MedicineLog).filter(
        MedicineLog.status.in_([ReminderStatus.MISSED, ReminderStatus.PENDING])
    )
    
    if user_id:
        query = query.filter(MedicineLog.user_id == user_id)
    
    return query.order_by(MedicineLog.scheduled_at.desc()).all()

