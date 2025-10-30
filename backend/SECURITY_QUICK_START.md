# 🔐 Security Quick Start Guide

## 🚀 5-Minute Setup

### Step 1: Generate Your Keys (30 seconds)

```bash
# Run these commands and save the output!
echo "SECRET_KEY=$(openssl rand -hex 32)"
echo "API_KEY=$(openssl rand -hex 32)"
```

**Example output:**
```
SECRET_KEY=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2
API_KEY=z9y8x7w6v5u4t3s2r1q0p9o8n7m6l5k4j3i2h1g0f9e8d7c6b5a4z3y2x1w0v9u8
```

**⚠️ SAVE THESE KEYS! You'll need the API_KEY for your Android app.**

---

### Step 2: Set Environment Variables (1 minute)

**Option A: For Docker (Recommended)**

Create `backend/.env`:
```bash
cd backend
cat > .env << EOF
API_KEY=paste-your-api-key-here
SECRET_KEY=paste-your-secret-key-here
ENVIRONMENT=production
CORS_ORIGINS=["https://your-domain.com"]
EOF
```

**Option B: Export in Shell**
```bash
export API_KEY="your-api-key-here"
export SECRET_KEY="your-secret-key-here"
export ENVIRONMENT="production"
```

---

### Step 3: Restart Backend (30 seconds)

```bash
cd backend
docker-compose down
docker-compose up -d --build
```

---

### Step 4: Test It Works (2 minutes)

**Test WITHOUT API key (should fail):**
```bash
curl http://localhost:8001/api/users/
```
Expected: `{"detail":"Authentication required"}`

**Test WITH API key (should work):**
```bash
curl -H "X-API-Key: your-api-key-here" \
  http://localhost:8001/api/users/
```
Expected: `[]` (empty users list)

---

## 📱 Using in Android App

### Add to your Retrofit setup:

```kotlin
// Create interceptor
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}

// Add to Retrofit
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://your-api-domain.com/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### Store API Key in `local.properties`:
```properties
# local.properties (add to .gitignore!)
api.key=your-api-key-here
```

### Access in `build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "API_KEY", "\"${project.findProperty("api.key") ?: ""}\"")
    }
}
```

---

## 🔒 Security Levels

### Level 1: Development (Default)
```bash
ENVIRONMENT=development
```
- ✅ API docs at /docs
- ⚠️ API key optional
- ✅ CORS allows all origins

### Level 2: Production (Recommended)
```bash
ENVIRONMENT=production
API_KEY=your-strong-api-key
```
- ❌ API docs disabled
- 🔒 API key required
- 🛡️ CORS restricted

---

## 🎯 Common Commands

```bash
# Generate new keys
openssl rand -hex 32

# Test with API key
curl -H "X-API-Key: YOUR_KEY" http://localhost:8001/api/users/

# Check logs
docker-compose logs -f backend

# Restart backend
docker-compose restart backend

# Check environment
curl http://localhost:8001/
```

---

## ⚠️ Important Notes

1. **Never commit .env file** - It's in .gitignore
2. **Save your API_KEY** - You need it for Android app
3. **Use HTTPS in production** - Deploy behind Nginx/Caddy
4. **Rotate keys regularly** - Every 90 days recommended
5. **Different keys per environment** - Dev vs Staging vs Prod

---

## 🆘 Troubleshooting

| Error | Solution |
|-------|----------|
| "Authentication required" | Add `X-API-Key` header |
| "Invalid API Key" | Check your key matches .env |
| "API_KEY not configured" | Set API_KEY in .env file |
| "CORS blocked" | Add your domain to CORS_ORIGINS |

---

## 📚 Full Documentation

- **Security Details:** [SECURITY.md](SECURITY.md)
- **Deployment Guide:** [DEPLOYMENT.md](DEPLOYMENT.md)
- **API Docs:** http://localhost:8001/docs (dev only)

---

**You're all set! Your API is now secure. 🎉**

**Remember to:**
- ✅ Keep your API_KEY secret
- ✅ Use HTTPS in production
- ✅ Store keys securely in Android app
- ✅ Monitor access logs

