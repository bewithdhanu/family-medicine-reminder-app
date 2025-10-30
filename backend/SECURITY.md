# 🔒 Security Documentation

## Overview

This application implements multiple layers of security to protect your medication data when deployed to production.

---

## 🛡️ Security Features

### 1. **API Key Authentication**
Every request to protected endpoints requires an `X-API-Key` header.

### 2. **JWT Bearer Tokens** (Alternative)
Login with credentials to get a JWT token for session-based access.

### 3. **Rate Limiting**
- Root endpoint: 10 requests/minute
- Health check: 20 requests/minute
- All API endpoints: Rate limited per IP

### 4. **CORS Protection**
Configurable allowed origins to prevent unauthorized domain access.

### 5. **HTTPS Enforcement** (Production)
All traffic should be served over HTTPS in production.

### 6. **Environment-Based Security**
- Development: API docs enabled
- Production: API docs disabled, strict CORS

### 7. **Password Hashing**
Uses bcrypt for secure password storage.

---

## 🚀 Production Deployment Setup

### Step 1: Generate Secure Keys

```bash
# Generate SECRET_KEY
openssl rand -hex 32

# Generate API_KEY
openssl rand -hex 32
```

### Step 2: Create Production .env File

```bash
cp backend/.env.production.example backend/.env
```

Edit `backend/.env` with your secure values:
```env
SECRET_KEY=your-generated-secret-key-here
API_KEY=your-generated-api-key-here
ENVIRONMENT=production
DATABASE_URL=postgresql://secure_user:strong_password@your-db-host:5432/medicine_tracker_db
CORS_ORIGINS=["https://your-domain.com"]
```

### Step 3: Set Environment Variables

**Option A: Docker Compose (Recommended)**
```bash
# In docker-compose.yml or docker-compose.prod.yml
environment:
  - SECRET_KEY=${SECRET_KEY}
  - API_KEY=${API_KEY}
  - ENVIRONMENT=production
```

**Option B: System Environment Variables**
```bash
export SECRET_KEY="your-secret-key"
export API_KEY="your-api-key"
export ENVIRONMENT="production"
```

### Step 4: Verify Security

```bash
# Check that API key is required
curl http://localhost:8001/api/users/

# Should return 401 Unauthorized
```

---

## 🔑 Using API Key Authentication

### From Android App (Kotlin)

```kotlin
// Add to your Retrofit interceptor
class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", apiKey)
            .build()
        return chain.proceed(request)
    }
}

// Usage
val retrofit = Retrofit.Builder()
    .baseUrl("https://your-api-domain.com")
    .client(
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor("your-api-key-here"))
            .build()
    )
    .build()
```

### From cURL

```bash
# With API Key
curl -H "X-API-Key: your-api-key-here" \
  http://localhost:8001/api/users/
```

### From JavaScript/Fetch

```javascript
fetch('https://your-api-domain.com/api/users/', {
  headers: {
    'X-API-Key': 'your-api-key-here',
    'Content-Type': 'application/json'
  }
})
```

---

## 🎫 Using JWT Bearer Tokens (Alternative)

### Step 1: Login

```bash
curl -X POST http://localhost:8001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "your-api-key"
  }'
```

Response:
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 1800
}
```

### Step 2: Use Token

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8001/api/users/
```

---

## 🔐 Security Best Practices

### 1. **Never Commit Secrets**
- ✅ Add `.env` to `.gitignore`
- ✅ Use `.env.example` for templates
- ✅ Use environment variables in production

### 2. **Rotate Keys Regularly**
```bash
# Generate new keys every 90 days
openssl rand -hex 32
```

### 3. **Use HTTPS Only**
- Deploy behind reverse proxy (Nginx/Caddy)
- Use Let's Encrypt for free SSL certificates
- Redirect all HTTP to HTTPS

### 4. **Restrict CORS**
```python
# In production, set specific origins
CORS_ORIGINS=["https://your-app.com"]
```

### 5. **Enable Rate Limiting**
Already configured - prevents brute force attacks.

### 6. **Secure Database**
- Use strong PostgreSQL passwords
- Enable SSL for database connections
- Restrict database access by IP

### 7. **Monitor Access**
- Log all API requests
- Set up alerts for suspicious activity
- Review logs regularly

---

## 🚨 Production Checklist

Before deploying to production:

- [ ] Generated strong `SECRET_KEY` (32+ characters)
- [ ] Generated strong `API_KEY` (32+ characters)
- [ ] Set `ENVIRONMENT=production`
- [ ] Configured secure `DATABASE_URL`
- [ ] Restricted `CORS_ORIGINS` to your domain
- [ ] Enabled HTTPS/SSL
- [ ] API docs disabled in production
- [ ] Strong database password
- [ ] `.env` file NOT committed to git
- [ ] Set up monitoring/logging
- [ ] Tested authentication works
- [ ] Configured firewall rules
- [ ] Backed up database
- [ ] Set up automated backups

---

## 🔒 Environment-Specific Behavior

### Development (ENVIRONMENT=development)
- ✅ API docs available at `/docs`
- ✅ CORS allows all origins (`*`)
- ⚠️ API key optional (for testing)
- ✅ Detailed error messages

### Production (ENVIRONMENT=production)
- ❌ API docs disabled
- ✅ CORS restricted to allowed origins
- 🔒 API key **required**
- ❌ Error details hidden

---

## 🛠️ Troubleshooting

### Error: "API Key missing"
**Solution:** Add `X-API-Key` header to your requests.

### Error: "Invalid API Key"
**Solution:** Verify your API key matches the one in `.env` file.

### Error: "API_KEY not configured on server"
**Solution:** Set `API_KEY` environment variable on server.

### Error: "CORS policy blocked"
**Solution:** Add your domain to `CORS_ORIGINS` in `.env`.

---

## 📞 Support

For security issues:
1. **Do NOT** open a public GitHub issue
2. Email security concerns privately
3. Report vulnerabilities responsibly

---

## 🎯 Quick Commands

```bash
# Generate secret keys
openssl rand -hex 32

# Test API with key
curl -H "X-API-Key: YOUR_KEY" http://localhost:8001/api/users/

# Check API key status (dev only)
curl http://localhost:8001/api/auth/api-key-info

# Login and get JWT token
curl -X POST http://localhost:8001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"YOUR_API_KEY"}'

# Restart with new environment
docker-compose down
docker-compose up --build
```

---

## 📚 Additional Resources

- [FastAPI Security](https://fastapi.tiangolo.com/tutorial/security/)
- [JWT Best Practices](https://auth0.com/blog/jwt-security-best-practices/)
- [OWASP API Security](https://owasp.org/www-project-api-security/)
- [Let's Encrypt SSL](https://letsencrypt.org/)

---

**Remember: Security is not optional for production deployments! 🔒**

