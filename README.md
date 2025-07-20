# Andrei's XYZ Bank - How to Use

Spring Boot REST API for customer registration and account opening. Supports NL/BE customers with automatic IBAN generation and JWT authentication.

## Quick Start

**Recommended:** Use Docker

```bash
./gradlew bootRun
```

Application runs on `http://localhost:8080`

## API Documentation

*Available only when application is running:*

- **Swagger UI OpenAPI**: http://localhost:8080/swagger-ui.html
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Testing

**Recommended:** Run with Docker then test with Postman collection.

1. Ensure you have Docker installed
2. Build and start with Docker Compose (builds app + creates MySQL DB):
```bash
docker-compose up
```

3. Application will be available at `http://localhost:8080`

4. Test with Postman collection

**Postman Collection:**
- Local file: `assignment-xyz.postman_collection.json`
- Online: [Postman Workspace](https://web.postman.co/workspace/My-Workspace~fe2e9855-d084-4940-82b7-5bc8edb73140/collection/10600480-c54c58d2-8e4c-4e66-a5df-e2e69030bf14?action=share&source=copy-link&creator=10600480)

**How to use Postman Collection:**

Import the collection "Andrei's XYZ bank requests" in Postman by using the link above, or importing the JSON file from this repository. Then follow this workflow:

1. **Register a user** - Use any register request to create a new customer (user data under the JSON body)
2. **Login** - Use the generated password from registration response to login with the same username as the registration (user data under the JSON body)
3. **Access overview** - Copy the JWT token from successful login response (if login is successful) to access the overview method by pasting the JWT token under Authorization → Bearer Token

**API Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/register` | Register new customer |
| POST | `/api/login` | Customer authentication |
| GET | `/api/overview` | Account details (requires JWT) |

**Example Requests:**

Registration - `POST http://localhost:8080/api/register`:
```json
{
  "name": "Andrei Popescu",
  "address": "123 Main Street, Amsterdam",
  "username": "andrei12323",
  "dateOfBirth": "2000-03-15",
  "country": "NL"
}
```

Login - `POST http://localhost:8080/api/login`:
```json
{
  "username": "andrei12323",
  "password": "PasswordFromRegisterAbove"
}
```

Overview - `GET http://localhost:8080/api/overview`:
```
Authorization: Bearer <JWT_TOKEN_FROM_LOGIN_ABOVE>
```

**Automated Tests:**
Includes both unit and integration tests covering all functionality.

## Build & Test

```bash
./gradlew build
./gradlew test
./gradlew jacocoTestReport
```

