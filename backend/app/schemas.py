from pydantic import BaseModel, ConfigDict
from datetime import datetime
from typing import Optional, List
from app.models import MedicineType, ReminderStatus

# User Schemas
class UserBase(BaseModel):
    name: str
    photo_url: Optional[str] = None

class UserCreate(UserBase):
    pass

class UserUpdate(BaseModel):
    name: Optional[str] = None
    photo_url: Optional[str] = None

class UserResponse(UserBase):
    id: int
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)

# Medicine Schemas
class MedicineBase(BaseModel):
    name: str
    type: MedicineType
    dosage: Optional[str] = None
    instructions: Optional[str] = None
    image_url: Optional[str] = None

class MedicineCreate(MedicineBase):
    user_id: int

class MedicineUpdate(BaseModel):
    name: Optional[str] = None
    dosage: Optional[str] = None
    instructions: Optional[str] = None
    image_url: Optional[str] = None
    is_active: Optional[bool] = None

class MedicineResponse(MedicineBase):
    id: int
    user_id: int
    is_active: bool
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(from_attributes=True)

# Reminder Schemas
class ReminderBase(BaseModel):
    scheduled_time: str  # Format: "HH:MM"

class ReminderCreate(ReminderBase):
    medicine_id: int

class ReminderResponse(ReminderBase):
    id: int
    medicine_id: int
    is_active: bool
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

# Medicine Log Schemas
class MedicineLogBase(BaseModel):
    status: ReminderStatus
    scheduled_at: datetime
    notes: Optional[str] = None

class MedicineLogCreate(MedicineLogBase):
    user_id: int
    medicine_id: int
    reminder_id: Optional[int] = None

class MedicineLogUpdate(BaseModel):
    status: Optional[ReminderStatus] = None
    taken_at: Optional[datetime] = None
    snooze_count: Optional[int] = None
    notes: Optional[str] = None

class MedicineLogResponse(MedicineLogBase):
    id: int
    user_id: int
    medicine_id: int
    reminder_id: Optional[int]
    taken_at: Optional[datetime]
    snooze_count: int
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

# Insulin Log Schemas
class InsulinLogBase(BaseModel):
    glucose_reading: float
    insulin_dosage: float
    notes: Optional[str] = None

class InsulinLogCreate(InsulinLogBase):
    user_id: int
    medicine_log_id: Optional[int] = None
    suggested_dosage: Optional[float] = None

class InsulinLogResponse(InsulinLogBase):
    id: int
    user_id: int
    medicine_log_id: Optional[int]
    suggested_dosage: Optional[float]
    recorded_at: datetime
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

# Bookmark Schemas
class BookmarkBase(BaseModel):
    name: str
    phone_number: str
    contact_type: str

class BookmarkCreate(BookmarkBase):
    pass

class BookmarkResponse(BookmarkBase):
    id: int
    is_active: bool
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

