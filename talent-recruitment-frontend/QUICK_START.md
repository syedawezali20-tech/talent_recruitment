# 🚀 Quick Start Guide - Talent Recruitment HRMS

## 🎯 What's Ready

Your complete HRMS system is ready with:
- ✅ **Backend**: Spring Boot 3.3.2 REST APIs
- ✅ **Frontend**: Next.js 16 dashboard
- ✅ **Database**: PostgreSQL/MySQL
- ✅ **Authentication**: JWT with roles (ADMIN, HR, RECRUITER)

---

## ⚡ Quick Start (5 minutes)

### Step 1: Start the Backend
```bash
cd C:\Users\Enfec Solutions\Desktop\demo
mvn spring-boot:run
```
Wait for message: `Started TalentRecruitmentApplication in X.XXXs`

### Step 2: Start the Frontend
```bash
cd C:\Users\Enfec Solutions\Desktop\demo\talent-recruitment-frontend
npm run dev
```
You'll see: `▲ Next.js X.X.X Local: http://localhost:3000`

### Step 3: Open in Browser
```
http://localhost:3000
```

### Step 4: Login
Use credentials from your database:
- **Username/Email**: recruiter
- **Password**: password123 (or your actual password)

---

## 🎪 What You Can Do Now

### 📋 **View Candidates**
- See all candidates in paginated table
- View: ID, Name, Email, Phone, Skills, Experience, Status
- Shows total count and current page

### 🔍 **Filter & Sort**
- Filter by **skill** (e.g., "Java")
- Filter by **status** (ACTIVE, INACTIVE, HIRED, REJECTED)
- Sort by ID, First Name, Created Date
- Choose Ascending or Descending

### ➕ **Create Candidate**
1. Click "+ Add Candidate"
2. Fill form:
   - First Name (required)
   - Last Name (required)
   - Email (required, must be unique)
   - Phone
   - Skills (comma-separated, e.g., "Java, Spring, REST")
   - Experience (years)
   - Resume URL
   - Status
3. Click "Save"
4. See success message and candidate added to table

### ✏️ **Edit Candidate**
1. Click "Edit" on any candidate
2. Modify any field
3. Click "Save"
4. See updated data in table

### 🗑️ **Delete Candidate**
1. Click "Delete" on any candidate
2. Confirm in modal
3. See deletion confirmation
4. Candidate removed from table

### 🚪 **Logout**
- Click "Logout" in top-right navbar
- Returns to login page
- Token cleared from browser

---

## 📍 URLs

| URL | Purpose | Access |
|-----|---------|--------|
| http://localhost:3000 | Login Page | Public |
| http://localhost:3000/dashboard | Dashboard Home | After login |
| http://localhost:3000/dashboard/candidates | Candidates Management | After login |
| http://localhost:8080/swagger-ui.html | Backend API Docs | Public (optional) |

---

## 🔐 Login Credentials

Create a test user in Spring Boot backend:

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Password123!",
  "role": "RECRUITER"
}
```

Or use existing database credentials.

---

## ✨ Features Implemented

### Frontend
- ✅ Login with JWT authentication
- ✅ Dashboard with welcome message
- ✅ Candidate table with real data
- ✅ Create/Read/Update/Delete candidates
- ✅ Pagination (page/size controls)
- ✅ Filtering (skill, status)
- ✅ Sorting (multiple fields)
- ✅ Error/success messages
- ✅ Loading states
- ✅ Responsive design
- ✅ Logout functionality

### Backend (Pre-built)
- ✅ User registration & login
- ✅ JWT authentication
- ✅ Role-based authorization
- ✅ Candidate CRUD APIs
- ✅ Job CRUD APIs
- ✅ Global error handling
- ✅ Swagger API documentation
- ✅ MySQL database
- ✅ Password encryption (BCrypt)

---

## 🛠️ Environment Setup

### Backend (Spring Boot)
File: `src/main/resources/application.properties`

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/talent_recruitment_db
DB_USERNAME=root
DB_PASSWORD=password

# JWT
JWT_SECRET=myVeryLongSecureJwtSecretKeyForTalentRecruitment2026!
JWT_EXPIRATION=86400000
```

Override with environment variables:
```bash
set DB_URL=jdbc:mysql://localhost:3306/talent_recruitment_db
set DB_USERNAME=root
set DB_PASSWORD=password
mvn spring-boot:run
```

### Frontend (Next.js)
File: `lib/api.ts`

```typescript
const BASE_URL = "http://localhost:8080";
```

---

## 🧪 Testing Workflow

1. **Login Test**
   - Open http://localhost:3000
   - Enter valid credentials
   - ✅ Should redirect to dashboard

2. **View Candidates Test**
   - Click "View Candidates"
   - ✅ Should show candidate table with real data
   - ✅ Should show pagination info

3. **Create Test**
   - Click "+ Add Candidate"
   - Fill form with new candidate
   - ✅ Should see success message
   - ✅ New candidate should appear in table

4. **Edit Test**
   - Click "Edit" on a candidate
   - Change status to "HIRED"
   - ✅ Should see success message
   - ✅ Status should update in table

5. **Filter Test**
   - Enter skill filter (e.g., "Java")
   - Click "Apply Filters"
   - ✅ Table should only show candidates with Java skill

6. **Delete Test**
   - Click "Delete" on a candidate
   - Confirm in modal
   - ✅ Should see success message
   - ✅ Candidate should be removed

7. **Logout Test**
   - Click "Logout"
   - ✅ Should redirect to login
   - ✅ Should not be able to access dashboard

---

## 📁 Project Structure

```
Desktop/demo/
├── src/                          # Spring Boot backend
│   ├── main/java/com/example/talentrecruitment/
│   │   ├── auth/                 # Authentication module
│   │   ├── candidate/            # Candidate module
│   │   ├── job/                  # Job module
│   │   ├── config/               # Spring configuration
│   │   └── security/             # JWT security
│   ├── test/java/...             # Unit tests
│   └── main/resources/
│       └── application.properties # Database config
├── pom.xml                       # Maven dependencies
├── README.md                     # Backend docs
│
└── talent-recruitment-frontend/
    ├── app/
    │   ├── layout.tsx            # Root layout
    │   ├── page.tsx              # Login page
    │   ├── dashboard/            # Dashboard pages
    │   │   ├── layout.tsx        # Dashboard wrapper
    │   │   ├── page.tsx          # Dashboard home
    │   │   └── candidates/       # Candidates page
    │   │       └── page.tsx
    │   └── globals.css
    ├── lib/
    │   └── api.ts                # API utilities
    ├── package.json
    ├── tsconfig.json
    └── IMPLEMENTATION_SUMMARY.md
```

---

## 🔍 Debugging Tips

### Check Backend Logs
```bash
# In terminal where Spring Boot is running
# Look for: "Started TalentRecruitmentApplication"
```

### Check Frontend Logs
```bash
# In terminal where npm run dev is running
# Look for: "▲ Next.js X.X.X Local: http://localhost:3000"
```

### Browser DevTools
```javascript
// Check if token exists
localStorage.getItem('token')

// Check if user is logged in
localStorage.getItem('username')
localStorage.getItem('role')

// View API requests in Network tab
// Filter by "XHR" to see fetch requests
```

### Backend API Testing
Visit Swagger UI: http://localhost:8080/swagger-ui.html
- Try each endpoint
- See request/response formats
- Debug authentication issues

---

## ⚠️ Common Issues & Solutions

### "Cannot connect to backend"
**Problem**: Frontend shows network error
**Solution**: 
- Make sure Spring Boot is running on http://localhost:8080
- Run: `mvn spring-boot:run` from backend folder

### "Unauthorized" error
**Problem**: Login fails even with correct credentials
**Solution**:
- Check if user exists in database
- Verify password is correct
- Check backend logs for errors

### "CORS error" in browser console
**Problem**: Browser blocks request from localhost:3000 to localhost:8080
**Solution**: 
- This should already be fixed in backend
- If not, check SecurityConfig.java CORS configuration
- Verify corsConfigurationSource() bean exists

### "Page shows 'No candidates found'"
**Problem**: Candidate table is empty
**Solution**:
- Add a candidate using "+ Add Candidate" button
- Or check database directly
- Verify backend is running and connected to database

---

## 📞 Support

If you encounter issues:

1. **Check terminal output** for error messages
2. **View browser console** (F12 → Console) for JavaScript errors
3. **Check browser Network tab** to see API responses
4. **Verify backend is running** at http://localhost:8080/swagger-ui.html
5. **Check database** connection in application.properties

---

## 🎓 Next Steps

Once you're comfortable with the current setup:

1. **Customize styling** - Modify Tailwind classes in .tsx files
2. **Add more filters** - Extend candidates page filtering
3. **Implement Jobs page** - Similar to candidates page
4. **Add email notifications** - Send alerts on candidate status change
5. **Create admin panel** - Manage users and roles
6. **Add dark mode** - Extend Tailwind configuration
7. **Deploy to production** - Use Vercel (frontend) & AWS/Azure (backend)

---

## ✅ Implementation Status

| Component | Status | Notes |
|-----------|--------|-------|
| Backend API | ✅ Complete | Production-ready, no changes needed |
| Login Page | ✅ Complete | JWT auth working |
| Dashboard | ✅ Complete | Welcome page functional |
| Candidates List | ✅ Complete | Full CRUD + pagination + filters |
| Jobs Page | 🟡 Planned | Modal available for future implementation |
| Recruitment Pipeline | 🟡 Planned | Placeholder card on dashboard |
| Admin Panel | 🟡 Planned | For future user management |

---

**Your talent recruitment system is ready to use! 🎉**

Start backend → Start frontend → Open browser → Login → Manage candidates!
