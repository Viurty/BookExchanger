package database

import (
	"context"
	"database/sql"

	"example.com/api"
)

type UserFromDB struct {
	Login    string `json:"login" db:"login"`
	Role     string `json:"role" db:"role"`
	Password string `json:"password" db:"password"`
	Phone    string `json:"phone" db:"phone"`
}

func EnumToString(role api.Role) string {
	switch role {
	case api.Role_ADMIN:
		return "ADMIN"
	default:
		return "USER"
	}
}

func StringToEnum(s string) api.Role {
	switch s {
	case "ADMIN":
		return api.Role_ADMIN
	default:
		return api.Role_USER
	}
}

func (d *DBX) Close() error {
	return d.dbx.Close()
}

func (d *DBX) GetUserByLogin(ctx context.Context, login string) (UserFromDB, error) {
	var user UserFromDB
	err := d.dbx.GetContext(ctx, &user, "SELECT * FROM users WHERE login=$1", login)
	return user, err
}

func (d *DBX) CreateUser(ctx context.Context, user UserFromDB) error {
	_, err := d.dbx.ExecContext(ctx, `INSERT INTO users (login, role, password, phone) VALUES ($1, $2, $3, $4);`, user.Login, user.Role, user.Password, user.Phone)
	return err
}

func (d *DBX) UpdateRole(ctx context.Context, user UserFromDB) error {
	res, err := d.dbx.ExecContext(ctx, "UPDATE users SET role = $1 WHERE login = $2;", user.Role, user.Login)
	rows, _ := res.RowsAffected()
	if rows == 0 {
		return sql.ErrNoRows
	}
	return err
}

func (d *DBX) UpdateToken(ctx context.Context, token, login string) error {
	res, err := d.dbx.ExecContext(ctx, "UPDATE users SET token = $1 WHERE login = $2;", token, login)
	rows, _ := res.RowsAffected()
	if rows == 0 {
		return sql.ErrNoRows
	}
	return err
}

func (d *DBX) GetUserByToken(ctx context.Context, token string) (UserFromDB, error) {
	var user UserFromDB
	err := d.dbx.GetContext(ctx, &user, "SELECT * FROM users WHERE token=$1", token)
	return user, err
}
