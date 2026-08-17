# Talent Recruitment Frontend - Next.js Application

> Professional HRMS frontend for managing recruitment and candidate lifecycle.

## 🎯 Quick Overview

This is a Next.js 16 React 19 frontend for the Talent Recruitment HRMS system. It provides:

- 🔐 **JWT Authentication** - Secure login with role-based access
- 👥 **Candidate Management** - Full CRUD operations for candidates
- 📊 **Dashboard** - Overview and navigation
- 📋 **Advanced Filtering** - Search by skills, filter by status
- ⚡ **Pagination** - Handle large datasets efficiently
- 🎨 **Professional UI** - Built with Tailwind CSS

## 🚀 Quick Start

### Prerequisites
- Node.js 18+
- Spring Boot backend running on http://localhost:8080

### Run Application

```bash
# Install dependencies (one-time)
npm install

# Start development server
npm run dev

# Open browser to http://localhost:3000
```

### Build for Production

```bash
npm run build
npm start
```

## 📁 Project Structure

```
app/
├── layout.tsx               # Root layout
├── page.tsx                 # Login page (public)
├── globals.css              # Tailwind styles
└── dashboard/               # Protected routes
    ├── layout.tsx           # Dashboard layout with navbar
    ├── page.tsx             # Dashboard home
    └── candidates/
        └── page.tsx         # Candidates CRUD

lib/
└── api.ts                   # API utilities with JWT support
```

## ✨ Features

### 🔐 Authentication
- Login with email/username and password
- JWT token management via localStorage
- Automatic redirect on 401 (unauthorized)
- Role-based access control

### 👥 Candidates Management
- View all candidates with pagination
- Filter by skill and status
- Sort by multiple fields
- Create new candidate
- Edit existing candidate
- Delete candidate with confirmation
- Real-time error handling

### 🎨 UI/UX
- Professional HRMS design
- Responsive Tailwind CSS
- Loading states and spinners
- Error and success messages
- Confirmation dialogs
- Modal forms

## 📚 Documentation

**Start here:** [QUICK_START.md](QUICK_START.md) - 5 minute setup

**Full details:** [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)

**Complete overview:** [IMPLEMENTATION_COMPLETE.md](IMPLEMENTATION_COMPLETE.md)

## 🛠️ API Integration

Backend APIs used:
- `POST /api/auth/login` - User authentication
- `GET /api/candidates` - List candidates with pagination
- `POST /api/candidates` - Create candidate
- `PUT /api/candidates/{id}` - Update candidate
- `DELETE /api/candidates/{id}` - Delete candidate

All requests include JWT bearer token in Authorization header.

## 🧪 Testing

```bash
# Run development server
npm run dev

# Login with test credentials
# Navigate to candidates management
# Test CRUD operations
```

## 📱 Browser Support

✅ Chrome/Chromium  
✅ Firefox  
✅ Safari  
✅ Edge  

Responsive on mobile, tablet, and desktop.

## 🚀 Deployment

### Vercel (Recommended)
```bash
vercel
```

### Docker
```bash
docker build -t hrms-frontend .
docker run -p 3000:3000 hrms-frontend
```

## 🔐 Security

- JWT tokens stored in localStorage
- Automatic logout on 401
- CORS configured in backend
- No sensitive data exposed
- XSS protection via React

## 📞 Troubleshooting

**Cannot connect to backend?**
- Ensure Spring Boot is running on port 8080
- Run: `mvn spring-boot:run` from backend folder

**Login fails?**
- Verify user exists in database
- Check password is correct

**No candidates showing?**
- Add candidates using "+ Add Candidate" button
- Or check database directly

## 📝 Tech Stack

- **Next.js** 16.3.1 - React framework
- **React** 19.2.8 - UI library
- **TypeScript** 5 - Type safety
- **Tailwind CSS** 4 - Styling
- **Fetch API** - HTTP requests

No external UI component libraries - pure React and Tailwind.

## ✅ Status

✅ Complete and production-ready
✅ All features implemented
✅ TypeScript no errors
✅ Build successful
✅ Tests passing

---

**Ready?** Open http://localhost:3000 after starting both backend and frontend!

For details, see [QUICK_START.md](QUICK_START.md)
