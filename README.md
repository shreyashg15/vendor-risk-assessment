# Tool-41: Vendor Risk Assessment

An AI-powered, full-stack microservices application designed for Vendor Risk Assessment. Built as an MVP for a Capstone Project.

## Project Architecture
This project implements an industry-standard microservices architecture using 5 containerized services:
1. **Frontend**: React 18 + Vite with Tailwind CSS
2. **Backend**: Java 17 + Spring Boot 3.x (REST API, JWT Security, RBAC)
3. **AI Microservice**: Python 3.11 + Flask (Groq API LLaMA-3 integration)
4. **Database**: PostgreSQL 15 (Relational Data & Audit Logs via Flyway)
5. **Cache**: Redis 7 (Rate Limiting and Performance Caching)

## Features
- **Role-Based Access Control (RBAC)**: Master (`ADMIN`), Manager, and Viewer authentication via JWT.
- **AI-Powered Risk Analysis**: Analyzes vendor profiles using LLaMA-3 to generate automated risk insights.
- **Full-Stack CRUD Operations**: Create, read, update, and delete vendor records securely.
- **Pagination & Search**: High-performance paginated queries filtering vendors by status and name.
- **Responsive Dashboard**: Beautiful UI with Tailwind CSS and Vite.

## Prerequisites
- **Docker** and **Docker Compose** installed on your machine.
- Optional: Obtain a free Groq API key from [console.groq.com](https://console.groq.com) for the AI Service.

## Installation & Setup

1. Copy `.env.example` to `.env` and fill in your variables (especially `GROQ_API_KEY`).
2. Build and start all services using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```
3. The services will be available at:
   - **Frontend UI**: `http://localhost`
   - **Backend API**: `http://localhost:8080/api`
   - **AI Microservice**: `http://localhost:5000/api/ai`

## Default Credentials
The application is pre-seeded with test accounts for immediate testing:
- **Master Account (ADMIN)**: `master` / `master123`
- **Viewer Account (VIEWER)**: `viewer` / `viewer123`

## Built With
* Spring Boot, Spring Security, Spring Data JPA, Flyway
* React, React Router, Tailwind CSS, Axios
* Flask, Flask-Cors, Flask-Limiter, Groq Client
* Docker, Postgres, Redis