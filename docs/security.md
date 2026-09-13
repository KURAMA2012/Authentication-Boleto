# Secrets and local configuration

Credentials must not be committed. Both Spring Boot backends read database and RabbitMQ settings from environment variables.

## Setup

1. Copy `.env.example` to `.env` at the repository root.
2. Fill in real values (passwords have no committed default).
3. Do **not** commit `.env`, `*.pem`, or `application-local.properties`.

Spring Boot does **not** load `.env` files natively. Export the variables before starting a backend, or set them in your IDE run configuration:

```bash
set -a && source .env && set +a
cd backend-authentication-boleto && ./mvnw spring-boot:run
```

Alternatively, create `src/main/resources/application-local.properties` (gitignored) and activate the `local` profile.

## Required variables

| Variable | Used by | Notes |
| --- | --- | --- |
| `DB_URL` | authentication-boleto | Default: `jdbc:postgresql://localhost:5432/bd_authentication_boleto` |
| `DB_USERNAME` | authentication-boleto | Default: `postgres` |
| `DB_PASSWORD` | authentication-boleto | **Required in practice** — empty default |
| `RABBITMQ_HOST` | both backends | Default: `localhost` |
| `RABBITMQ_PORT` | both backends | Default: `5672` |
| `RABBITMQ_USERNAME` | both backends + docker-compose | Default: `guest` |
| `RABBITMQ_PASSWORD` | both backends + docker-compose | **Required in practice** — empty default |

## RabbitMQ via Docker Compose

`backend-boleto-batch-service/docker-compose.yml` reads `RABBITMQ_USERNAME` and `RABBITMQ_PASSWORD` from the environment. From the repo root:

```bash
docker compose --env-file .env -f backend-boleto-batch-service/docker-compose.yml up -d
```

See also [ADR 001](adr/001-secrets-via-env.md).
