package server

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"
	"strings"
	"sync"

	pb "example.com/api"
	"example.com/internal/database"
	"example.com/internal/hash"
	"example.com/internal/jwt"
	"github.com/jackc/pgx/v5/pgconn"
)

type Server struct {
	pb.UnimplementedAuthServiceServer
	mu     sync.RWMutex
	dbx    *database.DBX
	secret []byte
}

func (s *Server) RegisterUser(ctx context.Context, req *pb.RegisterData) (*pb.RegisterStatus, error) {
	login := strings.TrimSpace(req.GetLogin())
	password := strings.TrimSpace(req.GetPassword())
	phone := strings.TrimSpace(req.GetPhone())
	role := database.EnumToString(req.GetRole())
	encrypted_password := hash.EncryptPassword(password)
	if strings.Contains(login, " ") || strings.Contains(phone, " ") || strings.Contains(password, " ") {
		return &pb.RegisterStatus{Success: false, MsgError: "Недопустимые значения в данных."}, nil
	}
	user := database.UserFromDB{Login: login, Role: role, Phone: phone, Password: encrypted_password}

	s.mu.Lock()
	err := s.dbx.CreateUser(ctx, user)
	s.mu.Unlock()
	if err != nil {
		if pgErr, ok := err.(*pgconn.PgError); ok && pgErr.Code == "23505" {
			return &pb.RegisterStatus{Success: false, MsgError: "Пользователь с таким логином уже существует."}, nil
		} else {
			log.Printf("ошибка при регистрации пользователя: %v", err)
			return &pb.RegisterStatus{Success: false, MsgError: "Ошибка со стороны сервера."}, nil
		}
	}

	return &pb.RegisterStatus{Success: true, MsgError: ""}, nil
}

func (s *Server) AuthUser(ctx context.Context, req *pb.LoginRequest) (*pb.SessionToken, error) {
	login := req.GetLogin()
	password := req.GetPassword()

	s.mu.Lock()
	user, err := s.dbx.GetUserByLogin(ctx, login)
	s.mu.Unlock()
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return &pb.SessionToken{Success: false, MsgError: "Пользователь с таким логином не найден."}, nil
		} else {
			log.Printf("ошибка при поиске пользователя: %v", err)
			return &pb.SessionToken{Success: false, MsgError: fmt.Sprintf("%v", err)}, nil
		}
	}

	isValidPassword := hash.CheckPassword(user.Password, password)
	if !isValidPassword {
		return &pb.SessionToken{Success: false, MsgError: "Неправильно введен пароль."}, nil
	}

	s.mu.Lock()
	token, err := jwt.GenerateJWT(s.secret)
	s.mu.Unlock()
	if err != nil {
		return &pb.SessionToken{Success: false, MsgError: "Ошибка во время генерации токена."}, nil
	}
	s.mu.Lock()
	err = s.dbx.UpdateToken(ctx, token, login)
	s.mu.Unlock()
	if err != nil {
		return &pb.SessionToken{Success: false, MsgError: "Ошибка во время загрузки токена в дату базу."}, nil
	}
	return &pb.SessionToken{Success: true, Token: token, MsgError: ""}, nil
}

func (s *Server) GetUserData(ctx context.Context, req *pb.SessionToken) (*pb.UserData, error) {
	token := req.GetToken()

	s.mu.Lock()
	isActive := jwt.CheckToken(token, s.secret)
	user, err := s.dbx.GetUserByToken(ctx, token)
	s.mu.Unlock()
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			log.Println("Не найден токен в датабазе")
			return &pb.UserData{IsActive: false}, nil
		} else {
			log.Printf("ошибка при поиске пользователя: %v", err)
			return &pb.UserData{IsActive: false}, nil
		}
	}

	return &pb.UserData{IsActive: isActive, Login: user.Login, Role: database.StringToEnum(user.Role), Phone: user.Phone}, nil
}

func (s *Server) GiveRole(ctx context.Context, req *pb.UpdateRequest) (*pb.UpdateStatus, error) {
	login := req.GetLogin()
	role := database.EnumToString(req.GetRole())
	user := database.UserFromDB{Login: login, Role: role}

	s.mu.Lock()
	err := s.dbx.UpdateRole(ctx, user)
	s.mu.Unlock()
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return &pb.UpdateStatus{Success: false, MsgError: "Пользователь с таким логином не найден."}, nil
		} else {
			log.Printf("ошибка при обновлении роли пользователю: %v", err)
			return &pb.UpdateStatus{Success: false, MsgError: "Ошибка со стороны сервера."}, nil
		}
	}

	return &pb.UpdateStatus{Success: true, MsgError: ""}, nil
}
