# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Kubernetes Cluster                       │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  Ingress Controller                   │  │
│  └───────────────────────┬──────────────────────────────┘  │
│                          │                                   │
│  ┌───────────────────────┴──────────────────────────────┐  │
│  │              Synchronizer Service                     │  │
│  │                  (ClusterIP)                          │  │
│  └───────────────────────┬──────────────────────────────┘  │
│                          │                                   │
│  ┌───────────────────────┴──────────────────────────────┐  │
│  │         Synchronizer Pods (2-10 replicas)            │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │   Spring Boot Application (Java 25)            │  │  │
│  │  │   ┌──────────────────────────────────────┐     │  │  │
│  │  │   │  REST API (WebFlux)                  │     │  │  │
│  │  │   │  - MessageController                 │     │  │  │
│  │  │   │  - OpenAPI/Swagger                   │     │  │  │
│  │  │   └──────────────────────────────────────┘     │  │  │
│  │  │   ┌──────────────────────────────────────┐     │  │  │
│  │  │   │  Kafka Integration                   │     │  │  │
│  │  │   │  - MessageProducerService            │     │  │  │
│  │  │   │  - MessageConsumerService            │     │  │  │
│  │  │   │  - Spring Cloud Stream               │     │  │  │
│  │  │   └──────────────────────────────────────┘     │  │  │
│  │  │   ┌──────────────────────────────────────┐     │  │  │
│  │  │   │  Database Layer (Spring Data)        │     │  │  │
│  │  │   │  - MessageRepository                 │     │  │  │
│  │  │   │  - MessageQueryService               │     │  │  │
│  │  │   └──────────────────────────────────────┘     │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  └───────────────┬──────────────────┬───────────────────┘  │
│                  │                  │                        │
└──────────────────┼──────────────────┼────────────────────────┘
                   │                  │
        ┌──────────▼─────────┐ ┌─────▼──────────┐
        │   Kafka Cluster    │ │  PostgreSQL DB │
        │  (External/K8s)    │ │   (External)   │
        │                    │ │                │
        │  Topic:            │ │  Tables:       │
        │  - synchronizer-   │ │  - messages    │
        │    messages        │ │                │
        └────────────────────┘ └────────────────┘
```

## Component Breakdown

### 1. REST API Layer (Spring WebFlux)
- **MessageController**: Reactive REST endpoints for message operations
- **OpenAPI Integration**: Automatic API documentation
- **Actuator**: Health checks and metrics

### 2. Message Processing Layer
- **MessageProducerService**: Publishes messages to Kafka
- **MessageConsumerService**: Consumes messages from Kafka
- **KafkaStreamBindings**: Spring Cloud Stream functional bindings

### 3. Data Persistence Layer
- **MessageRepository**: JPA repository for database operations
- **MessageQueryService**: Query service for complex retrievals
- **Liquibase**: Database migration management

### 4. Models
- **Message**: DTO for Kafka messages (JSON serializable)
- **MessageEntity**: JPA entity for database persistence

## Message Flow

### Publishing Flow
```
Client → REST API → MessageProducerService → Kafka Topic
                                                   ↓
                         MessageConsumerService ← Consumer Group
                                   ↓
                         MessageEntity (Database)
```

### Query Flow
```
Client → REST API → MessageQueryService → MessageRepository → Database
                                                                   ↓
                                                             Return Results
```

## Key Features

### High Throughput Configuration
- **Kafka Producer**: Batch size 16KB, compression (snappy), acks=all
- **Kafka Consumer**: Auto-commit enabled, earliest offset reset
- **Database**: Connection pooling (HikariCP), batch inserts enabled
- **Reactive**: WebFlux for non-blocking I/O

### High Availability
- **Kubernetes**: 2-10 pod replicas with HPA
- **Pod Anti-Affinity**: Distribution across nodes
- **Health Checks**: Liveness and readiness probes
- **Graceful Shutdown**: Zero-downtime deployments

### Scalability
- **Horizontal Scaling**: Auto-scaling based on CPU/memory
- **Kafka Partitioning**: Message distribution across partitions
- **Database Indexing**: Optimized queries on type, timestamp, source

### Observability
- **Structured Logging**: Per-package log levels
- **Metrics**: Prometheus-compatible metrics
- **Health Endpoints**: Application and dependency health
- **API Documentation**: Interactive Swagger UI

## Technology Stack Versions

| Component | Version |
|-----------|---------|
| Java | 25 |
| Spring Boot | 3.4.1 |
| Spring Cloud | 2024.0.0 |
| Spring Cloud Stream | Latest |
| Kafka | 3.x |
| PostgreSQL | 17 |
| Liquibase | Latest |
| OpenAPI | 2.7.0 |

## Security Considerations

1. **Container Security**
   - Non-root user (UID 1000)
   - Read-only root filesystem option
   - Dropped capabilities

2. **Kubernetes Security**
   - Security contexts
   - Pod security policies
   - Secret management

3. **Database Security**
   - Credentials in Kubernetes secrets
   - Connection encryption (configurable)

4. **API Security**
   - Input validation
   - JSON deserialization trusted packages
   - Rate limiting (via Ingress/API Gateway)

## Deployment Options

### Local Development
- Docker Compose for all services
- Includes Kafka, Zookeeper, PostgreSQL

### Kubernetes
- Helm chart with configurable values
- Support for multiple environments
- Integration with external services

### Cloud Native
- Compatible with AWS EKS, GCP GKE, Azure AKS
- Can use managed Kafka (MSK, Confluent Cloud)
- Can use managed PostgreSQL (RDS, Cloud SQL)
