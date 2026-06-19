# Ecommerce Backend Application

A production-style E-Commerce Backend application built using Spring Boot.

The project provides secure JWT-based authentication, user management, product management, order management, caching with Redis, database versioning using Flyway, dynamic searching using Specifications and Criteria API, automated testing, and Docker-based deployment.

---

## Architecture

Client
  ↓
Spring Boot REST API
  ↓
Spring Security + JWT
  ↓
Service Layer
  ↓
JPA/Hibernate
  ↓
PostgreSQL

Redis → Caching Layer
Flyway → Database Versioning
Docker → Containerization


## Tech Stack

### Backend

* Java 21
* Spring Boot 3.5
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* PostgreSQL
* Flyway Migration

### Caching

* Redis

### API Documentation

* Swagger / OpenAPI

### Testing

* JUnit 5
* Mockito
* MockMvc
* Integration Testing

### Deployment

* Docker
* Docker Compose

---
## Running with Docker

docker compose up -d

## Features

- JWT Authentication & Refresh Tokens
- Role Based Authorization
- Product Management
- Order Management
- Wishlist Management
- Dynamic Search using Specifications
- Criteria API Queries
- Pagination & Sorting
- Redis Caching
- Flyway Migrations
- Docker & Docker Compose
- Unit Testing (Mockito)
- Integration Testing (MockMvc + Testcontainers)

### Authentication

* JWT Authentication
* Refresh Token Support
* Role Based Authorization

### User Management

* Create User
* Update User
* Delete User
* Search Users
* Pagination Support
* Get Current Logged-in User

### Product Management

* Create Product
* Update Product
* Delete Product
* Search Products
* Pagination Support
* Dynamic Filtering using Specifications
* Dynamic Filtering using Criteria API

### Order Management

* Place Orders
* View Order Details
* View My Orders
* Admin View of All Orders

### Wishlist Management

* Add Product to Wishlist
* Remove Product from Wishlist
* View all user added products from wishlist

### Infrastructure

* Redis Cache
* Flyway Database Migration
* Dockerized Deployment

---

## Running Locally

```bash
mvn clean install
mvn spring-boot:run
```

---

## Running Using Docker Compose

```bash
docker compose down
```
```bash
docker build -t ecommerce-app .
```
```bash
docker compose up -d
```
```
docker logs -f ecommerce-app
```

## API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

---

## Testing

Run all tests:

```bash
mvn test
```

---

## Project Structure

Controller
↓
Service
↓
Repository
↓
PostgreSQL

Additional Components:

* JWT Security
* Redis Cache
* Flyway Migration
* Docker Containers


## Swagger UI Overview

Swagger UI of all API's with Authorize option

![Swagger API List](screenshots/swagger-overview.png)

## Authentication

JWT Login API

![Login API](screenshots/login-api.png)

## Product Search

Dynamic filtering using Specifications

![Search API](screenshots/search-api.png)

## Product Pagination

Dynamic fetching using pageable 

![Pagination API](screenshots/pagination.png)

## Docker Containers

Application running with Docker Compose

![Docker](screenshots/docker-containers.png)
