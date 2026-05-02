# 🏨 Tourism Hotel Booking System

A RESTful backend application developed using Spring Boot for managing hotel bookings.  
The system allows users to browse hotels, manage room types, and create bookings with secure role-based access.

---

## 🚀 Overview

The Tourism Hotel Booking System is designed using a modular and layered architecture.  
It supports multiple user roles and ensures secure access to system features.

---

## ✨ Features

### 👤 User Management
- User registration and login
- Role-based system (ADMIN, MANAGER, GUEST)
- Secure authentication using JWT

---

### 🏨 Hotel Management
- Create, update, and delete hotels
- Assign hotels to managers
- Store hotel details (name, location, description, rating)

---

### 🛏️ Room Types
- Define room capacity and pricing
- Associate room types with hotels
- Manage room availability

---

### 📅 Booking System
- Create bookings with validation
- Check availability before booking
- Calculate total booking price
- Link bookings to guests

---

### 📊 Availability & Pricing
- Availability checking logic
- Dynamic price calculation

---

### 🔐 Security
- JWT-based authentication
- Role-based authorization
- Protected endpoints using @PreAuthorize

---

## 🧱 Architecture

The system follows a layered architecture:

Controller → Service → Repository → Database

- **Controller Layer**: Handles HTTP requests
- **Service Layer**: Contains business logic
- **Repository Layer**: Handles database access

---

## 📦 Modules

- Users Module (AppUser, Guest, Manager)
- Hotel Module
- RoomType Module
- Booking Module
- Availability & Pricing Module
- Security Module

---

## 🧠 Design Decisions

- **Layered Architecture** to separate concerns  
- **DTO Pattern** to isolate API from database entities  
- **Role-Based Access Control** for security  
- **Spring Boot Framework** for rapid development  
- **Validation Logic** to ensure data integrity  

---

## 🧪 Testing

### Unit Testing
- Implemented using JUnit 5 and Mockito
- Covers service layer logic
- Uses mocks for dependencies

### Integration Testing
- Implemented using SpringBootTest and MockMvc
- Tests full request flow
- Simulates authenticated users

---

## ⚙️ Technologies

- Java
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- JUnit 5
- Mockito

---

## ▶️ Running the Project

```bash
mvn clean install
mvn spring-boot:run
---
🧪 Running Tests
mvn test

📌 Notes
Security is implemented using roles and JWT tokens
Integration tests simulate authentication using @WithMockUser
The system is designed for scalability and future extension
---
👩‍💻 Author

Zaina Alahmar
Jana Abu Zer
---
📚 Course

SWER 313 – Software Engineering Project
