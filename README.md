# REST Assured API Test Automation Framework

Automated RESTful API testing framework developed with **Java**, **REST Assured**, **TestNG**, **Maven**, and **GitHub Actions CI/CD**.

## Highlights Matching Project Scope
- **30 Automated Test Cases (`TC01` – `TC30`)**: Covers end-to-end CRUD endpoints (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`), nested resources, and query parameter filtering.
- **Token Authentication**: `TokenManager` and `authRequestSpec` inject and validate `Authorization: Bearer <token>` headers on write operations.
- **Data-Driven Testing (`@DataProvider`)**: Parameterized test suites in `ProductAndOrderCrudTests` and `UserAndCustomerApiTests`.
- **JSON Schema Validation**: Contract verification against `order-schema.json` and `user-schema.json` using REST Assured's `json-schema-validator`.
- **Positive, Negative & Boundary Scenarios**: Verifies `200 OK`, `201 Created`, and `404 Not Found` status codes, pagination boundaries (`_limit=1`, `_limit=5`), and 250-character payload limits.
- **GitHub Actions CI/CD Integration**: Automatically runs `mvn clean test` on every push or pull request and archives the generated TestNG HTML reports.

## Project Structure
```text
restassured-api-tests/
├── .github/workflows/ci.yml
├── pom.xml
├── testng.xml
└── src/test/
    ├── java/com/uday/api/
    │   ├── base/BaseTest.java
    │   ├── models/OrderPayload.java
    │   ├── utils/TokenManager.java
    │   └── tests/
    │       ├── ProductAndOrderCrudTests.java
    │       ├── UserAndCustomerApiTests.java
    │       └── BoundaryAndNegativeApiTests.java
    └── resources/schemas/
        ├── order-schema.json
        └── user-schema.json
```

## How to Run Locally
1. Verify **Java (JDK 11 or 17+)** and **Maven** are installed:
   ```bash
   java -version
   mvn -version
   ```
2. Run the complete 30-test automation suite:
   ```bash
   mvn clean test
   ```
3. Open the generated TestNG HTML report in your browser:
   - `target/surefire-reports/index.html`
   - `target/surefire-reports/emailable-report.html`
