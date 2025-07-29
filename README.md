# SEFAZ Transparency Consumer

A Spring Boot application that consumes data from the Sergipe State Tax Authority (SEFAZ) transparency API and stores it in a local database.

## Overview

This application is designed to:
- Consume data from SEFAZ transparency APIs
- Process and store fiscal contract data, management unit data, and other transparency information
- Provide scheduled data synchronization
- Expose REST endpoints for accessing the consumed data

## Technology Stack

- **Framework**: Spring Boot 3.3.3
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **API Documentation**: OpenAPI 3.0 (Swagger)

## SEFAZ API Endpoints

The application consumes data from:
- **Fiscal Contracts**: `https://api-transparencia.apps.sefaz.se.gov.br/gbp/v1/contrato-fiscais`
- **Management Units**: `https://api-transparencia.apps.sefaz.se.gov.br/gfu/v2/unidade-gestora`

## Database Schema

The application works with the `sco` (Sistema de Controle Orçamentário) schema, storing data in tables such as:
- `sco.contratos_fiscais`
- `sco.unidade_gestora`

## Configuration

Update `src/main/resources/application.properties` with your database configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8083`

### Health Check Endpoints

- **Application Health**: `GET http://localhost:8083/health`
- **Token Service Test**: `GET http://localhost:8083/health/token-test`

### Troubleshooting Startup Issues

If the application fails to start, check:

1. **Database Connection**: Ensure PostgreSQL is running and accessible
2. **Port Availability**: Make sure port 8083 is not in use
3. **Dependencies**: Run `mvn clean install` to ensure all dependencies are resolved

Common startup endpoints for testing:
- `http://localhost:8083/health` - Basic health check
- `http://localhost:8083/unidade-gestora/test` - Test endpoint configuration
- `http://localhost:8083/unidade-gestora` - Full API consumption

## API Documentation

Once running, access the Swagger UI at: `http://localhost:8083/swagger-ui.html`

## Scheduled Jobs

The application includes scheduled jobs to automatically sync data from SEFAZ APIs:

### Test Execution (Development)
- **Automatic execution**: 5 seconds after application startup
- **Manual execution**: `POST /scheduler/execute`
- **Status monitoring**: `GET /scheduler/status`

### Production Schedule (Configurable)
- **Daily execution**: 2:45 AM (disabled by default)
- **Frequent testing**: Every 10 minutes (disabled by default)
- **Configuration**: Enable via `application.properties`

### Scheduler Endpoints
- `GET /scheduler/ping` - Health check
- `GET /scheduler/info` - Configuration details
- `GET /scheduler/status` - Current status
- `POST /scheduler/execute` - Manual execution

### Data Processing Order
1. **Unidade Gestora** (Management Units) - Required first
2. **Contratos Fiscais** (Fiscal Contracts) - Depends on UG data
3. **Comprehensive logging** of all operations

## Development Status

This project is currently under development. Key features being implemented:
- Complete SEFAZ API integration
- Enhanced error handling and logging
- Additional endpoint DTOs
- Comprehensive testing
