from sqlalchemy import Column, Integer, String, DateTime, Boolean, Float, ForeignKey, Enum as SQLEnum, Text
from sqlalchemy.orm import relationship
from datetime import datetime
import enum
from app.database import Base

class MedicineType(str, enum.Enum):
    TABLET = "tablet"
    INJECTION = "injection"
    INSULIN = "insulin"

class ReminderStatus(str, enum.Enum):
    PENDING = "pending"
    TAKEN = "taken"
    MISSED = "missed"
    SNOOZED = "snoozed"

# User Model
class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    photo_url = Column(String, nullable=True)
    # Optional emoji avatar (e.g., "👨", "👩", "👴")
    avatar_emoji = Column(String, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    medicines = relationship("Medicine", back_populates="user")
    medicine_logs = relationship("MedicineLog", back_populates="user")
    insulin_logs = relationship("InsulinLog", back_populates="user")

# Medicine Model
class Medicine(Base):
    __tablename__ = "medicines"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    name = Column(String, nullable=False)
    type = Column(SQLEnum(MedicineType), nullable=False)
    dosage = Column(String, nullable=True)
    instructions = Column(Text, nullable=True)
    image_url = Column(String, nullable=True)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="medicines")
    reminders = relationship("Reminder", back_populates="medicine")
    medicine_logs = relationship("MedicineLog", back_populates="medicine")

# Reminder Model (Scheduled reminders)
class Reminder(Base):
    __tablename__ = "reminders"

    id = Column(Integer, primary_key=True, index=True)
    medicine_id = Column(Integer, ForeignKey("medicines.id"), nullable=False)
    scheduled_time = Column(String, nullable=False)  # Format: "HH:MM"
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    medicine = relationship("Medicine", back_populates="reminders")
    medicine_logs = relationship("MedicineLog", back_populates="reminder")

# Medicine Log (Actual intake records)
class MedicineLog(Base):
    __tablename__ = "medicine_logs"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    medicine_id = Column(Integer, ForeignKey("medicines.id"), nullable=False)
    reminder_id = Column(Integer, ForeignKey("reminders.id"), nullable=True)
    status = Column(SQLEnum(ReminderStatus), nullable=False)
    scheduled_at = Column(DateTime, nullable=False)
    taken_at = Column(DateTime, nullable=True)
    snooze_count = Column(Integer, default=0)
    notes = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="medicine_logs")
    medicine = relationship("Medicine", back_populates="medicine_logs")
    reminder = relationship("Reminder", back_populates="medicine_logs")

# Insulin Log (Glucose readings and insulin tracking)
class InsulinLog(Base):
    __tablename__ = "insulin_logs"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    medicine_log_id = Column(Integer, ForeignKey("medicine_logs.id"), nullable=True)
    glucose_reading = Column(Float, nullable=False)  # mg/dL
    insulin_dosage = Column(Float, nullable=False)  # Units
    suggested_dosage = Column(Float, nullable=True)  # Units
    notes = Column(Text, nullable=True)
    recorded_at = Column(DateTime, default=datetime.utcnow)
    created_at = Column(DateTime, default=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="insulin_logs")

# Communication Bookmark Model
class Bookmark(Base):
    __tablename__ = "bookmarks"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    phone_number = Column(String, nullable=False)
    contact_type = Column(String, nullable=False)  # "phone" or "whatsapp"
    # Optional photo URL for contact avatar
    photo_url = Column(String, nullable=True)
    # Optional emoji for contact avatar (if user prefers emoji over image)
    avatar_emoji = Column(String, nullable=True)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)

