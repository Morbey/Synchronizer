# Quick Start Guide

Get the Synchronizer up and running in minutes!

## Prerequisites

- Docker and Docker Compose installed
- Git
- (Optional) Java 25 for local development

## Option 1: Docker Compose (Recommended for Quick Start)

### Step 1: Clone the Repository
```bash
git clone https://github.com/Morbey/Synchronizer.git
cd Synchronizer
```

### Step 2: Start All Services
```bash
docker-compose up -d
```

This will start:
- PostgreSQL database
- Zookeeper
- Kafka broker
- Synchronizer application

### Step 3: Wait for Services to be Ready
```bash
# Check health
docker-compose ps

# Watch logs
docker-compose logs -f synchronizer
```

Wait until you see: `Started SynchronizerApplication`

### Step 4: Verify the Application
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Open Swagger UI in browser
# http://localhost:8080/swagger-ui.html
```

### Step 5: Publish Your First Message
```bash
curl -X POST http://localhost:8080/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "type": "HELLO_WORLD",
    "payload": {
      "message": "My first Kafka message!",
      "timestamp": "2026-01-05T18:15:00"
    },
    "source": "quickstart"
  }'
```

Response:
```json
{
  "messageId": "generated-uuid",
  "status": "PUBLISHED"
}
```

### Step 6: Retrieve the Message
```bash
# Get recent messages
curl http://localhost:8080/api/messages/recent?hours=1

# Get statistics
curl http://localhost:8080/api/messages/stats
```

### Step 7: Monitor Kafka (Optional)
```bash
# View messages in Kafka topic
docker exec -it synchronizer-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic synchronizer-messages \
  --from-beginning
```

### Clean Up
```bash
docker-compose down -v
```

## Option 2: Local Development

### Prerequisites
- Java 25 JDK
- PostgreSQL 17
- Kafka (or use Docker for Kafka only)

### Step 1: Start Infrastructure
```bash
# Start only Kafka and PostgreSQL
docker-compose up -d postgres kafka zookeeper
```

### Step 2: Build the Application
```bash
./mvnw clean package
```

### Step 3: Run the Application
```bash
./mvnw spring-boot:run
```

Or run the JAR:
```bash
java -jar target/synchronizer-1.0.0-SNAPSHOT.jar
```

## Option 3: Kubernetes with Helm

### Prerequisites
- Kubernetes cluster
- Helm 3.x
- kubectl configured

### Step 1: Prepare Dependencies
Ensure you have Kafka and PostgreSQL available in your cluster or externally.

### Step 2: Update Helm Values
Edit `k8s/charts/synchronizer/values.yaml`:

```yaml
kafka:
  bootstrapServers: "your-kafka-service:9092"

database:
  url: "jdbc:postgresql://your-postgres-service:5432/synchronizer"
  username: "postgres"
  password: "your-password"
```

### Step 3: Build and Push Docker Image
```bash
docker build -t your-registry/synchronizer:1.0.0 .
docker push your-registry/synchronizer:1.0.0
```

Update `values.yaml`:
```yaml
image:
  repository: your-registry/synchronizer
  tag: "1.0.0"
```

### Step 4: Install with Helm
```bash
helm install synchronizer ./k8s/charts/synchronizer
```

### Step 5: Verify Deployment
```bash
kubectl get pods
kubectl get svc

# Port forward to access locally
kubectl port-forward svc/synchronizer 8080:8080
```

### Step 6: Access the Application
```bash
curl http://localhost:8080/actuator/health
```

## Common Tasks

### View Application Logs
```bash
# Docker Compose
docker-compose logs -f synchronizer

# Kubernetes
kubectl logs -f deployment/synchronizer
```

### Access Database
```bash
# Docker Compose
docker exec -it synchronizer-postgres psql -U postgres -d synchronizer

# List messages
SELECT message_id, type, source, status FROM messages;
```

### Scale the Application (Kubernetes)
```bash
kubectl scale deployment synchronizer --replicas=5
```

### Update Configuration
```bash
# Edit application.yaml
# Restart the application

# Docker Compose
docker-compose restart synchronizer

# Kubernetes
kubectl rollout restart deployment/synchronizer
```

## Troubleshooting

### Application Won't Start

1. **Check Kafka connectivity**:
```bash
docker-compose logs kafka
```

2. **Check PostgreSQL connectivity**:
```bash
docker-compose logs postgres
```

3. **View application logs**:
```bash
docker-compose logs synchronizer
```

### Messages Not Being Consumed

1. **Verify Kafka topic exists**:
```bash
docker exec synchronizer-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --list
```

2. **Check consumer group**:
```bash
docker exec synchronizer-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group synchronizer-consumer-group
```

### Database Connection Issues

1. **Verify PostgreSQL is running**:
```bash
docker-compose ps postgres
```

2. **Check database credentials** in `application.yaml` or environment variables

3. **Run Liquibase manually**:
```bash
./mvnw liquibase:update
```

## Next Steps

- Explore the [API Documentation](http://localhost:8080/swagger-ui.html)
- Read the [Architecture Guide](ARCHITECTURE.md)
- Check out [API Examples](examples/README.md)
- Review the [Full README](README.md)

## Support

For issues or questions:
- Open an issue on GitHub
- Check existing issues for solutions
- Review the documentation

Happy messaging! 🚀
