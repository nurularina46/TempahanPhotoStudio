# PowerShell script to update .env file with Supabase credentials

$envContent = @"
# Server Configuration
PORT=3000
NODE_ENV=development

# ToyyibPay Sandbox Configuration
TOYYIBPAY_SECRET_KEY=01xkc9i3-b9rr-b830-afc5-j551ib8c4uib
TOYYIBPAY_CATEGORY_CODE=us4wsa6w
TOYYIBPAY_BASE_URL=https://dev.toyyibpay.com/index.php/api/

# Frontend URLs
RETURN_URL=http://localhost:3000/payment-success
CALLBACK_URL=http://localhost:3000/api/toyyibpay/callback

# Supabase Configuration
SUPABASE_URL=https://kebkfjomzodlmocqhxnv.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtlYmtmam9tem9kbG1vY3FoeG52Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM4MzU1NTIsImV4cCI6MjA3OTQxMTU1Mn0.WXdyKj9XuJ28zI-9TJz3hb7aC6OCYJvVKhKbCMBceCo
SUPABASE_SERVICE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtlYmtmam9tem9kbG1vY3FoeG52Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2MzgzNTU1MiwiZXhwIjoyMDc5NDExNTUyfQ.oRKp_0BDDy8vmoNJdyehh5j7S5piezkN8czkhuIX_P4
"@

$envContent | Out-File -FilePath ".env" -Encoding utf8 -NoNewline
Write-Host "✅ .env file updated successfully with Supabase credentials!" -ForegroundColor Green

