# 🔧 Fix Render Deployment Error

## ❌ Error Yang Berlaku

```
Error: Cannot find module '../config/supabase.config'
```

Ini berlaku kerana Render masih ada **old cache** atau **old files** dari deployment sebelum ini.

---

## ✅ SOLUTION: Force Clear & Redeploy

### **Option 1: Clear Build Cache (Recommended)**

1. **Buka Render Dashboard**
   - Pergi ke https://dashboard.render.com
   - Pilih service `toyyibpay-backend` anda

2. **Trigger Manual Deploy dengan Clear Cache**
   - Klik tab **"Manual Deploy"**
   - Klik **"Clear build cache & deploy"**
   - Wait 3-5 minit untuk deployment selesai

3. **Check Logs**
   - Klik tab **"Logs"**
   - Pastikan tiada error `Cannot find module`
   - Sepatutnya nampak: `🚀 Server running on port 3000`

---

### **Option 2: Redeploy dari GitHub**

Jika Option 1 tidak berjaya:

1. **Buka Render Dashboard**
2. Klik service anda
3. Klik **"Settings"** tab
4. Scroll ke bawah → Klik **"Delete Service"** (TEMPORARY)
   - ⚠️ Jangan risau, kita akan create semula!

5. **Create New Web Service**
   - Klik **"+ New"** → **"Web Service"**
   - Connect ke GitHub repository: `nurularina46/TempahanPhotoStudio`
   - Set configurations:
     ```
     Name: toyyibpay-backend
     Root Directory: toyyibpay-backend
     Environment: Node
     Build Command: npm install
     Start Command: node server.js
     Instance Type: Free
     ```

6. **Add Environment Variables** (PENTING!):
   ```
   SUPABASE_URL=your_supabase_url
   SUPABASE_SERVICE_KEY=your_supabase_service_key
   TOYYIB_USER_SECRET=your_toyyibpay_secret
   TOYYIB_CATEGORY_CODE=your_category_code
   TOYYIB_RETURN_URL=https://your-app.onrender.com/payment-success
   TOYYIB_CALLBACK_URL=https://your-app.onrender.com/api/toyyibpay/callback
   PORT=3000
   NODE_ENV=production
   ```

7. **Klik "Create Web Service"**
   - Wait 5-10 minit untuk first deployment

---

### **Option 3: Local Test Dulu**

Sebelum deploy, pastikan local berfungsi:

```bash
# Test locally
npm install
npm start

# Sepatutnya nampak:
# 🚀 Server running on port 3000
# 📝 Environment: development
# 🔗 Health check: http://localhost:3000/health

# Test health check
curl http://localhost:3000/health
```

Jika local berfungsi tapi Render error, kemungkinan besar issue cache.

---

## 🔍 Verify Deployment Success

### **1. Check Health Endpoint**

```bash
curl https://your-app.onrender.com/health
```

**Expected Response:**
```json
{
  "status": "OK",
  "message": "ToyyibPay Backend is running",
  "timestamp": "2025-11-25T12:00:00.000Z"
}
```

### **2. Check Render Logs**

Di Render Dashboard → Logs, sepatutnya nampak:

```
Nov 25 12:00:00 PM  ==> Cloning from https://github.com/nurularina46/TempahanPhotoStudio...
Nov 25 12:00:05 PM  ==> Checking out commit 76fca86...
Nov 25 12:00:10 PM  ==> Running 'npm install'
Nov 25 12:00:30 PM  ==> Build successful!
Nov 25 12:00:35 PM  ==> Starting service with 'node server.js'
Nov 25 12:00:36 PM  🚀 Server running on port 3000
Nov 25 12:00:36 PM  📝 Environment: production
```

### **3. Test Create Order Endpoint**

```bash
curl -X POST https://your-app.onrender.com/api/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "user_name": "Test User",
    "user_email": "test@example.com",
    "user_phone": "0123456789",
    "package_name": "Test Package",
    "event_date": "2025-12-25",
    "event_time": "14:00:00",
    "base_amount": 100.00
  }'
```

**Expected:** Response dengan `orderNumber`, `billCode`, `billUrl`

---

## 🐛 Troubleshooting

### Error: Module not found

**Solution:** Clear build cache (Option 1 above)

### Error: Cannot connect to Supabase

**Solution:** 
1. Check environment variables di Render
2. Pastikan `SUPABASE_URL` dan `SUPABASE_SERVICE_KEY` betul
3. Test Supabase connection:
   ```bash
   curl "https://your-supabase-project.supabase.co/rest/v1/payments?limit=1" \
     -H "apikey: YOUR_SERVICE_KEY"
   ```

### Error: ToyyibPay API failed

**Solution:**
1. Check `TOYYIB_USER_SECRET` dalam environment variables
2. Check `TOYYIB_CATEGORY_CODE` betul
3. Test ToyyibPay API dari Postman

### Server keeps restarting

**Solution:**
1. Check Render logs untuk exact error
2. Kemungkinan missing environment variable
3. Pastikan `package.json` ada semua dependencies

---

## 📋 Checklist

Sebelum declare success, check semua ini:

- [ ] ✅ Render deployment status: **Live**
- [ ] ✅ Health check endpoint working
- [ ] ✅ No errors dalam Render logs
- [ ] ✅ Supabase connection working
- [ ] ✅ Can create order (test dengan Postman)
- [ ] ✅ Can get order (test dengan Postman)
- [ ] ✅ ToyyibPay callback URL configured

---

## 🎯 Next Steps Selepas Deploy Success

1. **Update ToyyibPay Dashboard**
   - Set Callback URL: `https://your-app.onrender.com/api/toyyibpay/callback`
   - Set Return URL: `https://your-app.onrender.com/payment-success`

2. **Update Android App**
   - Change `BASE_URL` ke Render URL
   - Test create order dari Android
   - Test payment flow end-to-end

3. **Setup Monitoring** (Optional)
   - Render provides basic monitoring
   - Check metrics di Render Dashboard
   - Setup alerts untuk downtime

---

## 💡 Tips

### Keep Service Awake (Free Tier)

Free tier Render akan sleep after 15 minutes inactivity. Untuk keep awake:

1. Use cron job service (e.g., cron-job.org)
2. Ping health endpoint setiap 10 minit
3. Or upgrade ke paid plan ($7/month)

### Monitor Logs

```bash
# Install Render CLI (optional)
npm install -g @render/cli

# View logs
render logs -f
```

---

**Semoga berjaya! 🚀**

