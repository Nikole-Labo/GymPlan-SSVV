# Gym Management System

Web application for managing gyms, customers, and gym subscriptions (abonaments). Built for a take-home exam (Task 1).

## Stack

- Java 21
- Spring Boot 3.3 (Web, JPA, Thymeleaf, Validation)
- H2 Database (file-based: `./data/gymdb`)
- Bootstrap 5

## Architecture (MVC)

| Layer | Responsibility |
|-------|----------------|
| **Model** | `Gym`, `Customer`, `Abonament` entities |
| **View** | Thymeleaf HTML templates |
| **Controller** | Web request handlers |
| **Service** | Business logic (purchase validations, reports) |
| **Repository** | Data access (Spring Data JPA) |

## Features

### CRUD (3 entities)

- `/gyms` — Gym management
- `/customers` — Customer management
- `/abonaments` — Subscription management

### Purchase Subscription (all 3 entities)

- `/purchase-subscription`
- Selects customer + gym, creates abonament
- **Validations:** no duplicate active subscription at same gym; gym capacity check

### Gym Performance Report

- `/reports/gym-performance`
- Select gym + month → total revenue, most popular plan, new vs. returning customers

## Run

Requirements: **Java 17+** (Java 21 recommended), Maven 3.9+

```bash
cd gym-management
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

H2 console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL: `jdbc:h2:file:./data/gymdb` — user `sa`, empty password.

Sample data is loaded on first startup (Downtown Fitness has capacity 3 — useful for testing capacity limits).

## Demo video suggestions

1. CRUD on Gym and Customer
2. Purchase subscription (success)
3. Try duplicate purchase or fill gym to show validation
4. Generate report for Downtown Fitness + current month
