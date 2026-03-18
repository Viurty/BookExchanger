# BookExchanger

Веб-приложение для обмена книгами между пользователями.

## Что внутри

- `frontend/website` — SPA на `React` + `Vite`
- `backend/bookservice` — REST API на `Spring Boot`
- `backend/authservice` — сервис аутентификации на `Go` + `gRPC`
- `postgres` — единая база данных
- `nginx` — reverse proxy и единая точка входа

## Архитектура (понятно и по шагам)

### Как проходит запрос

1. Пользователь открывает `http://localhost`
2. Запрос попадает в `proxy` (`nginx`, порт `80`)
3. `nginx` маршрутизирует:
   - `/users`, `/books`, `/exchanges`, `/reviews` → `bookservice:8080`
   - всё остальное (`/`) → `frontend:80`
4. `bookservice` при операциях авторизации обращается в `auth-service` по `gRPC` (`8081`)
5. `bookservice` и `auth-service` работают с одной БД `postgres:5432`



### Карта сервисов и портов

| Сервис | Роль | Порты | Где описан |
|---|---|---|---|
| `proxy` | Входная точка, маршрутизация HTTP | `80:80` | `docker-compose.yml`, `nginx/default.conf` |
| `frontend` | UI-приложение | `80` (внутри сети) | `frontend/website/Dockerfile` |
| `bookservice` | REST API доменной логики | `8080` (внутри сети) | `backend/bookservice` |
| `auth-service` | gRPC аутентификация/роли | `8081:8081` | `backend/authservice` |
| `postgres` | Хранение данных | `5432:5432` | `docker-compose.yml` |
| `pgadmin` | Веб-интерфейс к БД | `5050:80` | `docker-compose.yml` |

## Технологический стек

- Frontend: `React`, `react-router-dom`, `Vite`
- Backend: `Spring Boot 3`, `Spring Data JPA`, `PostgreSQL`
- Auth: `Go`, `grpc-go`, `sqlx`, `bcrypt`, `JWT`
- Инфраструктура: `Docker`, `Docker Compose`, `Nginx`

## Структура репозитория

```text
backend/
  authservice/
  bookservice/
frontend/
  website/
nginx/
docker-compose.yml
README.md
```

## Быстрый старт (Docker)

### Требования

- Docker Engine / Docker Desktop
- Docker Compose v2+

### Запуск

```bash
docker compose up --build
```

### Проверка доступности

- Приложение: `http://localhost`
- pgAdmin: `http://localhost:5050`
- PostgreSQL: `localhost:5432`

### Полезные команды

```bash
docker compose ps
docker compose logs -f
docker compose down
```

## Локальная разработка (без Docker)

### 1) PostgreSQL

Поднимите PostgreSQL локально и укажите соответствующие параметры подключения.

### 2) `authservice` (`Go`)

```bash
cd backend/authservice
go mod download
go run ./cmd/server
```

Обязательные переменные окружения:

- `PORT`
- `DB_USER`
- `DB_PASS`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `SECRET_KEY`

### 3) `bookservice` (`Spring Boot`)

```bash
cd backend/bookservice
chmod +x gradlew
./gradlew bootRun
```

Ключевые параметры в `backend/bookservice/application.properties`:

- `spring.datasource.*`
- `grpc.auth-service-port`
- `grpc.auth-service-secret`

### 4) `frontend` (`React`)

```bash
cd frontend/website
cp .env.example .env
npm install
npm run dev
```

Ключевая переменная:

- `VITE_API_BASE_URL` — базовый URL API

## Основные API маршруты

### Пользователи

- `POST /users`
- `POST /users/token`
- `POST /users/valid`
- `POST /users/role/{code}`

### Книги

- `POST /books`
- `POST /books/append`
- `GET /books/top`
- `GET /books/{bookId}`
- `GET /books/user/{login}`
- `PUT /books/delete`

### Обмены и отзывы

- `GET/POST/PATCH /exchanges...`
- `GET/POST /reviews...`

Полные контракты: `backend/bookservice/src/main/java/.../controller`.

## Тестирование

### `bookservice`

```bash
cd backend/bookservice
chmod +x gradlew
./gradlew test
```

### `authservice`

Перед `go test` должны быть сгенерированы protobuf-файлы в `backend/authservice/api`:

```bash
cd backend/authservice
protoc --proto_path=api --go_out=api --go_opt=paths=source_relative --go-grpc_out=api --go-grpc_opt=paths=source_relative api/auth.proto
go test ./...
```

## Конфигурация и безопасность

- Реальные `.env` не коммитятся
- Шаблон фронта: `frontend/website/.env.example`
- Секреты (`SECRET_KEY`, пароли, служебные коды) не храните в репозитории

## Troubleshooting

### `protoc` ошибка на macOS (Homebrew + `abseil`)

```bash
brew reinstall abseil protobuf
```

### `gradlew` не запускается

```bash
cd backend/bookservice
chmod +x gradlew
./gradlew --version
```

Если `./gradlew test` падает:

```bash
./gradlew test --stacktrace --info
```

## Ссылка на ТЗ

`https://git.culab.ru/bsc-development-basics-2nd-semester/dev-basics-2025-longreads/-/tree/main/course-project?ref_type=heads`

