package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"os"

	pb "example.com/api"
	"example.com/internal/database"
	"google.golang.org/grpc"
)

func main() {
	secret := os.Getenv("SECRET_KEY")
	server_port, db_port := os.Getenv("PORT"), os.Getenv("DB_PORT")
	db_user, db_pass, db_host, db_name := os.Getenv("DB_USER"), os.Getenv("DB_PASS"), os.Getenv("DB_HOST"), os.Getenv("DB_NAME")
	dsn := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", db_user, db_pass, db_host, db_port, db_name)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	db := database.StartDB(dsn, ctx)
	defer db.Close()

	listener, err := net.Listen("tcp", fmt.Sprintf(":%s", server_port))
	if err != nil {
		log.Printf("Ошибка запуска слушателя: %v", err)
	}

	grpcServer := grpc.NewServer()
	pb.RegisterAuthServiceServer(grpcServer, &Server{dbx: db, secret: []byte(secret)})
	log.Printf("Сервер запущен!")
	if err := grpcServer.Serve(listener); err != nil {
		log.Printf("Ошибка работы gRPC-сервера: %v", err)
	}
}
