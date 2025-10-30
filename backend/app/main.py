from fastapi import FastAPI, Depends, Request, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from slowapi import _rate_limit_exceeded_handler
from slowapi.errors import RateLimitExceeded
from app.database import engine
from app.models import Base
from app.routers import users, medicines, reminders, insulin_logs, auth
from app.security import limiter, verify_api_key
from app.config import settings
import os

# Create database tables
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Medicine Tracker API",
    description="Backend API for Family Medication Reminder and Launcher Application (Secured)",
    version="1.0.0",
    docs_url="/docs" if settings.ENVIRONMENT == "development" else None,
    redoc_url="/redoc" if settings.ENVIRONMENT == "development" else None,
)

# Add rate limiter
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)

# CORS configuration - Restrict in production!
allowed_origins = settings.CORS_ORIGINS if settings.ENVIRONMENT == "production" else ["*"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Security middleware to check if API key is configured
@app.middleware("http")
async def check_api_key_configured(request: Request, call_next):
    """Ensure API key is configured in production"""
    if settings.ENVIRONMENT == "production" and not settings.API_KEY:
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={"detail": "Server misconfigured: API_KEY not set"}
        )
    response = await call_next(request)
    return response

# Include routers - Protected routes
app.include_router(
    auth.router, 
    prefix="/api/auth", 
    tags=["Authentication"]
)

app.include_router(
    users.router, 
    prefix="/api/users", 
    tags=["Users"],
    dependencies=[Depends(verify_api_key)]  # Requires API Key
)

app.include_router(
    medicines.router, 
    prefix="/api/medicines", 
    tags=["Medicines"],
    dependencies=[Depends(verify_api_key)]  # Requires API Key
)

app.include_router(
    reminders.router, 
    prefix="/api/reminders", 
    tags=["Reminders"],
    dependencies=[Depends(verify_api_key)]  # Requires API Key
)

app.include_router(
    insulin_logs.router, 
    prefix="/api/insulin", 
    tags=["Insulin Logs"],
    dependencies=[Depends(verify_api_key)]  # Requires API Key
)

@app.get("/")
@limiter.limit("10/minute")
async def root(request: Request):
    """Public root endpoint - Rate limited"""
    return {
        "message": "Medicine Tracker API",
        "version": "1.0.0",
        "status": "running",
        "environment": settings.ENVIRONMENT,
        "docs": "/docs" if settings.ENVIRONMENT == "development" else "disabled in production"
    }

@app.get("/health")
@limiter.limit("20/minute")
async def health_check(request: Request):
    """Public health check endpoint - Rate limited"""
    return {
        "status": "healthy",
        "environment": settings.ENVIRONMENT
    }

