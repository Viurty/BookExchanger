package main

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"
	"net"
	"os"
	"sync"

	pb "example.com/api"
	"example.com/internal/database"
	"example.com/internal/hash"
	"github.com/lib/pq"
	"google.golang.org/grpc"
)

type server struct {
	pb.UnimplementedAuthServiceServer
	mu  sync.RWMutex
	dbx *database.DBX
}

func (s *server) RegisterUser(ctx context.Context, req *pb.LoginRequest) (*pb.AccessStatus, error) {
	login := req.GetLogin()
	password := req.GetPassword()
	encrypted_password := hash.EncryptPassword(password)
	user := database.UserFromDB{Login: login, Role: "user", Password: encrypted_password}

	s.mu.Lock()
	err := s.dbx.CreateUser(ctx, user)
	s.mu.Unlock()
	if err != nil {
		if pgErr, ok := err.(*pq.Error); ok && pgErr.Code == "23505" {
			return &pb.AccessStatus{Success: false, MsgError: "Пользователь с таким логином уже существует."}, nil
		} else {
			log.Fatalf("ошибка при регистрации пользователя: %v", err)
			return &pb.AccessStatus{Success: false, MsgError: "Ошибка со стороны сервера. Попробуйте еще раз."}, nil
		}
	}

	return &pb.AccessStatus{Success: true, Role: "user"}, nil
}

func (s *server) AuthUser(ctx context.Context, req *pb.LoginRequest) (*pb.AccessStatus, error) {
	login := req.GetLogin()
	password := req.GetPassword()

	s.mu.Lock()
	user, err := s.dbx.GetUser(ctx, login)
	s.mu.Unlock()
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return &pb.AccessStatus{Success: false, MsgError: "Пользователь с таким логином не найден."}, nil
		} else {
			log.Fatalf("ошибка при поиске пользователя: %v", err)
			return &pb.AccessStatus{Success: false, MsgError: "Ошибка со стороны сервера. Попробуйте еще раз."}, nil
		}
	}

	isValidPassword := hash.CheckPassword(user.Password, password)
	if !isValidPassword {
		return &pb.AccessStatus{Success: false, MsgError: "Неправильно введен пароль."}, nil
	}

	return &pb.AccessStatus{Success: true, Role: user.Role}, nil
}

func (s *server) GiveAccess(ctx context.Context, req *pb.LoginRequest) (*pb.RoleStatus, error) {
	login := req.GetLogin()
	role := req.GetRole()
	user := database.UserFromDB{Login: login, Role: role}

	s.mu.Lock()
	err := s.dbx.UpdateRole(ctx, user)
	s.mu.Unlock()
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return &pb.RoleStatus{Success: false, MsgError: "Пользователь с таким логином не найден."}, nil
		} else {
			log.Fatalf("ошибка при обновлении роли пользователю: %v", err)
			return &pb.RoleStatus{Success: false, MsgError: "Ошибка со стороны сервера. Попробуйте еще раз."}, nil
		}
	}

	return &pb.RoleStatus{Success: true}, nil
}

func main() {
	server_port, db_port := os.Getenv("PORT"), os.Getenv("DB_PORT")
	db_user, db_pass, db_host, db_name := os.Getenv("DB_USER"), os.Getenv("DB_PASS"), os.Getenv("DB_HOST"), os.Getenv("DB_NAME")
	dsn := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", db_user, db_pass, db_host, db_port, db_name)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	db := database.StartDB(dsn, ctx)
	defer db.Close()

	listener, err := net.Listen("tcp", fmt.Sprintf(":%s", server_port))
	if err != nil {
		log.Fatalf("Ошибка запуска слушателя: %v", err)
	}

	grpcServer := grpc.NewServer()
	pb.RegisterAuthServiceServer(grpcServer, &server{dbx: db})
	if err := grpcServer.Serve(listener); err != nil {
		log.Fatalf("Ошибка работы gRPC-сервера: %v", err)
	}
}
