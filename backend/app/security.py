"""
Security utilities for API authentication and authorization
"""
from datetime import datetime, timedelta
from typing import Optional
from fastapi import HTTPException, Security, status, Depends, Request
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials, APIKeyHeader
from jose import JWTError, jwt
from passlib.context import CryptContext
import os
from slowapi import Limiter
from slowapi.util import get_remote_address

# Security settings
SECRET_KEY = os.getenv("SECRET_KEY", "your-secret-key-change-in-production")
ALGORITHM = os.getenv("ALGORITHM", "HS256")
ACCESS_TOKEN_EXPIRE_MINUTES = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", "30"))

# API Key from environment
API_KEY = os.getenv("API_KEY", "")  # Must be set in production!
API_KEY_NAME = "X-API-Key"

# Password hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Security schemes
security = HTTPBearer()
api_key_header = APIKeyHeader(name=API_KEY_NAME, auto_error=False)

# Rate limiter
limiter = Limiter(key_func=get_remote_address)


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verify a password against its hash"""
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password: str) -> str:
    """Hash a password"""
    return pwd_context.hash(password)


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    """Create a JWT access token"""
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


def verify_token(token: str) -> dict:
    """Verify and decode a JWT token"""
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except JWTError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Could not validate credentials",
            headers={"WWW-Authenticate": "Bearer"},
        )


async def verify_api_key(api_key: str = Security(api_key_header)) -> str:
    """
    Verify API Key from header
    Usage: Add X-API-Key header to requests
    """
    if not API_KEY:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="API_KEY not configured on server"
        )
    
    if api_key is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="API Key missing. Please provide X-API-Key header"
        )
    
    if api_key != API_KEY:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid API Key"
        )
    
    return api_key


async def verify_bearer_token(credentials: HTTPAuthorizationCredentials = Security(security)) -> dict:
    """
    Verify JWT Bearer token
    Usage: Add Authorization: Bearer <token> header
    """
    token = credentials.credentials
    return verify_token(token)


async def get_current_user(token: dict = Depends(verify_bearer_token)) -> dict:
    """Get current user from token"""
    user_id = token.get("sub")
    if user_id is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication credentials"
        )
    return token


# Combined authentication: API Key OR Bearer Token
async def verify_auth(
    api_key: Optional[str] = Security(api_key_header),
    credentials: Optional[HTTPAuthorizationCredentials] = Security(security)
) -> bool:
    """
    Flexible authentication: Accept either API Key or Bearer Token
    """
    # Try API Key first
    if api_key:
        try:
            await verify_api_key(api_key)
            return True
        except HTTPException:
            pass
    
    # Try Bearer Token
    if credentials:
        try:
            verify_token(credentials.credentials)
            return True
        except HTTPException:
            pass
    
    # Neither worked
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Authentication required. Provide either X-API-Key header or Authorization: Bearer token",
        headers={"WWW-Authenticate": "Bearer"},
    )

