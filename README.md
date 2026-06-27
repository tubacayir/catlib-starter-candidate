# CatLib — Starter Project

This is the starter project for your take-home engineering task.

## What this app does

CatLib is a small Spring Boot REST API. It currently exposes one endpoint:

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/cat/{tag}` | Fetches a cat image from CATAAS tagged with the given topic |

**Example:**
```
GET http://localhost:8080/api/cat/space
```

**Response:**
```json
{
  "tag": "space",
  "imageUrl": "https://cataas.com/cat/abc123"
}
```

## Requirements

- Java 17+
- Maven 3.8+

## Running the app

```bash
./mvnw spring-boot:run
```

Or build and run the jar:

```bash
./mvnw clean package
java -jar target/catlib-0.0.1-SNAPSHOT.jar
```

The app starts on port `8080` by default.

## Configuration

You can override defaults in `src/main/resources/application.properties` or via environment variables.

## Notes

- The CATAAS API is free and requires no authentication: https://cataas.com
- Open Library API is free and requires no authentication: https://openlibrary.org/developers/api
- Read the full task document before making any changes

## Additional Endpoints

### Store topic content

Fetches a cat image from CATAAS and related book metadata from Open Library, stores the combined result locally as a JSON file, and returns the stored content.

**Request**

```http
POST /api/topics/{topic}
```

Example:

```text
POST http://localhost:8080/api/topics/space
```

---

### Get stored content summary

Returns a summary of all topics stored so far.

**Request**

```http
GET /api/topics/summary
```

Example:

```text
GET http://localhost:8080/api/topics/summary
```

## Local Storage

The application stores each topic as a separate JSON file under the `storage` directory.

Example:

```text
storage/
├── space.json
├── history.json
└── java.json
```

## Running Tests

```bash
./mvnw test
```

## Design Notes

* Controllers are kept thin; orchestration is handled by `TopicContentService`.
* HTTP communication with external APIs is shared through `HttpClientService`.
* External API errors are handled centrally using `GlobalExceptionHandler`.
* Java records are used for immutable DTOs where appropriate.
* Local storage uses JSON files, following the assignment requirements.
* Unit tests are included for the orchestration logic in `TopicContentService`.
