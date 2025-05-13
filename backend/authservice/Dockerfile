FROM golang:1.23.5-alpine AS builder

RUN apk update && apk add --no-cache git

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o main ./cmd/server


FROM alpine:latest
WORKDIR /app
COPY --from=builder /app/main .
EXPOSE 8081
CMD ["./main"]
