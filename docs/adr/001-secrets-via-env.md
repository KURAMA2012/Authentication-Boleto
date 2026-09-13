# ADR 001: Secrets via environment variables

## Status

Accepted

## Context

Database and RabbitMQ passwords were committed in `application.properties` and docker-compose. That leaks credentials through git history and makes environment-specific config harder.

## Decision

- Keep non-secret local defaults (host, port, database name, usernames) in Spring property placeholders.
- Resolve passwords only from the environment, with an empty default.
- Document variables in `.env.example` (placeholders only).
- Ignore `.env`, `*.pem`, and `application-local.properties`.
- Do not add a dotenv library; Spring Boot and the IDE/shell supply the variables.

## Consequences

Developers must export env vars (or use an IDE env config) before running the backends. Docker Compose must be started with those same variables (for example `--env-file .env`). Existing clones need a one-time `.env` setup.
