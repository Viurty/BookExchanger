FROM golang:1.23.5-alpine AS builder

RUN apk update && \
      apk add --no-cache \
      git \
      protobuf protobuf-dev

RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.34.2
RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.5.1

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY api ./api

RUN protoc \
      --proto_path=api \
      --go_out=api --go_opt=paths=source_relative \
      --go-grpc_out=api --go-grpc_opt=paths=source_relative \
      api/auth.proto

COPY . .

RUN go build -o main ./cmd/server

FROM alpine:latest
WORKDIR /app

# Копируем бинарник из билдер-стадии
COPY --from=builder /app/main .

EXPOSE 8081

CMD ["./main"]
