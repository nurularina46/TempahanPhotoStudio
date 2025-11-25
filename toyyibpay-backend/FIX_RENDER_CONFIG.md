# 🔧 Fix Render Configuration Error

## ❌ Error Yang Berlaku

```
Error: Cannot find module 'helmet'
Require stack: /opt/render/project/src/server.js
```

## 🔍 Root Cause

Error path `/opt/render/project/src/server.js` menunjukkan Render deploy dari directory yang **SALAH**.

**Expected:** `/opt/render/project/server.js`  
**Actual:** `/opt/render/project/src/server.js` ❌

---

## ✅ SOLUTION: Fix Render Settings

### **Step 1: Buka Render Dashboard**

1. Pergi ke https://dashboard.render.com
2. Login dengan akaun anda
3. Pilih service **toyyibpay-backend**

### **Step 2: Check & Fix Settings**

Klik tab **"Settings"** dan verify:

#### **✅ Correct Configuration:**

```
Name: toyyibpay-backend
Environment: Node
Region: Singapore (atau yang terdekat)

Build & Deploy:
  Root Directory: toyyibpay-backend
  Build Command: npm install
  Start Command: node server.js

Auto-Deploy: Yes
```

#### **❌ Wrong Configuration (Jangan guna ini):**

```
Root Directory: (blank)
Build Command: cd toyyibpay-backend && npm install
Start Command: cd toyyibpay-backend && node server.js
```

### **Step 3: Save & Redeploy**

1. **Jika settings salah:**
   - Update `Root Directory` kepada: `toyyibpay-backend`
   - Update `Build Command` kepada: `npm install`
   - Update `Start Command` kepada: `node server.js`
   - Klik **"Save Changes"**

2. **Trigger manual deploy:**
   - Klik tab **"Manual Deploy"**
   - Klik **"Clear build cache & deploy"**
   - Wait 3-5 minit

### **Step 4: Verify Deployment**

Check logs - sepatutnya nampak:

```
==> Checking out commit 408e9b1...
==> Using Node version 22.16.0
==> Docs on specifying a Node version: https://render.com/docs/node-version
==> Running build command 'npm install'...

added 150 packages in 15s
==> Build successful 🎉
==> Uploading build...

==> Starting service with 'node server.js'
🚀 Server running on port 3000
📝 Environment: production
🔗 Health check: http://localhost:3000/health
```

---

## 🎯 Alternative: Delete & Recreate Service

Jika masih ada masalah, delete service dan create semula:

### **Step 1: Delete Service**

1. Render Dashboard → Select service
2. Settings tab → Scroll bawah
3. Klik **"Delete Service"**
4. Confirm deletion

### **Step 2: Create New Service**

1. Klik **"+ New"** → **"Web Service"**

2. **Connect Repository:**
   - Select: `nurularina46/TempahanPhotoStudio`
   - Klik **"Connect"**

3. **Configure Service:**
   ```
   Name: toyyibpay-backend
   Region: Singapore
   Branch: main
   Root Directory: toyyibpay-backend
   Runtime: Node
   Build Command: npm install
   Start Command: node server.js
   Instance Type: Free
   ```

4. **Add Environment Variables** (PENTING!):

   Klik **"Advanced"** → **"Add Environment Variable"**

   ```
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_SERVICE_KEY=your_service_key_here
   SUPABASE_ANON_KEY=your_anon_key_here
   
   TOYYIB_USER_SECRET=your_toyyibpay_secret
   TOYYIB_CATEGORY_CODE=your_category_code
   TOYYIB_RETURN_URL=https://your-app.onrender.com/payment-success
   TOYYIB_CALLBACK_URL=https://your-app.onrender.com/api/toyyibpay/callback
   
   PORT=3000
   NODE_ENV=production
   ```

5. **Klik "Create Web Service"**
   - Wait 5-10 minit untuk deployment
   - Monitor logs untuk ensure tiada error

---

## 🔍 Verify Configuration Betul

Run command ini untuk check file structure:

```bash
# Di local machine
cd toyyibpay-backend
ls -la

# Sepatutnya nampak:
# ✅ package.json
# ✅ server.js
# ✅ src/
# ✅ node_modules/ (lepas npm install)
```

**Structure yang betul:**

```
TempahanPhotoStudio/
├── app/                    # Android app
├── toyyibpay-backend/      # ← Root Directory di Render
│   ├── package.json        # ✅ Dependency definitions
│   ├── server.js           # ✅ Main entry point
│   ├── .env               # ⚠️ Local only (not in Git)
│   ├── src/
│   │   ├── config/
│   │   ├── controllers/
│   │   ├── routes/
│   │   ├── services/
│   │   └── utils/
│   └── ...
└── ...
```

---

## 🐛 Troubleshooting

### Error: "npm: command not found"

**Solution:** Node environment tidak selected betul
- Settings → Environment: **Node**

### Error: "package.json not found"

**Solution:** Root Directory salah
- Settings → Root Directory: **toyyibpay-backend**

### Error: "Cannot find module 'express'"

**Solution:** npm install tidak run
- Trigger manual deploy dengan clear cache
- Or check Build Command: **npm install**

### Error: "Port already in use"

**Solution:** Guna environment variable PORT
- Environment Variables → Add: `PORT=3000`
- Code guna: `process.env.PORT || 3000`

---

## ✅ Checklist Verify

Sebelum declare berjaya:

- [ ] ✅ Root Directory: `toyyibpay-backend`
- [ ] ✅ Build Command: `npm install`
- [ ] ✅ Start Command: `node server.js`
- [ ] ✅ Environment: Node
- [ ] ✅ All environment variables added
- [ ] ✅ Deployment status: **Live**
- [ ] ✅ No errors in logs
- [ ] ✅ Health endpoint working: `curl https://your-app.onrender.com/health`

---

## 🎯 Expected Logs (Success)

```
Nov 25 01:00:00 PM  ==> Cloning from https://github.com/nurularina46/TempahanPhotoStudio...
Nov 25 01:00:05 PM  ==> Checking out commit 408e9b1...
Nov 25 01:00:06 PM  ==> Using Node version 22.16.0 (default)
Nov 25 01:00:08 PM  ==> Running build command 'npm install'...
Nov 25 01:00:10 PM  npm WARN deprecated inflight@1.0.6
Nov 25 01:00:15 PM  
Nov 25 01:00:15 PM  added 150 packages, and audited 151 packages in 7s
Nov 25 01:00:15 PM  
Nov 25 01:00:15 PM  23 packages are looking for funding
Nov 25 01:00:15 PM    run `npm fund` for details
Nov 25 01:00:15 PM  
Nov 25 01:00:15 PM  found 0 vulnerabilities
Nov 25 01:00:16 PM  ==> Build successful 🎉
Nov 25 01:00:16 PM  ==> Uploading build...
Nov 25 01:00:20 PM  ==> Build uploaded in 4s
Nov 25 01:00:20 PM  
Nov 25 01:00:21 PM  ==> Starting service with 'node server.js'
Nov 25 01:00:22 PM  🚀 Server running on port 3000
Nov 25 01:00:22 PM  📝 Environment: production
Nov 25 01:00:22 PM  🔗 Health check: http://localhost:3000/health
```

---

## 📞 Still Have Issues?

1. **Screenshot Render Settings** - hantar kepada saya
2. **Copy Render Logs** - share full error message
3. **Check `.gitignore`** - pastikan `node_modules/` tidak dalam Git

---

**Good luck! 🚀**

