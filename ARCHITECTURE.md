# MedFlow Auth Service - Complete Architecture Document

## Project Overview
A professional, production-style authentication and user management system built with Java 21, Spring Boot, PostgreSQL, Spring Security, and modern web technologies.

---

## 1. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         BROWSER (User)                          │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND LAYER                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Thymeleaf Templates (HTML)                               │  │
│  │ - Layout: Navbar, Footer, Sidebar (Fragments)           │  │
│  │ - Pages: Login, Signup, Dashboard, Profile, Admin       │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Static Resources                                         │  │
│  │ - Bootstrap 5 CSS                                        │  │
│  │ - Custom CSS                                             │  │
│  │ - JavaScript (Validation, UX)                            │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    HTTP Request/Response
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                  SPRING MVC CONTROLLER LAYER                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ HomeController          - Public pages                   │  │
│  │ AuthController          - Login, Signup, Logout          │  │
│  │ UserController          - User dashboard & profile       │  │
│  │ AdminController         - Admin management              │  │
│  │ ErrorController         - Error handling                │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   SPRING SECURITY LAYER                         │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ SecurityConfig          - Security configuration        │  │
│  │ CustomUserDetailsService - User authentication           │  │
│  │ Authentication Filters  - Request validation            │  │
│  │ Authorization Checks    - Role-based access             │  │
│  │ Password Encoder        - BCrypt hashing                │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SERVICE LAYER (Business Logic)              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ AuthService             - Registration, authentication   │  │
│  │ UserService             - User operations               │  │
│  │ AdminService            - Admin operations              │  │
│  │ EmailService            - Password reset emails         │  │
│  │ ValidationService       - Custom validations            │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                REPOSITORY LAYER (Data Access)                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Spring Data JPA Repositories                             │  │
│  │ - UserRepository                                         │  │
│  │ - RoleRepository                                         │  │
│  │ - Automatic SQL generation                              │  │
│  │ - Query optimization                                     │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│           JPA/HIBERNATE ORM & CONNECTION POOLING               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Object-Relational Mapping                               │  │
│  │ Connection pooling                                       │  │
│  │ Lazy/Eager loading strategies                            │  │
│  │ Transaction management                                   │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              POSTGRESQL DATABASE                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ users table        - User credentials & info            │  │
│  │ roles table        - Role definitions                    │  │
│  │ user_roles table   - User-role assignments              │  │
│  │ password_tokens table - Reset token storage             │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Architecture Flow Explanation

1. **Browser** - User interacts with web interface
2. **Thymeleaf Frontend** - Server-side template rendering (HTML + CSS + JS)
3. **Spring MVC Controller** - Handles HTTP requests, no business logic here
4. **Spring Security** - Authenticates users, enforces authorization, manages sessions
5. **Service Layer** - Core business logic, validations, transactions
6. **Repository Layer** - Data access abstraction using Spring Data JPA
7. **Hibernate/JPA** - Maps Java objects to database, generates SQL
8. **PostgreSQL** - Persistent data storage

---

## 2. Page Architecture

### 2.1 Public Pages (Accessible without login)

#### Home Page
- **URL**: `GET /`
- **Purpose**: Landing page, application overview
- **Access**: Everyone
- **Data Displayed**: Welcome message, links to signup/login, about info
- **Actions**: Navigate to Login, Signup, About, Contact
- **Controller**: `HomeController.home()`
- **Service**: None (static content)
- **Database**: None
- **Template**: `templates/home.html`

#### Login Page
- **URL**: `GET /login`
- **Purpose**: User authentication
- **Access**: Unauthenticated users only
- **Data Displayed**: Login form (email, password), error messages
- **Actions**: Submit login form, signup link, forgot password link
- **Controller**: `AuthController.showLoginForm()`
- **Service**: None (form display)
- **Database**: None
- **Template**: `templates/auth/login.html`

#### Login Form Submission
- **URL**: `POST /login`
- **Purpose**: Process user authentication
- **Access**: Unauthenticated users
- **Actions**: Spring Security handles authentication
- **Controller**: Spring Security (automatic)
- **Service**: `CustomUserDetailsService.loadUserByUsername()`
- **Database**: Query users table, verify password
- **Result**: Success → Redirect to dashboard, Failure → Error message

#### Signup/Register Page
- **URL**: `GET /signup`
- **Purpose**: New user registration
- **Access**: Unauthenticated users only
- **Data Displayed**: Registration form (name, email, password, confirm password)
- **Actions**: Submit registration form, login link
- **Controller**: `AuthController.showSignupForm()`
- **Service**: None (form display)
- **Database**: None
- **Template**: `templates/auth/signup.html`

#### Signup Form Submission
- **URL**: `POST /signup`
- **Purpose**: Create new user account
- **Access**: Unauthenticated users
- **Validation**: Email uniqueness, password match, password strength
- **Actions**: Hash password, create user, assign USER role, send welcome email
- **Controller**: `AuthController.registerUser(SignupRequest)`
- **Service**: `AuthService.registerUser()`, `EmailService.sendWelcomeEmail()`
- **Database**: Insert into users table, insert into user_roles table
- **Result**: Success → Redirect to login with message, Failure → Validation errors

#### Forgot Password Page
- **URL**: `GET /forgot-password`
- **Purpose**: Request password reset
- **Access**: Unauthenticated users
- **Data Displayed**: Email input form
- **Actions**: Submit email
- **Controller**: `AuthController.showForgotPasswordForm()`
- **Service**: None (form display)
- **Database**: None
- **Template**: `templates/auth/forgot-password.html`

#### Forgot Password Form Submission
- **URL**: `POST /forgot-password`
- **Purpose**: Generate password reset token
- **Access**: Unauthenticated users
- **Actions**: Generate reset token, send email with reset link
- **Controller**: `AuthController.processForgotPassword(String email)`
- **Service**: `AuthService.createPasswordResetToken()`, `EmailService.sendResetEmail()`
- **Database**: Insert into password_tokens table
- **Result**: "Check your email" message

#### Reset Password Page
- **URL**: `GET /reset-password`
- **Purpose**: Reset password using token
- **Access**: Unauthenticated users with valid token
- **Data Displayed**: New password form
- **Actions**: Submit new password
- **Controller**: `AuthController.showResetPasswordForm(String token)`
- **Service**: `AuthService.validateResetToken()`
- **Database**: Query password_tokens table
- **Template**: `templates/auth/reset-password.html`

#### Reset Password Form Submission
- **URL**: `POST /reset-password`
- **Purpose**: Update password with reset token
- **Access**: Unauthenticated users with valid token
- **Actions**: Validate token, hash new password, update user
- **Controller**: `AuthController.resetPassword(PasswordResetRequest)`
- **Service**: `AuthService.resetPassword()`
- **Database**: Update users table, delete from password_tokens table
- **Result**: Success → Redirect to login, Failure → Error message

#### About Page
- **URL**: `GET /about`
- **Purpose**: Application information
- **Access**: Everyone
- **Data Displayed**: Company/project information
- **Actions**: Navigate to other pages
- **Controller**: `HomeController.about()`
- **Service**: None
- **Database**: None
- **Template**: `templates/about.html`

#### Contact Page
- **URL**: `GET /contact`
- **Purpose**: Contact form
- **Access**: Everyone
- **Data Displayed**: Contact form
- **Actions**: Submit contact message
- **Controller**: `HomeController.showContactForm()`, `HomeController.submitContact()`
- **Service**: `EmailService.sendContactEmail()`
- **Database**: Optional - contact_messages table for persistence
- **Template**: `templates/contact.html`

---

### 2.2 Authenticated User Pages (Requires USER role or higher)

#### Dashboard
- **URL**: `GET /dashboard`
- **Purpose**: Main application page for authenticated users
- **Access**: Authenticated users (USER or ADMIN role)
- **Data Displayed**: User greeting, quick stats, recent activity
- **Actions**: Navigate to profile, settings, logout, admin (if ADMIN)
- **Controller**: `UserController.dashboard(Model, Principal)`
- **Service**: `UserService.getUserStats()`, `UserService.getRecentActivity()`
- **Database**: Query users table, activity-related tables
- **Template**: `templates/user/dashboard.html`

#### User Profile Page
- **URL**: `GET /profile`
- **Purpose**: View user profile information
- **Access**: Authenticated users
- **Data Displayed**: Name, email, account creation date, account status
- **Actions**: Edit profile, change password, delete account
- **Controller**: `UserController.viewProfile(Model, Principal)`
- **Service**: `UserService.getUserProfile()`
- **Database**: Query users table
- **Template**: `templates/user/profile.html`

#### Edit Profile Page
- **URL**: `GET /profile/edit`
- **Purpose**: Edit user information
- **Access**: Authenticated users
- **Data Displayed**: Edit form (name, email)
- **Actions**: Submit changes
- **Controller**: `UserController.showEditProfileForm(Model, Principal)`
- **Service**: None (form display)
- **Database**: None
- **Template**: `templates/user/edit-profile.html`

#### Edit Profile Form Submission
- **URL**: `POST /profile/update`
- **Purpose**: Update user profile
- **Access**: Authenticated users
- **Validation**: Email uniqueness (if changed), non-empty fields
- **Actions**: Update user information
- **Controller**: `UserController.updateProfile(UserProfileUpdateRequest, Principal)`
- **Service**: `UserService.updateProfile()`
- **Database**: Update users table
- **Result**: Success → Redirect to profile with message, Failure → Validation errors

#### Settings Page
- **URL**: `GET /settings`
- **Purpose**: User account settings
- **Access**: Authenticated users
- **Data Displayed**: Settings form (notifications, privacy preferences)
- **Actions**: Update settings, change password link
- **Controller**: `UserController.showSettings(Model, Principal)`
- **Service**: `UserService.getUserSettings()`
- **Database**: Query user_settings table
- **Template**: `templates/user/settings.html`

#### Settings Form Submission
- **URL**: `POST /settings/update`
- **Purpose**: Update user settings
- **Access**: Authenticated users
- **Actions**: Save settings
- **Controller**: `UserController.updateSettings(UserSettingsRequest, Principal)`
- **Service**: `UserService.updateSettings()`
- **Database**: Update user_settings table
- **Result**: Success → Confirmation message

#### Change Password Page
- **URL**: `GET /change-password`
- **Purpose**: Change password for authenticated user
- **Access**: Authenticated users
- **Data Displayed**: Change password form (current password, new password, confirm)
- **Actions**: Submit password change
- **Controller**: `UserController.showChangePasswordForm(Model)`
- **Service**: None (form display)
- **Database**: None
- **Template**: `templates/user/change-password.html`

#### Change Password Form Submission
- **URL**: `POST /change-password`
- **Purpose**: Update user password
- **Access**: Authenticated users
- **Validation**: Verify current password, password match, password strength
- **Actions**: Hash new password, update user
- **Controller**: `UserController.changePassword(PasswordChangeRequest, Principal)`
- **Service**: `UserService.changePassword()`
- **Database**: Update users table
- **Result**: Success → Confirmation message, Failure → Validation error

#### Logout
- **URL**: `POST /logout` or `GET /logout`
- **Purpose**: End user session
- **Access**: Authenticated users
- **Actions**: Invalidate session, clear authentication
- **Controller**: Spring Security handles logout
- **Service**: None
- **Database**: None
- **Result**: Redirect to home or login page

---

### 2.3 Admin Pages (Requires ADMIN role)

#### Admin Login Page
- **URL**: `GET /admin/login`
- **Purpose**: Admin authentication (separate from user login)
- **Access**: Unauthenticated, intended for admins
- **Data Displayed**: Login form
- **Actions**: Submit login
- **Controller**: `AdminController.showAdminLoginForm()`
- **Service**: None (form display)
- **Database**: None
- **Template**: `templates/admin/login.html`
- **Note**: Could use same login as regular users if they have ADMIN role

#### Admin Dashboard
- **URL**: `GET /admin/dashboard`
- **Purpose**: Admin overview
- **Access**: ADMIN role only
- **Data Displayed**: System stats, user count, recent registrations, alerts
- **Actions**: Navigate to user management, settings, logout
- **Controller**: `AdminController.dashboard(Model)`
- **Service**: `AdminService.getSystemStats()`, `AdminService.getRecentRegistrations()`
- **Database**: Query users table, calculate statistics
- **Template**: `templates/admin/dashboard.html`

#### User Management List
- **URL**: `GET /admin/users`
- **Purpose**: List all users with pagination, search, filter
- **Access**: ADMIN role only
- **Data Displayed**: User table (id, name, email, role, status, created_at, actions)
- **Actions**: View details, edit, deactivate, delete, search, filter by role/status
- **Controller**: `AdminController.listUsers(Model, Pageable)`
- **Service**: `AdminService.getAllUsers()`
- **Database**: Query users table with pagination
- **Template**: `templates/admin/users/list.html`

#### User Details Page
- **URL**: `GET /admin/users/{userId}`
- **Purpose**: View detailed user information
- **Access**: ADMIN role only
- **Data Displayed**: User full info, role, status, created date, last login, activity
- **Actions**: Edit, deactivate, delete, view activity log
- **Controller**: `AdminController.viewUser(Model, Long userId)`
- **Service**: `AdminService.getUserDetails()`
- **Database**: Query users table, activity logs
- **Template**: `templates/admin/users/detail.html`

#### Edit User Page
- **URL**: `GET /admin/users/{userId}/edit`
- **Purpose**: Edit user information
- **Access**: ADMIN role only
- **Data Displayed**: Edit form (name, email, role, status)
- **Actions**: Submit changes
- **Controller**: `AdminController.showEditUserForm(Model, Long userId)`
- **Service**: None (form display)
- **Database**: Query single user
- **Template**: `templates/admin/users/edit.html`

#### Edit User Form Submission
- **URL**: `POST /admin/users/{userId}/update`
- **Purpose**: Update user information by admin
- **Access**: ADMIN role only
- **Actions**: Update user, update role assignment
- **Controller**: `AdminController.updateUser(Long userId, UserAdminUpdateRequest)`
- **Service**: `AdminService.updateUser()`
- **Database**: Update users table, update user_roles table
- **Result**: Redirect to user details with success message

#### Deactivate/Activate User
- **URL**: `POST /admin/users/{userId}/toggle-status`
- **Purpose**: Deactivate or activate user account
- **Access**: ADMIN role only
- **Actions**: Toggle user enabled status
- **Controller**: `AdminController.toggleUserStatus(Long userId)`
- **Service**: `AdminService.toggleUserStatus()`
- **Database**: Update users table (enabled field)
- **Result**: Confirmation message

#### Delete User
- **URL**: `POST /admin/users/{userId}/delete`
- **Purpose**: Permanently delete user account
- **Access**: ADMIN role only
- **Actions**: Delete user and related data
- **Controller**: `AdminController.deleteUser(Long userId)`
- **Service**: `AdminService.deleteUser()`
- **Database**: Delete from user_roles, delete from users
- **Result**: Redirect to user list with confirmation

---

## 3. Complete User Flow

### 3.1 New User Registration Flow

```
User visits home (/)
    ↓
Clicks "Sign Up"
    ↓
Navigates to signup page (GET /signup)
    ↓
Fills form: Name, Email, Password, Confirm Password
    ↓
Clicks "Register"
    ↓
Browser submits POST /signup
    ↓
AuthController.registerUser() receives request
    ↓
Validation Service validates:
  - Name is not empty
  - Email format is valid
  - Email is not already registered in database
  - Password length >= 8 characters
  - Password contains uppercase, lowercase, numbers
  - Passwords match
    ↓
If validation fails:
  Return signup form with error messages
    ↓
If validation succeeds:
  - AuthService.registerUser() executes:
    - Create new User object
    - Hash password using BCrypt
    - Set User enabled = true
    - Assign USER role
    - Save to database (users table)
    - Record created_at and updated_at
    ↓
  - EmailService.sendWelcomeEmail() executes:
    - Send welcome email to user
    ↓
  Redirect to login page with success message
    ↓
User sees "Registration successful, please login"
```

### 3.2 User Login Flow

```
User visits login page (GET /login)
    ↓
Fills form: Email, Password
    ↓
Clicks "Login"
    ↓
Browser submits POST /login (Spring Security intercepts)
    ↓
Spring Security AuthenticationManager processes:
  - AuthenticationFilter extracts email and password
  - ProviderManager calls UserDetailsService
    ↓
CustomUserDetailsService.loadUserByUsername(email) executes:
  - Query users table by email
  - If user not found:
    UsernameNotFoundException thrown
    ↓
  - If user found:
    - Get user record
    - Load user_roles and related Role objects
    - Build UserDetails object with authorities
    ↓
Spring Security PasswordEncoder.matches():
  - Compare provided password with BCrypt hash
  - If doesn't match:
    BadCredentialsException thrown
    ↓
  - If matches:
    Authentication successful
    ↓
If authentication fails:
  Redirect to /login with error parameter
  Display "Invalid email or password"
    ↓
If authentication succeeds:
  - Spring Security creates SecurityContext
  - Create HttpSession with authentication
  - Set SecurityContext in session
  - Create authentication cookie (optional)
    ↓
  Determine where to redirect based on roles:
  - If ADMIN role: Redirect to /admin/dashboard
  - If only USER role: Redirect to /dashboard
    ↓
User sees dashboard page
```

### 3.3 Authenticated User Session Flow

```
User logged in, has active session
    ↓
User navigates to /dashboard
    ↓
Spring Security DispatcherServlet processes request:
  - SecurityFilterChain checks request
  - Session cookie extracted
  - SecurityContext loaded from session
  - User's Authentication and roles verified
    ↓
UserController.dashboard() executes:
  - Principal parameter contains authenticated user info
  - UserService.getUserStats() fetches user data
  - Model attributes populated
    ↓
Thymeleaf renders dashboard.html:
  - Navbar includes user name and logout link
  - Sidebar shows user menu (Profile, Settings, etc.)
  - Dashboard content displays user information
    ↓
Page returns to browser
```

### 3.4 Admin User Flow

```
Admin logs in with email/password
    ↓
CustomUserDetailsService loads user with roles
    ↓
Confirms user has ADMIN role
    ↓
Authentication successful
    ↓
Redirect to /admin/dashboard
    ↓
AdminController.dashboard() executes:
  - Verify ADMIN authority (Spring Security annotation)
  - Access denied if not ADMIN
  - Query system statistics
    ↓
Admin Dashboard displays:
  - Total user count
  - Recent registrations
  - System alerts
  - Links to user management
    ↓
Admin clicks "Users"
    ↓
Navigates to /admin/users
    ↓
AdminController.listUsers() executes:
  - Query users table with pagination
  - Fetch 20 users per page
    ↓
Displays user list with:
  - User name, email, role, status
  - Action buttons (View, Edit, Deactivate, Delete)
    ↓
Admin clicks "Edit" for specific user
    ↓
Navigates to /admin/users/{userId}/edit
    ↓
AdminController.showEditUserForm() executes:
  - Query specific user from database
  - Populate form with user data
    ↓
Admin modifies fields and submits
    ↓
AdminController.updateUser() executes:
  - Validate changes
  - Update users table
  - Update user_roles if role changed
    ↓
Redirect to user detail page with success message
```

### 3.5 Logout Flow

```
User clicks "Logout"
    ↓
Browser submits POST /logout
    ↓
Spring Security LogoutFilter processes:
  - Get HttpServletRequest and HttpServletResponse
  - Call registered LogoutHandlers:
    - SecurityContextLogoutHandler:
      * Invalidate HttpSession
      * Clear SecurityContextHolder
    - CookieClearingLogoutHandler:
      * Remove authentication cookies
    ↓
  - Call LogoutSuccessHandler:
    - Default: Redirect to /login?logout
    ↓
Browser redirects to home or login
    ↓
User sees login page with "You have been logged out" message
```

### 3.6 Access Denied Flow

```
Authenticated user tries to access admin page
Example: /admin/dashboard without ADMIN role
    ↓
Spring Security checks @PreAuthorize("hasRole('ADMIN')")
    ↓
User's authorities checked:
  - Has ROLE_USER only
  - Does NOT have ROLE_ADMIN
    ↓
Access denied
    ↓
Spring Security ExceptionTranslationFilter catches AccessDeniedException
    ↓
AccessDeniedHandler processes:
  - Redirect to /error/403 (Forbidden)
  - Or return 403 status
    ↓
ErrorController.handleAccessDenied() executes:
  - Prepare error page
    ↓
Display friendly error message:
  "You do not have permission to access this page"
  With link back to dashboard
```

### 3.7 Forgot Password Flow

```
User clicks "Forgot Password?"
    ↓
Navigates to /forgot-password
    ↓
Enters email address
    ↓
Submits POST /forgot-password
    ↓
AuthController.processForgotPassword() executes:
  - Query users table for email
  - If email not found:
    Display message: "If account exists, email will be sent"
    (Security: Don't reveal whether email exists)
    ↓
  - If email found:
    - AuthService.createPasswordResetToken() executes:
      * Generate secure random token (UUID)
      * Set expiration time (15 minutes)
      * Save to password_tokens table
    ↓
    - EmailService.sendResetEmail() executes:
      * Compose email with reset link:
        /reset-password?token={token}
      * Send to user email
    ↓
Display message: "Check your email for reset instructions"
```

### 3.8 Reset Password Flow

```
User receives reset email
    ↓
Clicks reset link: /reset-password?token=abc123xyz
    ↓
AuthController.showResetPasswordForm() executes:
  - Extract token from URL
  - AuthService.validateResetToken() checks:
    * Query password_tokens table
    * Check token exists
    * Check expiration time
    * If invalid/expired: throw TokenException
    ↓
If token invalid:
  Display error: "Reset link expired or invalid"
  ↓
If token valid:
  Display reset password form
    ↓
User enters new password, confirms
    ↓
Submits POST /reset-password
    ↓
AuthController.resetPassword() executes:
  - Validate new passwords match
  - Validate password strength
  - AuthService.resetPassword() executes:
    * Query password_tokens to get user_id
    * Hash new password
    * Update users table password
    * Delete from password_tokens (token used)
    ↓
Display: "Password reset successful, please login"
    ↓
Redirect to login page
```

---

## 4. Authentication Architecture

### 4.1 Spring Security Overview

Spring Security is the Java equivalent of Django's authentication system:

| Django | Spring Security |
|--------|-----------------|
| django.contrib.auth | Spring Security Framework |
| User model | JPA @Entity User + UserDetails |
| authenticate() | AuthenticationManager.authenticate() |
| login() | SecurityContext + HttpSession |
| @login_required decorator | @PreAuthorize("isAuthenticated()") |
| @admin_required | @PreAuthorize("hasRole('ADMIN')") |
| password hashing (PBKDF2) | BCryptPasswordEncoder |
| permission system | GrantedAuthority / Role |

### 4.2 Authentication Flow Architecture

```
┌─────────────────────────────────────┐
│   Browser Login Request             │
│  POST /login                        │
│  (email, password)                  │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   Spring Security Servlet Filter    │
│   Chain                             │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   UsernamePasswordAuthentication    │
│   Filter                            │
│  - Extracts email & password        │
│  - Creates Authentication token     │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   AuthenticationManager             │
│  - Delegates to ProviderManager     │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   DaoAuthenticationProvider         │
│  - Calls UserDetailsService         │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   CustomUserDetailsService          │
│  - Queries database for user        │
│  - Loads user and roles             │
│  - Returns UserDetails object       │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   PasswordEncoder (BCrypt)          │
│  - Compares provided password       │
│  - With stored hashed password      │
└────────────┬────────────────────────┘
             │
             ├─ Password mismatch ─────┐
             │                         │
             │   BadCredentialsException
             │                         │
             ▼                         │
   ┌─────────────────┐                │
   │ Create auth     │                │
   │ token (SUCCESS) │                ▼
   └────────┬────────┘       ┌──────────────────┐
            │                │ Authentication   │
            │                │ Failure Handler  │
            │                │ Redirect to      │
            │                │ /login?error     │
            │                └──────────────────┘
            │
            ▼
   ┌──────────────────────┐
   │ SecurityContextHolder│
   │ Store authentication │
   │ in SecurityContext   │
   └────────┬─────────────┘
            │
            ▼
   ┌──────────────────────┐
   │ HttpSession          │
   │ Save SecurityContext │
   │ Create session cookie│
   └────────┬─────────────┘
            │
            ▼
   ┌──────────────────────┐
   │ AuthenticationSuccess│
   │ Handler              │
   │ Redirect to          │
   │ /dashboard (or admin)│
   └──────────────────────┘
```

### 4.3 Authorization Flow

```
Authenticated user requests protected resource
Example: GET /admin/dashboard
    ↓
Spring Security checks URL in SecurityConfig:
  - @PreAuthorize("hasRole('ADMIN')")
    ↓
Retrieves user's authorities from SecurityContext:
  Example: [ROLE_USER]
    ↓
Checks if ROLE_ADMIN is in user's authorities:
  - NOT PRESENT
    ↓
Access denied
    ↓
ExceptionTranslationFilter catches:
  AccessDeniedException
    ↓
AccessDeniedHandler redirects to:
  /error/403 (Forbidden page)
```

### 4.4 Session Management

```
Successful authentication
    ↓
Spring creates HttpSession
    ↓
SecurityContext stored in session
  Contains:
  - Authentication object
  - Authenticated user details
  - Granted authorities (roles)
    ↓
SessionID cookie sent to browser
    ↓
Browser includes SessionID with every request
    ↓
Spring Security restores SecurityContext from session
    ↓
User remains authenticated until:
  - Session expires (default 30 minutes)
  - User logs out
  - Session invalidated
```

### 4.5 Password Hashing with BCrypt

```
User enters password: "MyPassword123!"
    ↓
BCryptPasswordEncoder.encode(password)
    ↓
BCrypt algorithm:
  - Generates random salt
  - Applies hash function multiple times
  - Creates hash like:
    $2a$10$N9qo8ucomp...
    ↓
Hash stored in database:
  users.password = "$2a$10$N9qo8ucomp..."
    ↓
Never store plain password
    ↓
During login:
  User enters: "MyPassword123!"
    ↓
  PasswordEncoder.matches(rawPassword, hashedPassword)
    ↓
  BCrypt re-applies hash to user input
  Compares with stored hash
  Returns true/false
```

### 4.6 Complete Authentication Checklist

- ✅ **Signup**: Validate input → Hash password → Save user → Assign role
- ✅ **Login**: Load user → Verify password → Create session → Authenticate
- ✅ **Logout**: Invalidate session → Clear authentication → Redirect
- ✅ **Password Hashing**: BCrypt with salt
- ✅ **Session Management**: HttpSession + SecurityContext
- ✅ **Protected Routes**: @PreAuthorize annotations
- ✅ **Role-based Access**: ROLE_USER, ROLE_ADMIN
- ✅ **Forgot Password**: Token-based reset
- ✅ **Change Password**: Current password verification
- ✅ **Authentication Failure**: Friendly error messages
- ✅ **Access Denied**: 403 Forbidden page

---

## 5. Database Architecture

### 5.1 Database Schema

#### users table

| Column | Type | Constraint | Description |
|--------|------|-----------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| name | VARCHAR(100) | NOT NULL | User's full name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | User's email address |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| enabled | BOOLEAN | DEFAULT true | Account active/inactive |
| created_at | TIMESTAMP | DEFAULT NOW() | Account creation time |
| updated_at | TIMESTAMP | DEFAULT NOW() | Last update time |
| last_login | TIMESTAMP | NULLABLE | Last login timestamp |

**SQL**:
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);
```

#### roles table

| Column | Type | Constraint | Description |
|--------|------|-----------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Unique role identifier |
| name | VARCHAR(50) | NOT NULL, UNIQUE | Role name (USER, ADMIN) |

**SQL**:
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
```

#### user_roles table (Join table)

| Column | Type | Constraint | Description |
|--------|------|-----------|-------------|
| user_id | BIGINT | PRIMARY KEY, FOREIGN KEY | Reference to users table |
| role_id | BIGINT | PRIMARY KEY, FOREIGN KEY | Reference to roles table |

**SQL**:
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

#### password_tokens table

| Column | Type | Constraint | Description |
|--------|------|-----------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Token identifier |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | Reference to users table |
| token | VARCHAR(255) | NOT NULL, UNIQUE | Reset token |
| expiry_date | TIMESTAMP | NOT NULL | Token expiration time |
| created_at | TIMESTAMP | DEFAULT NOW() | Creation timestamp |

**SQL**:
```sql
CREATE TABLE password_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 5.2 Entity Relationship Diagram

```
┌─────────────────────┐
│      users          │
├─────────────────────┤
│ id (PK)             │
│ name                │
│ email (UNIQUE)      │
│ password            │
│ enabled             │
│ created_at          │
│ updated_at          │
│ last_login          │
└──────────┬──────────┘
           │
           │ ONE-TO-MANY
           │
           ▼
┌──────────────────────┐     MANY-TO-MANY      ┌──────────────────┐
│   user_roles         │◄────────────────────►│     roles        │
├──────────────────────┤                       ├──────────────────┤
│ user_id (PK, FK)     │                       │ id (PK)          │
│ role_id (PK, FK)     │                       │ name (UNIQUE)    │
└──────────────────────┘                       └──────────────────┘
           ▲
           │
           │ ONE-TO-MANY
           │
           └──────────────────┐
                              │
                    ┌─────────▼──────────────┐
                    │  password_tokens       │
                    ├───────────────────────┤
                    │ id (PK)               │
                    │ user_id (FK)          │
                    │ token (UNIQUE)        │
                    │ expiry_date           │
                    │ created_at            │
                    └───────────────────────┘
```

### 5.3 Relationships Explained

**users ↔ user_roles ↔ roles**
- One user can have multiple roles (One-to-Many through join table)
- One role can belong to many users (Many-to-One through join table)
- Example: User #1 has [ROLE_USER, ROLE_ADMIN]
- Example: ROLE_USER is assigned to Users #1, #2, #3, etc.

**users ↔ password_tokens**
- One user can have multiple password reset tokens
- Each token is associated with one user
- Tokens are temporary (expire after 15 minutes)
- Token deleted after successful password reset

### 5.4 Indexes for Performance

```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_password_tokens_user_id ON password_tokens(user_id);
CREATE INDEX idx_password_tokens_token ON password_tokens(token);
```

---

## 6. Spring Boot Package Architecture

### 6.1 Project Structure

```
medflow-auth-service/
│
├── pom.xml                                  # Maven configuration
├── README.md                                # Project documentation
├── ARCHITECTURE.md                          # This document
│
└── src/
    ├── main/
    │   ├── java/com/medflow/medflowauthservice/
    │   │   ├── MedflowAuthServiceApplication.java    # Spring Boot main class
    │   │   │
    │   │   ├── controller/                           # Spring MVC Controllers
    │   │   │   ├── HomeController.java
    │   │   │   ├── AuthController.java
    │   │   │   ├── UserController.java
    │   │   │   ├── AdminController.java
    │   │   │   └── ErrorController.java
    │   │   │
    │   │   ├── service/                              # Business Logic
    │   │   │   ├── AuthService.java
    │   │   │   ├── UserService.java
    │   │   │   ├── AdminService.java
    │   │   │   ├── EmailService.java
    │   │   │   └── ValidationService.java
    │   │   │
    │   │   ├── repository/                           # Data Access Layer
    │   │   │   ├── UserRepository.java
    │   │   │   └── RoleRepository.java
    │   │   │
    │   │   ├── entity/                               # JPA Entities
    │   │   │   ├── User.java
    │   │   │   ├── Role.java
    │   │   │   └── PasswordResetToken.java
    │   │   │
    │   │   ├── dto/                                  # Data Transfer Objects
    │   │   │   ├── SignupRequest.java
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── UserResponse.java
    │   │   │   ├── UserProfileUpdateRequest.java
    │   │   │   ├── PasswordChangeRequest.java
    │   │   │   └── PasswordResetRequest.java
    │   │   │
    │   │   ├── security/                             # Spring Security
    │   │   │   ├── SecurityConfig.java
    │   │   │   ├── CustomUserDetailsService.java
    │   │   │   └── AuthenticationFailureHandler.java
    │   │   │
    │   │   ├── config/                               # Application Configuration
    │   │   │   ├── AppConfig.java
    │   │   │   └── WebConfig.java
    │   │   │
    │   │   ├── exception/                            # Custom Exceptions
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── DuplicateResourceException.java
    │   │   │   ├── InvalidTokenException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   │
    │   │   └── validation/                           # Validation
    │   │       └── CustomValidators.java
    │   │
    │   └── resources/
    │       ├── application.properties               # Configuration
    │       ├── application-dev.properties           # Development config
    │       ├── application-prod.properties          # Production config
    │       │
    │       ├── templates/
    │       │   ├── fragments/
    │       │   │   ├── navbar.html
    │       │   │   ├── footer.html
    │       │   │   ├── sidebar.html
    │       │   │   └── alerts.html
    │       │   │
    │       │   ├── auth/
    │       │   │   ├── login.html
    │       │   │   ├── signup.html
    │       │   │   ├── forgot-password.html
    │       │   │   └── reset-password.html
    │       │   │
    │       │   ├── user/
    │       │   │   ├── dashboard.html
    │       │   │   ├── profile.html
    │       │   │   ├── edit-profile.html
    │       │   │   ├── settings.html
    │       │   │   └── change-password.html
    │       │   │
    │       │   ├── admin/
    │       │   │   ├── dashboard.html
    │       │   │   └── users/
    │       │   │       ├── list.html
    │       │   │       ├── detail.html
    │       │   │       └── edit.html
    │       │   │
    │       │   ├── errors/
    │       │   │   ├── 403.html
    │       │   │   └── 404.html
    │       │   │
    │       │   ├── home.html
    │       │   ├── about.html
    │       │   └── contact.html
    │       │
    │       ├── static/
    │       │   ├── css/
    │       │   │   ├── bootstrap.min.css
    │       │   │   ├── style.css
    │       │   │   └── theme.css
    │       │   │
    │       │   ├── js/
    │       │   │   ├── bootstrap.bundle.min.js
    │       │   │   ├── validation.js
    │       │   │   └── app.js
    │       │   │
    │       │   └── images/
    │       │       └── logo.png
    │       │
    │       └── application.properties
    │
    └── test/
        └── java/com/medflow/medflowauthservice/
            ├── MedflowAuthServiceApplicationTests.java
            ├── controller/
            │   ├── HomeControllerTests.java
            │   ├── AuthControllerTests.java
            │   └── UserControllerTests.java
            ├── service/
            │   ├── AuthServiceTests.java
            │   └── UserServiceTests.java
            └── repository/
                └── UserRepositoryTests.java
```

### 6.2 Package Responsibilities

**controller/**
- Handles HTTP requests and responses
- No business logic here
- Calls service methods
- Sets up Model attributes for templates
- Redirects or renders views
- Validates user input
- Returns appropriate HTTP status codes

**service/**
- Contains all business logic
- Validates data before database operations
- Calls repository methods
- Performs transformations
- Handles transactions
- Coordinates between multiple repositories
- Throws meaningful exceptions

**repository/**
- Data access abstraction using Spring Data JPA
- Database queries using method naming conventions
- Automatic CRUD operations
- Custom queries using @Query
- No business logic here

**entity/**
- JPA entities representing database tables
- Uses @Entity, @Table, @Column annotations
- Defines relationships (@OneToMany, @ManyToMany)
- Getters and setters
- Constructor methods

**dto/**
- Data Transfer Objects for requests/responses
- Not directly mapped to database
- Validation annotations
- Used for API/form binding
- Cleanly separate API contracts from entities

**security/**
- Spring Security configuration
- Authentication setup
- Authorization rules
- Custom UserDetailsService
- Password encoding

**config/**
- Application-wide configuration
- Bean definitions
- Database connection setup
- Message encoder configuration
- Web MVC setup

**exception/**
- Custom exception classes
- Global exception handler using @ControllerAdvice
- Error response formatting
- HTTP status code mapping

**validation/**
- Custom validation logic
- Input validation rules
- Cross-field validation
- Reusable validators

---

## 7. Important Classes Required

### 7.1 Controllers

**HomeController**
- Purpose: Handle public pages
- Methods: home(), about(), contact()
- Routes: /, /about, /contact

**AuthController**
- Purpose: Handle authentication flows
- Methods: 
  - showLoginForm(), processLogin() (handled by Spring Security)
  - showSignupForm(), registerUser()
  - showForgotPasswordForm(), processForgotPassword()
  - showResetPasswordForm(token), resetPassword()
  - logout()
- Routes: /login, /signup, /forgot-password, /reset-password, /logout

**UserController**
- Purpose: Handle authenticated user pages
- Methods: 
  - dashboard(), viewProfile(), 
  - showEditProfileForm(), updateProfile()
  - showSettings(), updateSettings()
  - showChangePasswordForm(), changePassword()
- Routes: /dashboard, /profile, /profile/edit, /settings, /change-password
- Security: @PreAuthorize("isAuthenticated()")

**AdminController**
- Purpose: Handle admin pages
- Methods:
  - dashboard(), listUsers(), viewUser()
  - showEditUserForm(), updateUser()
  - toggleUserStatus(), deleteUser()
- Routes: /admin/dashboard, /admin/users, /admin/users/{id}, etc.
- Security: @PreAuthorize("hasRole('ADMIN')")

**ErrorController**
- Purpose: Handle error pages
- Methods: handle403(), handle404(), handle500()
- Routes: /error (Spring Boot default)

### 7.2 Services

**AuthService**
- Purpose: Authentication business logic
- Methods:
  - registerUser(SignupRequest)
  - validateUserInput(SignupRequest)
  - createPasswordResetToken(String email)
  - validateResetToken(String token)
  - resetPassword(String token, String newPassword)
  - getUserByEmail(String email)

**UserService**
- Purpose: User operations
- Methods:
  - getUserProfile(Long userId)
  - updateProfile(Long userId, UserProfileUpdateRequest)
  - changePassword(Long userId, PasswordChangeRequest)
  - getUserStats(Long userId)
  - getRecentActivity(Long userId)

**AdminService**
- Purpose: Admin operations
- Methods:
  - getAllUsers(Pageable)
  - getUserDetails(Long userId)
  - updateUser(Long userId, UserAdminUpdateRequest)
  - toggleUserStatus(Long userId)
  - deleteUser(Long userId)
  - getSystemStats()
  - getRecentRegistrations()

**EmailService**
- Purpose: Email operations
- Methods:
  - sendWelcomeEmail(User)
  - sendResetEmail(User, String token)
  - sendContactMessage(ContactRequest)

**ValidationService**
- Purpose: Custom validation logic
- Methods:
  - validateEmail(String email)
  - validatePassword(String password)
  - isEmailTaken(String email)
  - validatePasswordMatch(String pwd1, String pwd2)

### 7.3 Repositories

**UserRepository extends JpaRepository<User, Long>**
- findByEmail(String email)
- existsByEmail(String email)
- findAll(Pageable pageable)

**RoleRepository extends JpaRepository<Role, Long>**
- findByName(String name)

**PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long>**
- findByToken(String token)
- findByUserId(Long userId)

### 7.4 Entities

**User**
- Fields: id, name, email, password, enabled, createdAt, updatedAt, lastLogin
- Relationships: @ManyToMany roles
- Annotations: @Entity, @Table, @Column, @GeneratedValue

**Role**
- Fields: id, name
- Relationships: @ManyToMany users
- Annotations: @Entity, @Table

**PasswordResetToken**
- Fields: id, user, token, expiryDate, createdAt
- Relationships: @ManyToOne user
- Annotations: @Entity, @Table

### 7.5 DTOs

**SignupRequest**
- Fields: name, email, password, confirmPassword
- Validation: @NotBlank, @Email, @Size, etc.

**LoginRequest**
- Fields: email, password
- Validation: @NotBlank, @Email

**UserResponse**
- Fields: id, name, email, roles, createdAt
- Purpose: API/template response

**UserProfileUpdateRequest**
- Fields: name, email
- Validation: @NotBlank, @Email

**PasswordChangeRequest**
- Fields: currentPassword, newPassword, confirmPassword
- Validation: Required fields, match check

**PasswordResetRequest**
- Fields: token, newPassword, confirmPassword
- Validation: Required fields, match check

### 7.6 Security Classes

**SecurityConfig**
- Purpose: Spring Security configuration
- Methods:
  - securityFilterChain(HttpSecurity http)
  - passwordEncoder()
  - userDetailsService()
  - authenticationProvider()
- Configures: Login page, logout, CSRF, permissions

**CustomUserDetailsService implements UserDetailsService**
- Purpose: Load user from database
- Methods: loadUserByUsername(String email)
- Returns: UserDetails with authorities

**AuthenticationFailureHandler**
- Purpose: Handle login failures
- Redirects to login with error message

### 7.7 Exception Classes

**ResourceNotFoundException extends RuntimeException**
- Thrown when resource not found in database
- Returns 404 status

**DuplicateResourceException extends RuntimeException**
- Thrown when duplicate email in signup
- Returns 400 status

**InvalidTokenException extends RuntimeException**
- Thrown when password reset token invalid/expired
- Returns 400 status

**GlobalExceptionHandler with @ControllerAdvice**
- Handles all exceptions
- Formats error responses
- Returns appropriate HTTP status codes

---

## 8. Frontend Architecture

### 8.1 Template Structure

**Thymeleaf Fragments** (Reusable components)

`fragments/navbar.html`
- Navigation bar
- User menu (Profile, Settings, Logout)
- Admin link (if ADMIN role)
- Logo and branding

`fragments/footer.html`
- Copyright
- Links

`fragments/sidebar.html`
- User dashboard menu
- Admin menu (if applicable)
- Navigation links

`fragments/alerts.html`
- Success messages
- Error messages
- Warning messages
- Bootstrap alerts

`fragments/layout.html`
- Master layout template
- Includes navbar, footer, scripts
- Navigation structure

### 8.2 Page Templates

**Authentication Pages**

`auth/login.html`
- Email input
- Password input
- "Remember me" checkbox
- Submit button
- "Forgot password?" link
- "Sign up" link
- Error display

`auth/signup.html`
- Name input
- Email input
- Password input
- Confirm password input
- Submit button
- Login link
- Validation error display

`auth/forgot-password.html`
- Email input
- Submit button
- Back to login link

`auth/reset-password.html`
- New password input
- Confirm password input
- Submit button

**User Pages**

`user/dashboard.html`
- Welcome greeting
- User statistics
- Quick links to profile, settings
- Recent activity (if applicable)
- Logout button

`user/profile.html`
- User information display
- Edit profile link
- Change password link
- Delete account link

`user/edit-profile.html`
- Name input (pre-filled)
- Email input (pre-filled)
- Submit button
- Cancel button

`user/settings.html`
- Notification preferences
- Privacy settings
- Theme selection
- Save button

`user/change-password.html`
- Current password input
- New password input
- Confirm password input
- Submit button
- Validation error display

**Admin Pages**

`admin/dashboard.html`
- System statistics
- Recent registrations
- User count
- Active users
- Links to user management

`admin/users/list.html`
- User table with pagination
- Search/filter functionality
- Name, email, role, status columns
- Action buttons (View, Edit, Deactivate, Delete)
- Bulk actions (if needed)

`admin/users/detail.html`
- User full information
- Role assignment
- Account status
- Creation date
- Last login
- Edit, deactivate, delete buttons

`admin/users/edit.html`
- User form (name, email, role, status)
- Submit and cancel buttons
- Validation error display

**Public Pages**

`home.html`
- Welcome message
- Feature highlights
- Call to action (Sign up / Login)
- Links to About, Contact

`about.html`
- Company/project information
- Features
- Team info

`contact.html`
- Contact form (name, email, message)
- Submit button

**Error Pages**

`errors/403.html`
- "Access Denied" message
- Back to dashboard link

`errors/404.html`
- "Page Not Found" message
- Home link

### 8.3 Static Resources

**CSS**
- Bootstrap 5 for responsive design
- Custom stylesheet for branding
- Theme colors
- Custom form styling

**JavaScript**
- Bootstrap bundle (popovers, tooltips)
- Form validation
- Confirmation dialogs
- User interaction enhancements

**Images**
- Logo
- Icons
- Background images

### 8.4 Thymeleaf Features Used

```html
<!-- Variable substitution -->
<p th:text="${variable}"></p>

<!-- Conditionals -->
<div th:if="${isAdmin}">Admin section</div>

<!-- Loops -->
<tr th:each="user : ${users}">

<!-- Form binding -->
<form th:action="@{/submit}" th:object="${formObject}">

<!-- Security expressions -->
<div th:if="${#authentication.name}">Logged in</div>

<!-- Fragments -->
<div th:replace="fragments/navbar :: navbar"></div>

<!-- Links -->
<a th:href="@{/profile}">Profile</a>

<!-- URL parameters -->
<a th:href="@{/reset-password(token=${resetToken})}">Reset</a>
```

---

## 9. URL Architecture (Complete Route Table)

### 9.1 Public Routes (No authentication required)

| HTTP | URL | Purpose | Template | Security |
|------|-----|---------|----------|----------|
| GET | / | Home page | home.html | PUBLIC |
| GET | /about | About page | about.html | PUBLIC |
| GET | /contact | Contact page | contact.html | PUBLIC |
| POST | /contact | Submit contact form | - | PUBLIC |
| GET | /login | Login form | auth/login.html | PUBLIC |
| POST | /login | Process login | - | Spring Security |
| GET | /signup | Signup form | auth/signup.html | PUBLIC |
| POST | /signup | Process registration | - | PUBLIC |
| GET | /forgot-password | Forgot password form | auth/forgot-password.html | PUBLIC |
| POST | /forgot-password | Send reset email | - | PUBLIC |
| GET | /reset-password | Reset password form | auth/reset-password.html | PUBLIC |
| POST | /reset-password | Process password reset | - | PUBLIC |

### 9.2 Authenticated User Routes (Requires authentication)

| HTTP | URL | Purpose | Template | Security |
|------|-----|---------|----------|----------|
| GET | /dashboard | User dashboard | user/dashboard.html | isAuthenticated() |
| GET | /profile | View profile | user/profile.html | isAuthenticated() |
| GET | /profile/edit | Edit profile form | user/edit-profile.html | isAuthenticated() |
| POST | /profile/update | Update profile | - | isAuthenticated() |
| GET | /settings | Settings page | user/settings.html | isAuthenticated() |
| POST | /settings/update | Update settings | - | isAuthenticated() |
| GET | /change-password | Change password form | user/change-password.html | isAuthenticated() |
| POST | /change-password | Process password change | - | isAuthenticated() |
| POST | /logout | Logout | - | isAuthenticated() |

### 9.3 Admin Routes (Requires ADMIN role)

| HTTP | URL | Purpose | Template | Security |
|------|-----|---------|----------|----------|
| GET | /admin/dashboard | Admin dashboard | admin/dashboard.html | hasRole('ADMIN') |
| GET | /admin/users | User list | admin/users/list.html | hasRole('ADMIN') |
| GET | /admin/users/{id} | User details | admin/users/detail.html | hasRole('ADMIN') |
| GET | /admin/users/{id}/edit | Edit user form | admin/users/edit.html | hasRole('ADMIN') |
| POST | /admin/users/{id}/update | Update user | - | hasRole('ADMIN') |
| POST | /admin/users/{id}/toggle-status | Toggle user status | - | hasRole('ADMIN') |
| POST | /admin/users/{id}/delete | Delete user | - | hasRole('ADMIN') |

### 9.4 Error Routes

| HTTP | URL | Purpose | Template | Status |
|------|-----|---------|----------|--------|
| - | /error/403 | Access denied | errors/403.html | 403 |
| - | /error/404 | Not found | errors/404.html | 404 |
| - | /error/500 | Server error | errors/500.html | 500 |

---

## 10. REST API Architecture

While this application primarily uses server-side rendering with Thymeleaf, we can add REST endpoints for:

1. **AJAX calls from JavaScript**
2. **Future mobile app integration**
3. **Third-party integrations**

### 10.1 REST Endpoints

#### User Authentication APIs

**POST /api/auth/signup**
```
Request:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}

Response (201 Created):
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "roles": ["USER"],
  "createdAt": "2026-08-24T10:30:00"
}

Errors:
- 400: Validation failed (duplicate email, weak password)
- 500: Internal server error
```

**POST /api/auth/login**
```
Request:
{
  "email": "john@example.com",
  "password": "SecurePass123"
}

Response (200 OK):
{
  "token": "JWT_TOKEN_HERE",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "roles": ["USER"]
  }
}

Errors:
- 401: Invalid credentials
- 400: Bad request
```

**POST /api/auth/logout**
```
Request: No body

Response (200 OK):
{
  "message": "Logged out successfully"
}
```

#### User Profile APIs

**GET /api/users/profile**
```
Request: Bearer {token}
Response (200 OK):
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2026-08-24T10:30:00",
  "updatedAt": "2026-08-24T11:00:00"
}

Errors:
- 401: Unauthorized
- 404: User not found
```

**PUT /api/users/profile**
```
Request:
{
  "name": "Jane Doe",
  "email": "jane@example.com"
}

Response (200 OK):
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane@example.com",
  "updatedAt": "2026-08-24T12:00:00"
}

Errors:
- 400: Validation failed
- 409: Email already exists
```

#### Admin APIs

**GET /api/admin/users**
```
Request: Bearer {admin_token}
Query params: ?page=0&size=20

Response (200 OK):
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "roles": ["USER"],
      "enabled": true,
      "createdAt": "2026-08-24T10:30:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 3,
  "currentPage": 0
}

Errors:
- 403: Forbidden (not admin)
```

---

## 11. Validation Architecture

### 11.1 Frontend Validation (Browser)

**HTML5 Attributes**
```html
<input type="email" required>
<input type="password" minlength="8" required>
<input type="text" pattern="[A-Za-z ]+" required>
```

**JavaScript Validation**
```javascript
- Check form fields before submission
- Validate password match
- Validate email format
- Show real-time validation feedback
```

### 11.2 Backend Validation (Spring)

**Using Jakarta Bean Validation**

```java
public class SignupRequest {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "Password must contain uppercase, lowercase, and numbers")
    private String password;
}
```

### 11.3 Validation Rules

| Field | Rules | Messages |
|-------|-------|----------|
| Name | Not empty, 2-100 chars | "Name is required", "Name must be 2-100 characters" |
| Email | Valid format, unique | "Invalid email", "Email already registered" |
| Password | Min 8 chars, uppercase, lowercase, numbers | "Password must be 8+ chars with uppercase, lowercase, numbers" |
| Confirm Password | Must match password | "Passwords do not match" |

---

## 12. Error Handling Architecture

### 12.1 Centralized Exception Handling

**GlobalExceptionHandler.java**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(...));
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(...));
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(400).body(new ErrorResponse(...));
    }
}
```

### 12.2 HTTP Status Codes & Error Pages

| Status | Scenario | Action |
|--------|----------|--------|
| 400 | Bad Request (validation failure) | Show form with error messages |
| 401 | Unauthorized (not logged in) | Redirect to login |
| 403 | Forbidden (insufficient permissions) | Show 403 error page |
| 404 | Not Found | Show 404 error page |
| 409 | Conflict (duplicate email) | Show form with error message |
| 500 | Internal Server Error | Show generic error page, log details |

### 12.3 User-Friendly Error Messages

```
❌ DO NOT show:
"SQLException: Connection pool exhausted"
"NullPointerException at line 243"

✅ DO show:
"An error occurred. Please try again later."
"Email already registered. Please use a different email."
"Invalid reset link. Please request a new password reset."
```

---

## 13. Security Architecture

### 13.1 Authentication Security

- ✅ **Passwords**: BCrypt hashing with salt (never plain text)
- ✅ **Session Management**: HttpSession with timeout
- ✅ **Login Protection**: Rate limiting (optional)
- ✅ **Logout**: Complete session invalidation
- ✅ **Password Reset**: Token-based with expiration

### 13.2 Authorization Security

- ✅ **ROLE_USER**: Default authenticated user role
- ✅ **ROLE_ADMIN**: Administrative privileges
- ✅ **@PreAuthorize Annotations**: Method-level security
- ✅ **Protected Routes**: URL pattern-based access control

### 13.3 CSRF Protection

- ✅ **Enabled by Default**: Spring Security automatic CSRF tokens
- ✅ **Form Submissions**: Hidden CSRF token in forms
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
```

### 13.4 Input Validation & XSS Prevention

- ✅ **Server-side Validation**: All inputs validated
- ✅ **Thymeleaf Auto-Escaping**: Prevents XSS attacks
- ✅ **JPA Parameterized Queries**: Prevents SQL injection

### 13.5 Sensitive Data Handling

- ✅ **No Hardcoded Credentials**: Use environment variables
- ✅ **Database Password**: Environment variable
- ✅ **Email Service Password**: Environment variable
- ✅ **JWT Secret** (if used): Environment variable
- ✅ **HTTPS Only**: In production

### 13.6 Security Best Practices

```properties
# application.properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

# Never commit secrets to git
# Use: export DB_URL="jdbc:postgresql://localhost/medflow"
```

---

## 14. PostgreSQL Configuration

### 14.1 Database Connection Configuration

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/medflow
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Connection pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 14.2 PostgreSQL Server Configuration

- **Host**: localhost
- **Port**: 5432
- **Database**: medflow
- **User**: medflow_user (create separate user for application)
- **Password**: Secure password stored in environment

### 14.3 Database Creation Script

```sql
-- Create database
CREATE DATABASE medflow;

-- Create user
CREATE USER medflow_user WITH PASSWORD 'secure_password_here';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE medflow TO medflow_user;
```

---

## 15. Maven Dependencies (pom.xml)

### 15.1 Core Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| spring-boot-starter-web | 3.x | Web framework, Spring MVC |
| spring-boot-starter-security | 3.x | Authentication & authorization |
| spring-boot-starter-data-jpa | 3.x | ORM using Hibernate |
| spring-boot-starter-validation | 3.x | Jakarta Bean Validation |
| spring-boot-starter-thymeleaf | 3.x | Server-side templating |
| thymeleaf-spring6 | 3.x | Thymeleaf Spring Integration |
| thymeleaf-extras-springsecurity6 | 3.x | Thymeleaf Security tags |
| postgresql | Latest | PostgreSQL JDBC driver |
| spring-boot-devtools | 3.x | Development tools (hot reload) |

### 15.2 Testing Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| spring-boot-starter-test | 3.x | JUnit, Mockito, AssertJ |

### 15.3 Optional Dependencies (Not needed initially)

| Dependency | Purpose |
|------------|---------|
| spring-boot-starter-mail | Email sending (optional) |
| jwt-io | JWT tokens (if REST APIs later) |
| swagger-ui | API documentation (if REST APIs later) |

### 15.4 Dependency Management

```xml
<!-- Using Spring Boot BOM for version management -->
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>3.2.x</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

## 16. Development Phases

### Phase 1: Architecture & Planning ✓
- Define complete system architecture
- Plan database schema
- Identify all required classes
- Plan page structure and flows
- **Deliverable**: This architecture document

### Phase 2: Project Setup
- Create Spring Boot project using Maven
- Configure Maven dependencies
- Set up PostgreSQL database
- Configure application.properties
- Configure Spring Security basics
- **Deliverable**: Runnable Spring Boot application

### Phase 3: Database & Entities
- Create PostgreSQL database and tables
- Create JPA entities (User, Role, PasswordResetToken)
- Configure entity relationships
- Create repositories
- **Deliverable**: Working database with entities

### Phase 4: Security Configuration
- Configure SecurityConfig
- Implement CustomUserDetailsService
- Set up BCryptPasswordEncoder
- Configure login/logout flow
- Configure CSRF protection
- **Deliverable**: Basic authentication working

### Phase 5: Authentication Implementation
- Implement signup functionality
- Implement login functionality
- Implement logout functionality
- Implement password validation
- Implement email validation
- Implement duplicate email checking
- **Deliverable**: Users can register and login

### Phase 6: Services & Business Logic
- Implement AuthService
- Implement UserService
- Implement AdminService
- Implement ValidationService
- Implement password hashing
- **Deliverable**: Business logic working

### Phase 7: Controllers & Routes
- Implement HomeController
- Implement AuthController
- Implement UserController
- Implement AdminController
- Implement ErrorController
- Configure URL mappings
- **Deliverable**: All routes defined

### Phase 8: Frontend - Public Pages
- Create base layout with fragments
- Implement home.html
- Implement about.html
- Implement contact.html
- Implement navbar and footer fragments
- Add Bootstrap 5 styling
- **Deliverable**: Public pages rendered

### Phase 9: Frontend - Auth Pages
- Implement login.html
- Implement signup.html
- Implement forgot-password.html
- Implement reset-password.html
- Add form validation UI
- Add error message display
- **Deliverable**: Authentication pages working

### Phase 10: Frontend - User Pages
- Implement dashboard.html
- Implement profile.html
- Implement edit-profile.html
- Implement settings.html
- Implement change-password.html
- Add user menu to navbar
- **Deliverable**: User pages accessible to authenticated users

### Phase 11: Frontend - Admin Pages
- Implement admin dashboard
- Implement user list page
- Implement user detail page
- Implement user edit page
- Implement admin navigation
- **Deliverable**: Admin pages accessible to admins

### Phase 12: Password Reset Flow
- Implement forgot password email sending
- Implement reset token generation
- Implement reset password processing
- Implement token expiration
- **Deliverable**: Forgot/reset password flow working

### Phase 13: Validation & Error Handling
- Add field validation annotations
- Implement GlobalExceptionHandler
- Implement error pages (403, 404, 500)
- Add form validation messages
- Add user-friendly error displays
- **Deliverable**: Comprehensive error handling

### Phase 14: User Profile Management
- Implement edit profile functionality
- Implement settings management
- Implement change password functionality
- Add validation
- **Deliverable**: Users can manage their profiles

### Phase 15: Admin User Management
- Implement user list with pagination
- Implement user detail view
- Implement user editing
- Implement user deactivation/activation
- Implement user deletion
- **Deliverable**: Admins can manage users

### Phase 16: Testing
- Write unit tests for services
- Write integration tests for controllers
- Write repository tests
- Test authentication flow
- Test authorization enforcement
- **Deliverable**: Comprehensive test coverage

### Phase 17: Refinement & Polish
- Performance optimization
- Code cleanup
- Documentation
- Security review
- User experience improvements
- **Deliverable**: Production-ready application

---

## 17. Django to Spring Boot Comparison

### Concepts Comparison

| Django | Spring Boot | Explanation |
|--------|------------|-------------|
| views.py | @Controller / Controller classes | Handle HTTP requests and responses |
| models.py | @Entity classes | Define database table structure |
| Django ORM | Spring Data JPA + Hibernate | Object-relational mapping to database |
| urls.py | @RequestMapping, @GetMapping | Route HTTP requests to handlers |
| settings.py | application.properties | Configuration management |
| manage.py | Maven (mvn) | Build and run application |
| django.contrib.auth | Spring Security | User authentication and authorization |
| @login_required | @PreAuthorize("isAuthenticated()") | Protect routes requiring login |
| @admin_required | @PreAuthorize("hasRole('ADMIN')") | Protect admin routes |
| forms.py | DTOs (@Data class) | Data input validation and binding |
| Middleware | Filter / @Component | Request/response interceptors |
| signals.py | Events / Listeners | React to data changes |
| Celery tasks | N/A (use @Async if needed) | Background jobs |
| Django templates | Thymeleaf templates | Server-side HTML rendering |

### Request/Response Cycle Comparison

**Django**
```
Browser Request
  ↓
URL Router (urls.py)
  ↓
View Function (views.py)
  ↓
Model Query (models.py ORM)
  ↓
Template Rendering (templates/)
  ↓
Response sent to browser
```

**Spring Boot**
```
Browser Request
  ↓
Spring DispatcherServlet
  ↓
RequestMapping (@Controller)
  ↓
Controller Method
  ↓
Service Method (Business Logic)
  ↓
Repository Method (JPA Query)
  ↓
Database Query
  ↓
Thymeleaf Template Rendering
  ↓
Response sent to browser
```

### Authentication Comparison

**Django**
```python
# urls.py
from django.contrib.auth.views import login_required

# views.py
@login_required
def dashboard(request):
    user = request.user
    return render(request, 'dashboard.html', {'user': user})

# Check admin
if request.user.is_staff:
    # Admin code
```

**Spring Boot**
```java
// SecurityConfig.java - Configure routes
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .authorizeRequests()
        .requestMatchers("/dashboard").authenticated()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .and()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/dashboard");
    return http.build();
}

// Controller.java - Use annotations
@GetMapping("/dashboard")
@PreAuthorize("isAuthenticated()")
public String dashboard(Model model, Principal principal) {
    User user = userService.getUserByEmail(principal.getName());
    model.addAttribute("user", user);
    return "user/dashboard";
}

// Check admin
if (principal != null && authentication.getAuthorities()
    .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
    // Admin code
}
```

### Password Hashing Comparison

**Django**
```python
from django.contrib.auth.hashers import make_password, check_password

# Hashing
hashed = make_password("MyPassword123!")
# Uses PBKDF2 by default

# Verifying
is_valid = check_password("MyPassword123!", hashed)
```

**Spring Boot**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Create encoder
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

// Hashing
String hashed = encoder.encode("MyPassword123!");

// Verifying
boolean isValid = encoder.matches("MyPassword123!", hashed);
```

### Database Query Comparison

**Django ORM**
```python
# Get user by email
user = User.objects.get(email='john@example.com')

# Get all users
users = User.objects.all()

# Filter and paginate
users = User.objects.filter(is_active=True)[0:20]

# Count
count = User.objects.count()
```

**Spring Data JPA**
```java
// Get user by email
User user = userRepository.findByEmail("john@example.com");

// Get all users
List<User> users = userRepository.findAll();

// Filter and paginate
Pageable pageable = PageRequest.of(0, 20);
Page<User> users = userRepository.findAll(pageable);

// Count
long count = userRepository.count();
```

### Template Comparison

**Django Template**
```html
{% extends "base.html" %}

{% block content %}
  <h1>Hello, {{ user.name }}!</h1>
  
  {% if user.is_staff %}
    <a href="/admin">Admin Panel</a>
  {% endif %}
  
  {% for item in items %}
    <p>{{ item.title }}</p>
  {% endfor %}
{% endblock %}
```

**Thymeleaf Template**
```html
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
      
  <body th:replace="fragments/navbar">
    <h1 th:text="'Hello, ' + ${user.name} + '!'"></h1>
    
    <a th:if="${#authentication.principal.authorities.contains('ROLE_ADMIN')}"
       th:href="@{/admin}">Admin Panel</a>
    
    <p th:each="item : ${items}" th:text="${item.title}"></p>
  </body>
</html>
```

### Key Differences to Remember

1. **Routing**: Django uses urls.py file; Spring Boot uses @RequestMapping annotations in classes
2. **Views**: Django functions can handle both GET/POST; Spring Boot uses separate methods or annotations
3. **Models/Entities**: Django auto-creates tables; Spring uses Hibernate with explicit configuration
4. **Settings**: Django uses settings.py file; Spring Boot uses application.properties
5. **Security**: Django decorators; Spring Security uses annotations and configuration
6. **Database Queries**: Django QuerySet is lazy; Spring JPA Repository is similar
7. **Middleware**: Django middleware can't access response body; Spring Filters more flexible
8. **Testing**: Django has built-in test runner; Spring uses JUnit + Mockito

---

## 18. Complete Project Folder Structure

```
medflow-auth-service/
│
├── pom.xml                                  # Maven project config
├── ARCHITECTURE.md                          # This architecture document
├── README.md                                # Project README
│
├── .gitignore                               # Git ignore file
│
├── mvnw                                     # Maven wrapper (Linux/Mac)
├── mvnw.cmd                                 # Maven wrapper (Windows)
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── medflow/
    │   │           └── medflowauthservice/
    │   │               ├── MedflowAuthServiceApplication.java
    │   │               │
    │   │               ├── controller/
    │   │               │   ├── HomeController.java
    │   │               │   ├── AuthController.java
    │   │               │   ├── UserController.java
    │   │               │   ├── AdminController.java
    │   │               │   └── ErrorController.java
    │   │               │
    │   │               ├── service/
    │   │               │   ├── AuthService.java
    │   │               │   ├── AuthServiceImpl.java
    │   │               │   ├── UserService.java
    │   │               │   ├── UserServiceImpl.java
    │   │               │   ├── AdminService.java
    │   │               │   ├── AdminServiceImpl.java
    │   │               │   ├── EmailService.java
    │   │               │   ├── EmailServiceImpl.java
    │   │               │   ├── ValidationService.java
    │   │               │   └── ValidationServiceImpl.java
    │   │               │
    │   │               ├── repository/
    │   │               │   ├── UserRepository.java
    │   │               │   ├── RoleRepository.java
    │   │               │   └── PasswordTokenRepository.java
    │   │               │
    │   │               ├── entity/
    │   │               │   ├── User.java
    │   │               │   ├── Role.java
    │   │               │   └── PasswordResetToken.java
    │   │               │
    │   │               ├── dto/
    │   │               │   ├── SignupRequest.java
    │   │               │   ├── LoginRequest.java
    │   │               │   ├── UserResponse.java
    │   │               │   ├── UserProfileUpdateRequest.java
    │   │               │   ├── PasswordChangeRequest.java
    │   │               │   ├── PasswordResetRequest.java
    │   │               │   ├── ContactRequest.java
    │   │               │   └── ErrorResponse.java
    │   │               │
    │   │               ├── security/
    │   │               │   ├── SecurityConfig.java
    │   │               │   ├── CustomUserDetailsService.java
    │   │               │   └── AuthenticationFailureHandler.java
    │   │               │
    │   │               ├── config/
    │   │               │   ├── AppConfig.java
    │   │               │   └── WebConfig.java
    │   │               │
    │   │               ├── exception/
    │   │               │   ├── ResourceNotFoundException.java
    │   │               │   ├── DuplicateResourceException.java
    │   │               │   ├── InvalidTokenException.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   └── ErrorResponse.java
    │   │               │
    │   │               └── validation/
    │   │                   └── CustomValidators.java
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── application-dev.properties
    │       ├── application-prod.properties
    │       │
    │       ├── templates/
    │       │   ├── fragments/
    │       │   │   ├── navbar.html
    │       │   │   ├── footer.html
    │       │   │   ├── sidebar.html
    │       │   │   ├── alerts.html
    │       │   │   └── layout.html
    │       │   │
    │       │   ├── auth/
    │       │   │   ├── login.html
    │       │   │   ├── signup.html
    │       │   │   ├── forgot-password.html
    │       │   │   └── reset-password.html
    │       │   │
    │       │   ├── user/
    │       │   │   ├── dashboard.html
    │       │   │   ├── profile.html
    │       │   │   ├── edit-profile.html
    │       │   │   ├── settings.html
    │       │   │   └── change-password.html
    │       │   │
    │       │   ├── admin/
    │       │   │   ├── dashboard.html
    │       │   │   └── users/
    │       │   │       ├── list.html
    │       │   │       ├── detail.html
    │       │   │       └── edit.html
    │       │   │
    │       │   ├── errors/
    │       │   │   ├── 403.html
    │       │   │   ├── 404.html
    │       │   │   └── 500.html
    │       │   │
    │       │   ├── home.html
    │       │   ├── about.html
    │       │   └── contact.html
    │       │
    │       ├── static/
    │       │   ├── css/
    │       │   │   ├── bootstrap.min.css
    │       │   │   ├── style.css
    │       │   │   └── theme.css
    │       │   │
    │       │   ├── js/
    │       │   │   ├── bootstrap.bundle.min.js
    │       │   │   ├── validation.js
    │       │   │   └── app.js
    │       │   │
    │       │   └── images/
    │       │       └── logo.png
    │       │
    │       └── application.properties
    │
    └── test/
        └── java/
            └── com/
                └── medflow/
                    └── medflowauthservice/
                        ├── MedflowAuthServiceApplicationTests.java
                        ├── controller/
                        │   ├── HomeControllerTests.java
                        │   ├── AuthControllerTests.java
                        │   ├── UserControllerTests.java
                        │   └── AdminControllerTests.java
                        ├── service/
                        │   ├── AuthServiceTests.java
                        │   ├── UserServiceTests.java
                        │   └── AdminServiceTests.java
                        └── repository/
                            └── UserRepositoryTests.java
```

---

## 19. Complete Development Checklist

### Pre-Implementation Checklist
- ✅ Read this entire architecture document
- ✅ Understand all components and their responsibilities
- ✅ Understand user flows and authentication flow
- ✅ Understand database schema and relationships
- ✅ Understand folder structure and naming conventions
- ✅ Understand Spring Boot vs Django concepts

### Phase Checklist (To be completed in each phase)

#### Phase 2: Project Setup
- [ ] Create Spring Boot project
- [ ] Add Maven dependencies
- [ ] Configure pom.xml
- [ ] Test project build with `mvn clean install`

#### Phase 3: Database Setup
- [ ] Install PostgreSQL 18.6
- [ ] Create medflow database
- [ ] Create medflow_user
- [ ] Configure database connection
- [ ] Run Hibernate DDL to create tables

#### Phase 4: Entities & Repositories
- [ ] Create User entity
- [ ] Create Role entity
- [ ] Create PasswordResetToken entity
- [ ] Create UserRepository
- [ ] Create RoleRepository
- [ ] Create PasswordTokenRepository

#### Phase 5: Security Configuration
- [ ] Create SecurityConfig class
- [ ] Create CustomUserDetailsService
- [ ] Configure BCryptPasswordEncoder
- [ ] Configure login page
- [ ] Configure logout
- [ ] Test basic authentication

#### Phase 6: Authentication Services
- [ ] Implement AuthService
- [ ] Implement password validation
- [ ] Implement email validation
- [ ] Implement duplicate email check
- [ ] Implement password hashing
- [ ] Test service methods

#### Phase 7: Controllers & Routes
- [ ] Implement all controllers
- [ ] Map all routes
- [ ] Configure protected routes
- [ ] Configure error handling
- [ ] Test routing

#### Phase 8-11: Frontend Templates
- [ ] Create layout fragments
- [ ] Create all public pages
- [ ] Create auth pages
- [ ] Create user pages
- [ ] Create admin pages
- [ ] Style with Bootstrap 5

#### Phase 12-15: Features
- [ ] Implement password reset
- [ ] Implement profile management
- [ ] Implement admin user management
- [ ] Add validation
- [ ] Add error handling

#### Phase 16-17: Testing & Polish
- [ ] Write unit tests
- [ ] Write integration tests
- [ ] Perform security testing
- [ ] Performance optimization
- [ ] Code review and cleanup

---

## 20. Important Implementation Notes

### Note 1: Never Start with Code
This architecture document is intentionally detailed to prevent random coding. Follow it step-by-step.

### Note 2: Layer Separation is Critical
```
❌ DON'T: Put SQL queries in Controller
❌ DON'T: Put business logic in Repository
❌ DON'T: Put authentication in Service

✅ DO: Controller → Service → Repository → Database
```

### Note 3: Spring Security is Complex
- Take time to understand `SecurityContext`, `Authentication`, `UserDetails`
- Study how `SecurityFilterChain` works
- Understand role-based access control
- Test authentication thoroughly before moving forward

### Note 4: Database Schema First
- Design schema carefully before creating entities
- Understand relationships (1-to-Many, Many-to-Many)
- Plan for scalability
- Add indexes for performance

### Note 5: Frontend Validation & Backend Validation
Both are necessary:
- Frontend: Improve user experience
- Backend: Prevent security breaches

### Note 6: Error Messages Must Be User-Friendly
- Don't expose stack traces
- Don't expose database structure
- Use simple, clear language
- Provide actionable guidance

### Note 7: Environment Variables for Secrets
Never commit passwords or API keys:
```properties
# NEVER do this:
spring.datasource.password=mySecretPassword

# DO this:
spring.datasource.password=${DB_PASSWORD}

# Then set as environment variable:
export DB_PASSWORD=mySecretPassword
```

### Note 8: Test as You Go
Don't wait until the end to test:
- Test each entity as created
- Test each service method
- Test each controller endpoint
- Test authentication flow
- Test authorization enforcement

### Note 9: Git Ignore Secrets
Add to `.gitignore`:
```
.env
*.properties.local
secrets/
target/
.DS_Store
*.class
```

### Note 10: Documentation is Code
Maintain:
- This architecture document
- README.md with setup instructions
- Inline code comments for complex logic
- API documentation (if REST APIs added)

---

## 21. Next Steps

**Step 1: Review this architecture**
- Read through completely
- Ask clarifying questions if needed
- Request changes if necessary

**Step 2: Approve the architecture**
- Confirm all points are acceptable
- Suggest modifications if needed
- Get alignment before coding

**Step 3: Begin Phase 2 - Project Setup**
Once approved, we'll:
- Create Spring Boot project
- Configure Maven dependencies
- Set up PostgreSQL
- Begin implementation

---

## Summary

This architecture document provides a complete blueprint for building a professional, production-style authentication and user management system using:

- **Java 21** and **Spring Boot 3.x**
- **PostgreSQL 18.6** for data persistence
- **Spring Security** for authentication/authorization
- **Spring Data JPA** with **Hibernate** for ORM
- **Thymeleaf** with **Bootstrap 5** for frontend
- **Maven** for build management

The design follows clean architecture principles with proper separation of concerns, includes comprehensive security measures, and provides a clear path from planning to implementation.

**Total Architecture Points: 21**

---

**Are you ready to proceed with Phase 2: Project Setup?**

Or would you like me to revise any sections of this architecture?
