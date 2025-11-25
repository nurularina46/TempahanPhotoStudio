# 📦 GitHub Setup Guide

Quick guide to set up your GitHub repository for the ToyyibPay backend.

## 🚀 Quick Start

### 1. Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `toyyibpay-backend` (or your preferred name)
3. Description: `ToyyibPay Sandbox Integration Backend`
4. Visibility: **Private** (recommended) or **Public**
5. **DO NOT** check:
   - ❌ Add a README file
   - ❌ Add .gitignore
   - ❌ Choose a license
6. Click **"Create repository"**

### 2. Initialize Local Repository

```bash
cd toyyibpay-backend

# Initialize git
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: ToyyibPay backend integration"
```

### 3. Connect to GitHub

```bash
# Add remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/toyyibpay-backend.git

# Rename branch to main
git branch -M main

# Push to GitHub
git push -u origin main
```

### 4. Verify

1. Go to your GitHub repository
2. Verify all files are uploaded
3. Check that `.env` is **NOT** in the repository (should be in `.gitignore`)

## 📝 Important Files

### Files to Commit ✅
- All source code files
- `package.json`
- `README.md`
- `.gitignore`
- Documentation files

### Files to NEVER Commit ❌
- `.env` (contains secrets)
- `node_modules/` (dependencies)
- Log files
- Build artifacts

## 🔒 Security Checklist

Before pushing:

- [ ] `.env` is in `.gitignore`
- [ ] No API keys in code
- [ ] No passwords in code
- [ ] `.env.example` is committed (without real values)
- [ ] Repository is private (if contains sensitive info)

## 🔄 Common Git Commands

### Daily Workflow

```bash
# Check status
git status

# Add changes
git add .

# Commit changes
git commit -m "Description of changes"

# Push to GitHub
git push origin main
```

### Create New Branch

```bash
# Create and switch to new branch
git checkout -b feature/new-feature

# Push new branch
git push -u origin feature/new-feature
```

### Update from Remote

```bash
# Fetch latest changes
git fetch origin

# Merge changes
git merge origin/main

# Or pull (fetch + merge)
git pull origin main
```

## 🏷️ Git Tags (Optional)

Tag releases for version tracking:

```bash
# Create tag
git tag -a v1.0.0 -m "Initial release"

# Push tag
git push origin v1.0.0
```

## 📋 Repository Structure

Your GitHub repository should look like:

```
toyyibpay-backend/
├── src/
│   ├── config/
│   ├── controllers/
│   ├── services/
│   ├── routes/
│   └── utils/
├── supabase/
│   └── schema.sql
├── .gitignore
├── package.json
├── README.md
├── DEPLOYMENT.md
├── TESTING.md
└── server.js
```

## 🔗 Connect to Render

After pushing to GitHub:

1. Go to Render dashboard
2. Create new Web Service
3. Connect GitHub account
4. Select `toyyibpay-backend` repository
5. Configure and deploy

See `DEPLOYMENT.md` for detailed instructions.

## ✅ Verification Checklist

- [ ] Repository created on GitHub
- [ ] Local repository initialized
- [ ] Remote added and connected
- [ ] Initial commit pushed
- [ ] All files uploaded correctly
- [ ] `.env` NOT in repository
- [ ] `.gitignore` working correctly
- [ ] Repository ready for Render connection

## 🎉 Success!

Your code is now on GitHub and ready for deployment!

Next steps:
1. Set up environment variables in Render
2. Connect repository to Render
3. Deploy your backend
4. Test the deployment

See `DEPLOYMENT.md` for next steps.

