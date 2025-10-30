"""
Authentication endpoints
"""
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from datetime import timedelta
from app.security import (
    create_access_token,
    verify_password,
    get_password_hash,
    ACCESS_TOKEN_EXPIRE_MINUTES
)
from app.config import settings

router = APIRouter()


class LoginRequest(BaseModel):
    username: str
    password: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_in: int


class APIKeyResponse(BaseModel):
    api_key: str
    message: str


# In production, store users in database
# For now, using environment variable
ADMIN_USERNAME = settings.API_KEY or "admin"  # Use API_KEY as admin identifier
ADMIN_PASSWORD_HASH = get_password_hash(settings.API_KEY or "change-me-in-production")


@router.post("/login", response_model=TokenResponse)
async def login(request: LoginRequest):
    """
    Login with username and password to get JWT token
    This is an alternative to API Key authentication
    """
    # Verify credentials
    if request.username != ADMIN_USERNAME:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password"
        )
    
    if not verify_password(request.password, ADMIN_PASSWORD_HASH):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password"
        )
    
    # Create access token
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": request.username},
        expires_delta=access_token_expires
    )
    
    return TokenResponse(
        access_token=access_token,
        expires_in=ACCESS_TOKEN_EXPIRE_MINUTES * 60
    )


@router.get("/api-key-info", response_model=APIKeyResponse)
async def get_api_key_info():
    """
    Get API key configuration status (for development/testing)
    DO NOT expose actual key!
    """
    if not settings.API_KEY:
        return APIKeyResponse(
            api_key="NOT_CONFIGURED",
            message="API Key is not configured. Set API_KEY environment variable."
        )
    
    # Show only first and last 3 characters for security
    masked_key = f"{settings.API_KEY[:3]}...{settings.API_KEY[-3:]}" if len(settings.API_KEY) > 6 else "***"
    
    return APIKeyResponse(
        api_key=masked_key,
        message="API Key is configured. Use X-API-Key header with requests."
    )

