# Capacitación Lab II / IV

Aplicación de reuniones con videollamadas (Jitsi Meet) desarrollada como
capacitación previa al proyecto Study Arena.

## Stack

- **Backend:** Java 21, Spring Boot, Spring Data JPA, Spring Security, Gradle (Groovy)
- **Frontend:** React, TypeScript, Vite, Axios
- **Base de datos:** PostgreSQL 16 (en Docker)

## Requisitos

- Docker Desktop
- JDK 21
- Node.js

## Cómo levantarlo

1. Clonar el repo
2. Copiar `.env.example` a `.env` (raíz) y `frontend/.env.example` a `frontend/.env`
3. Con Docker Desktop abierto, levantar la base desde la raíz:

       docker compose up -d

4. Abrir la carpeta `backend` y ejecutar `TrainingApplication`.
   Queda en `http://localhost:8080`
5. En otra terminal:

       cd frontend
       npm install
       npm run dev

Queda en `http://localhost:5173`

## Notas

- Postgres se expone en el puerto **5433** para evitar conflicto con
  instalaciones locales de PostgreSQL en el 5432
- La aplicación fuerza timezone UTC al arrancar: Windows reporta
  `America/Buenos_Aires`, un identificador que Postgres 16 ya no acepta
