# EatClub Order Management Service

A Spring Boot–based microservice for managing orders using MySQL for persistence and Redis Streams for real-time event streaming.

---

## 1. System Architecture Overview

**Key Components:**
- **Spring Boot (REST API):** Exposes endpoints for order creation, status updates, and retrieval.
- **MySQL Database:** Stores order and order item data using JPA entities.
- **Redis Streams:** Enables event-driven communication and asynchronous processing.
- **Springdoc OpenAPI:** Provides auto-generated API documentation.
- **Lettuce Client:** Handles Redis connections.

**Logical Flow:**
```
Client
  ↓
OrderController (REST API)
  ↓
OrderService (Business Logic)
  ↓
OrderRepository (MySQL)
  ↓
RedisOrderEventPublisher (Publishes order events)
  ↓
Redis Stream (orders:stream)
  ↓
OrderEventConsumer (Consumes and logs events)
```

---

## 2. Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL (local or cloud)
- Redis (local or Redis Cloud)

### Steps to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/mu-droid/Eatclub_OMS.git
   cd Eatclub_OMS
   ```
2. Create `application.properties` in `src/main/resources/`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/order_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update

   spring.data.redis.host=redis_host
   spring.data.redis.port=6379
   spring.data.redis.password=your_password
   ```
3. Build and start the service:
   ```bash
   mvn clean spring-boot:run
   ```
4. Access Swagger UI:
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

## 3. API Contracts

### Create Order
**Endpoint:** `POST /api/v1/orders`  
**Request:**
```json
{
  "customerId": "CUST123",
  "items": [
    {"productId": "P1", "quantity": 2, "price": 50.0},
    {"productId": "P2", "quantity": 1, "price": 30.0}
  ]
}
```
**Response:**
```json
{
  "id": "uuid",
  "customerId": "CUST123",
  "status": "PLACED",
  "totalAmount": 130.0,
  "items": [
    {"productId": "P1", "quantity": 2, "price": 50.0}
  ]
}
```

### Update Order Status
**Endpoint:** `PATCH /api/v1/orders/{orderId}/status`  
**Request:**
```json
{"status": "DELIVERED"}
```

### Get Order
**Endpoint:** `GET /api/v1/orders/{orderId}`  
**Response:** same as Create Order.

---

## 4. Flow / Sequence Diagram

```
[Client] → [OrderController] → [OrderService]
   ↓                     ↓
  Request           Save to MySQL
                        ↓
                 Publish Event → [Redis Stream]
                                        ↓
                                [OrderEventConsumer]
```

---

## 5. Scaling Considerations

- **Horizontal Scaling:**
    - Multiple instances of the service can run behind a load balancer.
    - Each instance can share the same Redis Stream (using different consumer names within a group).

- **Database Scaling:**
    - Use read replicas for reporting; master for writes.

- **Redis Scaling:**
    - Use Redis Cluster or Cloud Redis for high availability.

- **Event Processing:**
    - Redis Stream Consumer Groups distribute events evenly among instances.

---

## 6. Documentation

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`
- **External Docs:** [EatClub OMS GitHub Repo](https://github.com/mu-droid/Eatclub_OMS)

---

**Author:** Mudit Singh  
**Contact:** mudit12131@gmail.com
