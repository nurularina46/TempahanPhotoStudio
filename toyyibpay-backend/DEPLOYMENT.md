# 🚀 Deployment Guide - Render

Complete guide to deploy ToyyibPay backend to Render.

## 📋 Prerequisites

1. GitHub account
2. Render account (sign up at https://render.com)
3. Supabase project set up
4. ToyyibPay Sandbox credentials

## 🔧 Step 1: Push to GitHub

### 1.1 Initialize Git Repository

```bash
cd toyyibpay-backend
git init
git add .
git commit -m "Initial commit: ToyyibPay backend"
```

### 1.2 Create GitHub Repository

1. Go to https://github.com/new
2. Create a new repository (e.g., `toyyibpay-backend`)
3. **DO NOT** initialize with README, .gitignore, or license

### 1.3 Push to GitHub

```bash
# Add remote (replace with your repo URL)
git remote add origin https://github.com/yourusername/toyyibpay-backend.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## 🌐 Step 2: Connect GitHub to Render

### 2.1 Create New Web Service

1. Log in to [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"Web Service"**
3. Click **"Connect account"** if not connected
4. Select your GitHub account
5. Authorize Render to access your repositories

### 2.2 Select Repository

1. Find and select `toyyibpay-backend` repository
2. Click **"Connect"**

### 2.3 Configure Service

**Basic Settings:**
- **Name**: `toyyibpay-backend` (or your preferred name)
- **Region**: Choose closest to your users (e.g., Singapore)
- **Branch**: `main`
- **Root Directory**: (leave empty)
- **Runtime**: `Node`
- **Build Command**: `npm install`
- **Start Command**: `npm start`

**Advanced Settings:**
- **Auto-Deploy**: `Yes` (deploys on every push to main)
- **Health Check Path**: `/health`

## 🔐 Step 3: Environment Variables

In Render dashboard, go to **Environment** section and add:

### Required Variables

```env
NODE_ENV=production
PORT=10000

TOYYIBPAY_SECRET_KEY=your_toyyibpay_secret_key
TOYYIBPAY_CATEGORY_CODE=your_category_code
TOYYIBPAY_BASE_URL=https://dev.toyyibpay.com/index.php/api/

RETURN_URL=https://your-frontend.com/payment-success
CALLBACK_URL=https://your-backend.onrender.com/api/toyyibpay/callback

SUPABASE_URL=https://your-project.supabase.co
SUPABASE_KEY=your_supabase_anon_key
SUPABASE_SERVICE_KEY=your_supabase_service_key
```

### Important Notes:

1. **CALLBACK_URL**: Must be your Render service URL
   - Format: `https://your-service-name.onrender.com/api/toyyibpay/callback`
   - Replace `your-service-name` with your actual service name

2. **RETURN_URL**: Your frontend URL where users return after payment

3. **PORT**: Render automatically sets `PORT` environment variable
   - Your code should use `process.env.PORT || 3000`

## 🚀 Step 4: Deploy

### 4.1 Initial Deploy

1. Click **"Create Web Service"**
2. Render will:
   - Clone your repository
   - Install dependencies
   - Start your service
   - This takes 2-5 minutes

### 4.2 Monitor Deployment

1. Watch the **Logs** tab for:
   - ✅ Build success
   - ✅ Server started
   - ✅ Health check passed

2. Check **Events** tab for deployment status

### 4.3 Verify Deployment

1. Your service URL: `https://your-service-name.onrender.com`
2. Test health endpoint: `https://your-service-name.onrender.com/health`
3. Should return:
   ```json
   {
     "status": "OK",
     "message": "ToyyibPay Backend is running",
     "timestamp": "..."
   }
   ```

## ✅ Step 5: Verify Callback URL

### 5.1 Update ToyyibPay Settings

1. Log in to [ToyyibPay Dashboard](https://dev.toyyibpay.com)
2. Go to **Settings** → **Callback URL**
3. Set callback URL to: `https://your-service-name.onrender.com/api/toyyibpay/callback`
4. Save settings

### 5.2 Test Callback

1. Create a test payment
2. Complete payment in sandbox
3. Check Render logs for callback request
4. Verify database updated

## 🔄 Step 6: Auto-Deploy Setup

### 6.1 Enable Auto-Deploy

- Already enabled by default
- Every push to `main` branch triggers deployment

### 6.2 Manual Deploy

1. Go to **Manual Deploy** section
2. Select branch
3. Click **"Deploy latest commit"**

## 📊 Step 7: Monitoring

### 7.1 View Logs

- **Logs** tab shows real-time logs
- Filter by time range
- Search for specific terms

### 7.2 Metrics

- **Metrics** tab shows:
  - CPU usage
  - Memory usage
  - Request count
  - Response times

### 7.3 Alerts

- Set up alerts for:
  - Service down
  - High error rate
  - Resource limits

## 🛠️ Step 8: Update package.json (if needed)

Ensure your `package.json` has:

```json
{
  "scripts": {
    "start": "node server.js"
  },
  "engines": {
    "node": ">=18.0.0"
  }
}
```

## 🔍 Troubleshooting

### Issue: Build Fails

**Solution:**
- Check logs for error messages
- Verify `package.json` is correct
- Ensure all dependencies are listed
- Check Node.js version compatibility

### Issue: Service Crashes

**Solution:**
- Check application logs
- Verify environment variables are set
- Test locally with same environment
- Check database connection

### Issue: Callback Not Working

**Solution:**
- Verify `CALLBACK_URL` in Render matches ToyyibPay settings
- Check Render logs for incoming requests
- Ensure endpoint returns 200 OK
- Test callback URL manually

### Issue: Health Check Fails

**Solution:**
- Verify `/health` endpoint exists
- Check server starts correctly
- Ensure port is configured properly
- Review application logs

## 📝 Render Service Configuration Summary

```
Service Type: Web Service
Environment: Node
Build Command: npm install
Start Command: npm start
Health Check: /health
Auto-Deploy: Yes
```

## 🔗 Useful Links

- [Render Dashboard](https://dashboard.render.com)
- [Render Documentation](https://render.com/docs)
- [Render Status](https://status.render.com)

## ✅ Deployment Checklist

- [ ] Code pushed to GitHub
- [ ] Repository connected to Render
- [ ] Service created and configured
- [ ] Environment variables set
- [ ] Initial deployment successful
- [ ] Health check passing
- [ ] Callback URL configured in ToyyibPay
- [ ] Test payment created
- [ ] Callback received and processed
- [ ] Database updated correctly

## 🎉 Success!

Your backend is now deployed and ready to handle payments!

Next steps:
1. Test payment flow end-to-end
2. Monitor logs for any issues
3. Set up alerts for production
4. Configure custom domain (optional)

