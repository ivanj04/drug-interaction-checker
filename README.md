# Drug Interaction Checker API

A production-shaped Spring Boot 3 / Java 17 REST API for checking drug-drug interactions, designed to demonstrate pharma domain knowledge and enterprise Java engineering practices relevant to Bristol Myers Squibb's tech stack.

## Pharma Domain Relevance

Drug-drug interaction (DDI) checking is a critical safety function in pharmaceutical care:

- **Patient Safety**: The FDA estimates that adverse drug reactions account for over 100,000 deaths annually in the US, with DDIs contributing significantly.
- **Clinical Decision Support**: This API mirrors the core function of tools like Lexicomp and Micromedex used in clinical and retail pharmacy settings.
- **Severity Classification**: Interactions are classified as MINOR / MODERATE / MAJOR / CONTRAINDICATED, mirroring industry-standard DDI severity schemas.
- **Evidence Levels**: THEORETICAL / CASE_REPORT / CLINICAL_STUDY / WELL_DOCUMENTED reflects how DDI data is graded in pharmacological literature.

## Architecture Highlights

### Caching (Caffeine)

Interaction checks are **cached** at the service layer using Spring Cache + Caffeine:
- TTL: 1 hour
- Max entries: 500
- Cache key: normalized drug pair string (`warfarin:aspirin`)
- Bidirectional lookup: checks both `drug1+drug2` and `drug2+drug1` directions in the DB

This means 500 unique drug pairs stay in memory, dramatically reducing DB load in high-traffic clinical workflows.

### Rate Limiting (Bucket4j)

IP-based rate limiting using the token bucket algorithm:
- **Unauthenticated**: 100 requests/minute per IP
- **Authenticated**: 1,000 requests/minute per user
- In-memory per-IP buckets (no external store required for single-instance)

### Security (JWT / Spring Security 6)

- Stateless JWT authentication via `Authorization: Bearer <token>` header
- BCrypt password hashing
- Role-based authorization: `USER` and `ADMIN` roles
- ADMIN endpoints: POST /api/drugs, POST /api/interactions
- Public endpoints: GET drugs, GET interactions, POST auth

### Database Migrations (Flyway)

- **V1__init.sql**: Schema creation with proper indexes
- **V2__seed_data.sql**: 17 real drugs and 20 documented drug interactions with clinically accurate mechanisms and recommendations

## Seed Data — Real Drug Interactions

| Drug 1 | Drug 2 | Severity | Type |
|--------|--------|----------|------|
| Warfarin | Aspirin | MAJOR | Pharmacodynamic |
| Warfarin | Ibuprofen | MAJOR | Both |
| Warfarin | Amiodarone | MAJOR | Pharmacokinetic |
| Simvastatin | Amiodarone | **CONTRAINDICATED** | Pharmacokinetic |
| Fluoxetine (SSRI) | Phenelzine (MAOI) | **CONTRAINDICATED** | Pharmacodynamic |
| Clopidogrel | Omeprazole | MODERATE | Pharmacokinetic |
| Digoxin | Amiodarone | MAJOR | Pharmacokinetic |
| Methotrexate | Ibuprofen | MAJOR | Pharmacokinetic |
| Lisinopril | Potassium | MODERATE | Pharmacodynamic |
| Ciprofloxacin | Warfarin | MODERATE | Both |
| + 10 more | | | |

## Tech Stack

- **Java 17**, **Spring Boot 3.3**
- **Spring Data JPA** + **H2** (dev) / **PostgreSQL** (prod)
- **Flyway** database migrations
- **Spring Security 6** with **JWT** (jjwt 0.12.x)
- **Caffeine** cache with 1-hour TTL
- **Bucket4j** token-bucket rate limiting
- **OpenAPI / Swagger UI** at `/swagger-ui.html`
- **Lombok** for boilerplate reduction
- **Docker** + **docker-compose**
- **GitHub Actions** CI + **Jenkinsfile**

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+

### Start with H2 (dev profile, no external DB needed)

```bash
cd drug-interaction-checker
mvn spring-boot:run
```

API available at `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
H2 Console: `http://localhost:8080/h2-console`

### Start with Docker Compose (PostgreSQL)

```bash
docker-compose up --build
```

## API Usage

### Register a user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"pharmacist1","email":"pharmacist@hospital.org","password":"securepass123"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"pharmacist1","password":"securepass123"}'
```

### Check aspirin + warfarin interaction

```bash
curl "http://localhost:8080/api/interactions/check?drug1=aspirin&drug2=warfarin"
```

**Response:**
```json
{
  "drug1": "aspirin",
  "drug2": "warfarin",
  "interactionFound": true,
  "interactionId": 1,
  "severityLevel": "MAJOR",
  "interactionType": "PHARMACODYNAMIC",
  "mechanism": "Aspirin inhibits platelet aggregation via COX-1 inhibition; combined with warfarin anticoagulation significantly increases hemorrhagic risk.",
  "clinicalEffect": "Increased risk of serious bleeding including GI hemorrhage, intracranial bleeding, and prolonged bleeding time.",
  "recommendation": "Avoid concomitant use unless specifically indicated. If co-administration is necessary, use lowest effective aspirin dose and monitor INR closely.",
  "evidenceLevel": "WELL_DOCUMENTED",
  "message": "Interaction found between aspirin and warfarin"
}
```

### Check multiple drugs (pairwise)

```bash
curl -X POST http://localhost:8080/api/interactions/check-multiple \
  -H "Content-Type: application/json" \
  -d '{"drugs":["aspirin","warfarin","ibuprofen"]}'
```

Returns all 3 pairwise interaction results.

### Search drugs

```bash
curl "http://localhost:8080/api/drugs?query=aspirin&page=0&size=10"
```

### Get interactions by severity

```bash
curl "http://localhost:8080/api/interactions/by-severity?level=CONTRAINDICATED"
```

### Add a drug (ADMIN)

```bash
curl -X POST http://localhost:8080/api/drugs \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"genericName":"acetaminophen","brandNames":"Tylenol, Panadol","drugClass":"Analgesic/Antipyretic","description":"Commonly used for pain and fever relief."}'
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/ivanjiang/druginteraction/
│   │   ├── DrugInteractionApplication.java
│   │   ├── config/
│   │   │   ├── CacheConfig.java          # Caffeine cache bean (500 entries, 1h TTL)
│   │   │   └── OpenApiConfig.java        # Swagger/OpenAPI configuration
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── DrugController.java
│   │   │   └── InteractionController.java
│   │   ├── dto/                          # Request/Response DTOs
│   │   ├── model/
│   │   │   ├── Drug.java
│   │   │   ├── DrugInteraction.java
│   │   │   ├── User.java
│   │   │   └── enums/
│   │   ├── repository/
│   │   ├── security/
│   │   │   ├── JwtService.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── SecurityConfig.java
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── DrugService.java
│   │       ├── InteractionService.java   # @Cacheable interaction checks
│   │       └── RateLimitService.java     # Bucket4j token buckets
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           ├── V1__init.sql
│           └── V2__seed_data.sql
└── test/
    └── java/com/ivanjiang/druginteraction/
        ├── DrugInteractionApplicationTests.java
        └── service/
            └── InteractionServiceTest.java
```
