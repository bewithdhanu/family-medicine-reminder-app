from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from typing import List, Optional
from app.database import get_db
from app.models import Medicine, User
from app.schemas import MedicineCreate, MedicineUpdate, MedicineResponse
import os
import uuid

router = APIRouter()

@router.post("/", response_model=MedicineResponse, status_code=201)
def create_medicine(medicine: MedicineCreate, db: Session = Depends(get_db)):
    """Create a new medicine"""
    # Verify user exists
    user = db.query(User).filter(User.id == medicine.user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    db_medicine = Medicine(**medicine.model_dump())
    db.add(db_medicine)
    db.commit()
    db.refresh(db_medicine)
    return db_medicine

@router.get("/", response_model=List[MedicineResponse])
def get_medicines(
    user_id: Optional[int] = None,
    is_active: Optional[bool] = None,
    db: Session = Depends(get_db)
):
    """Get all medicines with optional filters"""
    query = db.query(Medicine)
    
    if user_id:
        query = query.filter(Medicine.user_id == user_id)
    if is_active is not None:
        query = query.filter(Medicine.is_active == is_active)
    
    return query.all()

@router.get("/{medicine_id}", response_model=MedicineResponse)
def get_medicine(medicine_id: int, db: Session = Depends(get_db)):
    """Get medicine by ID"""
    medicine = db.query(Medicine).filter(Medicine.id == medicine_id).first()
    if not medicine:
        raise HTTPException(status_code=404, detail="Medicine not found")
    return medicine

@router.put("/{medicine_id}", response_model=MedicineResponse)
def update_medicine(
    medicine_id: int,
    medicine_update: MedicineUpdate,
    db: Session = Depends(get_db)
):
    """Update medicine details"""
    medicine = db.query(Medicine).filter(Medicine.id == medicine_id).first()
    if not medicine:
        raise HTTPException(status_code=404, detail="Medicine not found")
    
    for key, value in medicine_update.model_dump(exclude_unset=True).items():
        setattr(medicine, key, value)
    
    db.commit()
    db.refresh(medicine)
    return medicine

@router.delete("/{medicine_id}", status_code=204)
def delete_medicine(medicine_id: int, db: Session = Depends(get_db)):
    """Delete (deactivate) a medicine"""
    medicine = db.query(Medicine).filter(Medicine.id == medicine_id).first()
    if not medicine:
        raise HTTPException(status_code=404, detail="Medicine not found")
    
    medicine.is_active = False
    db.commit()
    return None

@router.post("/{medicine_id}/upload-image")
async def upload_medicine_image(medicine_id: int, file: UploadFile = File(...), db: Session = Depends(get_db)):
    """Upload medicine image"""
    medicine = db.query(Medicine).filter(Medicine.id == medicine_id).first()
    if not medicine:
        raise HTTPException(status_code=404, detail="Medicine not found")
    
    # Create uploads directory if it doesn't exist
    upload_dir = "uploads/medicines"
    os.makedirs(upload_dir, exist_ok=True)
    
    # Generate unique filename
    file_extension = os.path.splitext(file.filename)[1]
    unique_filename = f"{uuid.uuid4()}{file_extension}"
    file_path = os.path.join(upload_dir, unique_filename)
    
    # Save file
    with open(file_path, "wb") as buffer:
        content = await file.read()
        buffer.write(content)
    
    # Update medicine with image URL
    medicine.image_url = f"/uploads/medicines/{unique_filename}"
    db.commit()
    
    return {"filename": unique_filename, "url": medicine.image_url}

