# Synchronizer

Synchronizer of Kafka messages using Java 25 capable of high throughput and database persistence.

## Overview

A production-ready Spring Boot application for consuming and publishing Kafka messages with:
- **Java 25** runtime
- **Spring Cloud Kafka** for message streaming
- **Spring WebFlux** for reactive REST APIs
- **Spring Data JPA** for database persistence
- **Liquibase** for database migrations
- **OpenAPI/Swagger** for API documentation
- **Kubernetes** deployment with Helm charts

## Features

- ✅ Kafka message consumption and publishing with JSON serialization
- ✅ Automatic message persistence to PostgreSQL database
- ✅ RESTful API for message operations
- ✅ OpenAPI/Swagger UI for API documentation
- ✅ Database versioning with Liquibase
- ✅ Kubernetes-ready with Helm charts
- ✅ Docker containerization
- ✅ Health checks and metrics via Spring Actuator
- ✅ High availability with horizontal pod autoscaling

## Technology Stack

- **Java**: 25
- **Spring Boot**: 3.4.1
- **Spring Cloud**: 2024.0.0
- **Spring Cloud Stream Kafka**: Latest
- **Spring Data JPA**: Latest
- **Spring WebFlux**: Latest
- **Liquibase**: Latest
- **OpenAPI**: 2.7.0
- **PostgreSQL**: 17
- **Apache Kafka**: 3.x (via Confluent)
- **Maven**: Latest

## Prerequisites

- Java 25
- Docker and Docker Compose
- Kubernetes cluster (for K8s deployment)
- Helm 3.x (for K8s deployment)
- Maven 3.x

## Quick Start

### Local Development with Docker Compose

1. **Build the project**:
```bash
./mvnw clean package
```

2. **Start all services** (Kafka, Zookeeper, PostgreSQL, Application):
```bash
docker-compose up -d
```

3. **Access the application**:
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs
- Health Check: http://localhost:8080/actuator/health

### Running Locally (without Docker)

1. **Start PostgreSQL**:
```bash
docker run -d -p 5432:5432 -e POSTGRES_DB=synchronizer -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:17-alpine
```

2. **Start Kafka**:
```bash
docker-compose up -d kafka zookeeper
```

3. **Run the application**:
```bash
./mvnw spring-boot:run
```

## API Endpoints

### Publish Message
```bash
POST /api/messages/publish
Content-Type: application/json

{
  "id": "msg-123",
  "type": "USER_CREATED",
  "payload": {
    "userId": "123",
    "name": "John Doe"
  },
  "source": "user-service",
  "priority": 5,
  "metadata": {
    "correlationId": "corr-123"
  }
}
```

### Get Message by ID
```bash
GET /api/messages/{messageId}
```

### Get All Messages (Paginated)
```bash
GET /api/messages?page=0&size=20&sortBy=createdAt&direction=DESC
```

### Get Messages by Type
```bash
GET /api/messages/type/{type}
```

### Get Messages by Source
```bash
GET /api/messages/source/{source}
```

### Get Recent Messages
```bash
GET /api/messages/recent?hours=24
```

### Get Statistics
```bash
GET /api/messages/stats
```

## Kafka Configuration

All Kafka configurations are in `application.yaml` using Spring Cloud Stream parameters:

- **Bootstrap Servers**: Configurable via `KAFKA_BOOTSTRAP_SERVERS` environment variable
- **Topics**: `synchronizer-messages` (auto-created)
- **Consumer Group**: `synchronizer-consumer-group`
- **Serialization**: JSON with Jackson
- **Deserialization**: JSON with trusted packages

## Database Schema

The application uses Liquibase for database migrations. The schema includes:

- **messages** table with columns:
  - `db_id`: Primary key
  - `message_id`: Unique message identifier
  - `type`: Message type/category
  - `payload`: JSON payload
  - `source`: Source system
  - `timestamp`: Message timestamp
  - `priority`: Message priority
  - `metadata`: Additional metadata
  - `created_at`: Record creation timestamp
  - `processed_at`: Processing timestamp
  - `status`: Message status (RECEIVED, PROCESSED)

Indexes on: `type`, `timestamp`, `source`, `status`

## Kubernetes Deployment

### Using Helm

1. **Build and push Docker image**:
```bash
docker build -t your-registry/synchronizer:1.0.0 .
docker push your-registry/synchronizer:1.0.0
```

2. **Update values.yaml**:
```yaml
image:
  repository: your-registry/synchronizer
  tag: "1.0.0"

kafka:
  bootstrapServers: "your-kafka-service:9092"

database:
  url: "jdbc:postgresql://your-postgres-service:5432/synchronizer"
  username: "postgres"
  password: "postgres"
```

3. **Install with Helm**:
```bash
helm install synchronizer ./k8s/charts/synchronizer
```

4. **Upgrade deployment**:
```bash
helm upgrade synchronizer ./k8s/charts/synchronizer
```

5. **Uninstall**:
```bash
helm uninstall synchronizer
```

### Helm Chart Features

- HorizontalPodAutoscaler (2-10 replicas)
- Pod anti-affinity for high availability
- Security contexts (non-root user)
- Resource limits and requests
- Liveness and readiness probes
- ConfigMap and Secret management
- Optional Ingress support

## Configuration

### Environment Variables

- `SERVER_PORT`: Application port (default: 8080)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka brokers (default: localhost:9092)
- `DATABASE_URL`: JDBC URL (default: jdbc:postgresql://localhost:5432/synchronizer)
- `DATABASE_USERNAME`: Database username (default: postgres)
- `DATABASE_PASSWORD`: Database password (default: postgres)

### Application Properties

All configuration is in `src/main/resources/application.yaml` with support for environment variable overrides.

## Monitoring

### Actuator Endpoints

- `/actuator/health`: Health status
- `/actuator/info`: Application info
- `/actuator/metrics`: Application metrics
- `/actuator/prometheus`: Prometheus metrics

### Logging

- Console and file logging configured
- Log levels configurable per package
- Structured logging for Kafka operations

## Development

### Building
```bash
./mvnw clean package
```

### Running Tests
```bash
./mvnw test
```

### Running with Maven
```bash
./mvnw spring-boot:run
```

## Production Considerations

1. **High Throughput**: Configured for batch processing and optimized Kafka settings
2. **Database Connection Pooling**: HikariCP with optimized settings
3. **Graceful Shutdown**: Configured for zero-downtime deployments
4. **Security**: Non-root containers, security contexts in K8s
5. **Observability**: Health checks, metrics, and structured logging
6. **Scalability**: Horizontal pod autoscaling based on CPU/memory

## License

Apache 2.0

## Support

For issues and questions, please open an issue in the repository.
