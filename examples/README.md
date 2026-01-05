# Synchronizer API Examples

This directory contains example requests and usage scenarios for the Synchronizer API.

## Example 1: Publishing a User Creation Message

```bash
curl -X POST http://localhost:8080/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "id": "msg-user-001",
    "type": "USER_CREATED",
    "payload": {
      "userId": "12345",
      "email": "john.doe@example.com",
      "name": "John Doe",
      "createdAt": "2026-01-05T18:15:00"
    },
    "source": "user-service",
    "priority": 5,
    "metadata": {
      "correlationId": "corr-abc-123",
      "requestId": "req-xyz-456"
    }
  }'
```

## Example 2: Publishing an Order Event

```bash
curl -X POST http://localhost:8080/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "type": "ORDER_PLACED",
    "payload": {
      "orderId": "ORD-2026-001",
      "customerId": "CUST-12345",
      "items": [
        {"productId": "PROD-001", "quantity": 2, "price": 29.99},
        {"productId": "PROD-002", "quantity": 1, "price": 49.99}
      ],
      "totalAmount": 109.97
    },
    "source": "order-service",
    "priority": 8,
    "metadata": {
      "correlationId": "order-flow-123",
      "region": "US-EAST"
    }
  }'
```

## Example 3: Retrieving a Message by ID

```bash
curl http://localhost:8080/api/messages/msg-user-001
```

## Example 4: Getting All Messages (Paginated)

```bash
curl "http://localhost:8080/api/messages?page=0&size=10&sortBy=timestamp&direction=DESC"
```

## Example 5: Getting Messages by Type

```bash
curl http://localhost:8080/api/messages/type/USER_CREATED
```

## Example 6: Getting Recent Messages

```bash
# Get messages from the last 2 hours
curl "http://localhost:8080/api/messages/recent?hours=2"
```

## Example 7: Getting Message Statistics

```bash
curl http://localhost:8080/api/messages/stats
```

Response:
```json
{
  "totalMessages": 150,
  "receivedMessages": 10,
  "processedMessages": 140
}
```

## Example 8: Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Example 9: Accessing Swagger UI

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

## Using with HTTPie

If you prefer HTTPie:

```bash
# Publish a message
http POST localhost:8080/api/messages/publish \
  type="PAYMENT_PROCESSED" \
  payload:='{"amount": 99.99, "currency": "USD"}' \
  source="payment-service" \
  priority:=7

# Get messages
http GET localhost:8080/api/messages page==0 size==20
```

## Testing Kafka Integration

To verify messages are being sent to Kafka, you can use kafka-console-consumer:

```bash
# If running with docker-compose
docker exec -it synchronizer-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic synchronizer-messages \
  --from-beginning
```
