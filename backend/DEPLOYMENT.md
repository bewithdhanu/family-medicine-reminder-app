# 🚀 Production Deployment Guide

## Prerequisites

- Docker & Docker Compose installed
- Domain name configured
- SSL certificate (Let's Encrypt recommended)
- Reverse proxy (Nginx/Caddy)

---

## Step-by-Step Deployment

### 1. Generate Secure Keys

```bash
# Generate SECRET_KEY (save this!)
openssl rand -hex 32

# Generate API_KEY (save this!)
openssl rand -hex 32
```

**Save these keys securely! You'll need them for your Android app.**

---

### 2. Create Production Environment File

Create `backend/.env`:

```env
# Database - Use strong password!
DATABASE_URL=postgresql://medicine_user:CHANGE_THIS_PASSWORD@postgres:5432/medicine_tracker_db

# Security - PASTE YOUR GENERATED KEYS HERE
SECRET_KEY=paste-your-secret-key-from-step-1
API_KEY=paste-your-api-key-from-step-1

# JWT
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=30

# CORS - Replace with your actual domain
CORS_ORIGINS=["https://your-domain.com","https://www.your-domain.com"]

# Environment
ENVIRONMENT=production

# Server
HOST=0.0.0.0
PORT=8000
```

---

### 3. Update Docker Compose for Production

Create `backend/docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: medicine_tracker_db
    environment:
      POSTGRES_USER: medicine_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}  # From .env
      POSTGRES_DB: medicine_tracker_db
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - medicine_network
    restart: unless-stopped

  backend:
    build: .
    container_name: medicine_tracker_api
    env_file:
      - .env
    ports:
      - "8000:8000"  # Internal port, use reverse proxy
    volumes:
      - ./uploads:/app/uploads
    depends_on:
      - postgres
    networks:
      - medicine_network
    restart: unless-stopped

volumes:
  postgres_data:

networks:
  medicine_network:
    driver: bridge
```

---

### 4. Deploy with Docker

```bash
# Build and start services
cd backend
docker-compose -f docker-compose.prod.yml up -d --build

# Check logs
docker-compose -f docker-compose.prod.yml logs -f backend

# Verify it's running
curl http://localhost:8000/health
```

---

### 5. Set Up Nginx Reverse Proxy

Create `/etc/nginx/sites-available/medicine-api`:

```nginx
server {
    listen 80;
    server_name api.your-domain.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.your-domain.com;
    
    # SSL Configuration (Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/api.your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.your-domain.com/privkey.pem;
    
    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000" always;
    
    # Rate Limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req zone=api burst=20 nodelay;
    
    location / {
        proxy_pass http://localhost:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/medicine-api /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

### 6. Set Up SSL with Let's Encrypt

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d api.your-domain.com
```

---

### 7. Test Security

```bash
# Should require API key
curl https://api.your-domain.com/api/users/
# Expected: 401 Unauthorized

# With API key - should work
curl -H "X-API-Key: your-api-key-here" \
  https://api.your-domain.com/api/users/
# Expected: 200 OK with data
```

---

### 8. Configure Your Android App

In your Android app, add the API key:

```kotlin
// NetworkModule.kt or similar
object ApiConfig {
    const val BASE_URL = "https://api.your-domain.com/"
    const val API_KEY = "your-api-key-here"  // Store securely!
}

// Retrofit setup
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", ApiConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}
```

**⚠️ Security Note:** Store API key securely using Android's EncryptedSharedPreferences or Android Keystore!

---

### 9. Monitor & Maintain

```bash
# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Restart services
docker-compose -f docker-compose.prod.yml restart

# Update application
git pull
docker-compose -f docker-compose.prod.yml up -d --build

# Backup database
docker exec medicine_tracker_db pg_dump -U medicine_user medicine_tracker_db > backup.sql
```

---

## 🔒 Security Checklist

- [x] Generated strong SECRET_KEY
- [x] Generated strong API_KEY
- [x] Set ENVIRONMENT=production
- [x] Configured SSL/HTTPS
- [x] Restricted CORS origins
- [x] API docs disabled in production
- [x] Using strong database password
- [x] Rate limiting enabled
- [x] Reverse proxy configured
- [x] Security headers added
- [x] Regular backups scheduled

---

## 🚨 Troubleshooting

### Error: "API_KEY not configured"
Set `API_KEY` in your `.env` file.

### Error: "CORS policy blocked"
Add your domain to `CORS_ORIGINS` in `.env`.

### Database connection failed
Check `DATABASE_URL` in `.env` matches your PostgreSQL configuration.

### SSL certificate errors
Renew with: `sudo certbot renew`

---

## 📊 Monitoring (Optional)

Add monitoring tools:
- **Uptime Monitoring:** UptimeRobot, Pingdom
- **Error Tracking:** Sentry
- **Performance:** New Relic, DataDog
- **Logs:** ELK Stack, Grafana

---

## 🔄 Auto-Deployment (Optional)

Set up GitHub Actions:

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /path/to/app/backend
            git pull
            docker-compose -f docker-compose.prod.yml up -d --build
```

---

## 📞 Support

For deployment issues:
1. Check logs: `docker-compose logs -f`
2. Review SECURITY.md
3. Verify environment variables
4. Test connectivity

---

**Your API is now secure and production-ready! 🎉**

