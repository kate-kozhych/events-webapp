# Event Management System

A simplified event listing and management platform built during the **Development of Web Applications** course at the University of Jaén (Erasmus+).  
The project provides secure user registration, authentication, and profile management with role-based access, laying the foundation for a scalable event participation system.  

---

## ✨ My Contribution
- 💡 **Project Idea & Design**: conceived the project concept and created the system architecture (entity design, diagrams, documentation).  
- 🏗️ **Entity & Database Modeling**: implemented `User`, `Event`, and `RegisteredEntity` entities with JPA (EclipseLink).  
- 🔐 **Authentication & Security**: developed registration, login, and profile management with **PBKDF2WithHmacSHA512 password hashing**.  
- 👥 **Role Management**: implemented access control with **user** and **administrator** roles.  
- 🖥️ **Frontend Integration**: built JSF (`.xhtml`) views for login and signup, linked to backend logic.  

---

## 🔑 Features
- User registration with name, surname, student email (UJA), ESN card, and password.  
- Secure login with hashed passwords.  
- Profile editing and password change functionality.  
- Role-based access restrictions (Admins vs Users).  

---

## 🛠️ Tech Stack
- **Java 21**  
- **Jakarta EE 10**  
  - JSF (Jakarta Faces) for UI  
  - JPA (EclipseLink) for persistence  
  - CDI (Contexts and Dependency Injection)  
  - Jakarta Security (Soteria) for authentication/roles  
- **H2 Database** (for local testing and deployment)  
- **Maven** (WAR packaging)  
- **Payara Server** (deployment)  

---

## 📚 Project Documentation

- [📄 Project Documentation (requirements & overview)](DocumentationESNts.pdf)  
- [📄 System Design (original idea & architecture)](ESNTS.pdf)  
- [🖼️ Class Diagram (entities & database schema)](class_diagram.png)  

---

## 🚀 Planned Features (Team Scope)
- Event management: create, edit, delete, and filter events.  
- Event participation: join and list user’s events.  
- Event search by category, location, and time (today, this week).  
- Detailed event description pages.  

---

## 🌍 Context
This project was developed during my **Erasmus+ exchange semester** in Spain at the University of Jaén, as part of the *Development of Web Applications* course.  
It showcases my ability to **design, implement, and secure a Java EE web application** from scratch.  

---

## 🔮 Future Improvements
- Complete the event management and participation module.  
- Add a modern responsive frontend (React/Angular).  
- Expose REST API endpoints for scalability.  
- Extend user management with advanced admin features.  
